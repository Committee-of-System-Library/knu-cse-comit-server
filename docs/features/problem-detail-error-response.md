# problem-detail-error-response

## 개요
Spring Boot 4의 `ProblemDetail`을 기준으로 에러 응답을 표준화하는 설계안.
성공 응답과 에러 응답을 분리하고, 프론트엔드가 `type`과 `errorCode`만으로 바로 분기할 수 있게 만드는 것이 목표다.

## 목표
- 비즈니스 에러, validation 에러, 시스템 에러를 같은 구조로 노출한다.
- 클라이언트가 `errorCode`만 보고 UI 분기를 할 수 있게 한다.
- RFC 9457 표준 필드(`type`, `title`, `status`, `detail`, `instance`)를 유지한다.
- 서버 내부 예외 메시지와 스택 트레이스는 외부에 노출하지 않는다.
- 현재 `ApiResponse.error(...)` 기반 구현을 점진적으로 대체할 수 있게 한다.

## 응답 구조

### 비즈니스 에러
```json
{
  "type": "https://api.comit.kr/problems/member/duplicate-nickname",
  "title": "Conflict",
  "status": 409,
  "detail": "이미 사용 중인 닉네임입니다.",
  "instance": "/members/me",
  "errorCode": "DUPLICATE_NICKNAME",
  "timestamp": "2026-03-24T03:10:00Z"
}
```

### Validation 에러
```json
{
  "type": "https://api.comit.kr/problems/common/invalid-request",
  "title": "Bad Request",
  "status": 400,
  "detail": "입력값이 올바르지 않습니다.",
  "instance": "/posts",
  "errorCode": "INVALID_REQUEST",
  "invalidFields": [
    {
      "field": "title",
      "message": "제목을 입력해주세요."
    }
  ],
  "timestamp": "2026-03-24T03:10:00Z"
}
```

### 시스템 에러
```json
{
  "type": "https://api.comit.kr/problems/common/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "서버 내부 오류가 발생했습니다. 관리자에게 문의해주세요.",
  "instance": "/posts/1",
  "errorCode": "INTERNAL_SERVER_ERROR",
  "errorTrackingId": "4a2b99f8-6e5d-4ce9-a86e-2d1f5f1fdb65",
  "timestamp": "2026-03-24T03:10:00Z"
}
```

## 구조

### 패키지
```text
global/exception/
├── ErrorCode.java
├── CommonErrorCode.java
├── MemberErrorCode.java
├── PostErrorCode.java
├── CommentErrorCode.java
├── BusinessException.java
├── ProblemDetailFactory.java
├── ProblemFieldViolation.java
└── GlobalExceptionHandler.java
```

### 책임
- `ErrorCode`: 문제 유형 URI, HTTP 상태, 기본 메시지, 문자열 에러 코드의 공통 계약
- 도메인별 `*ErrorCode`: 도메인 안에서만 쓰는 에러 정의
- `BusinessException`: 서비스 계층에서 던지는 단일 예외
- `ProblemDetailFactory`: `ProblemDetail` 생성과 확장 필드 주입
- `GlobalExceptionHandler`: 예외 카테고리별 응답 조립

### 의존 방향
```text
controller
  -> service
  -> BusinessException(ErrorCode)
  -> GlobalExceptionHandler
  -> ProblemDetailFactory
  -> response json
```

### 예외 변환 흐름
1. Controller는 입력을 받고 Service를 한 번 호출한다.
2. Service는 비즈니스 규칙 위반 시 `BusinessException`만 던진다.
3. `BusinessException`은 반드시 `ErrorCode`를 가진다.
4. `GlobalExceptionHandler`는 예외 종류별로 `ProblemDetailFactory`를 호출한다.
5. `ProblemDetailFactory`는 RFC 9457 기본 필드와 `errorCode`, `invalidFields`, `errorTrackingId` 같은 확장 필드를 채운다.
6. Controller 밖으로는 항상 표준화된 JSON만 나간다.

## 설계 원칙
- `type`을 1차 식별자로 사용한다.
- `errorCode`는 프론트 분기와 로그 검색을 위한 짧은 문자열로 유지한다.
- 성공 응답은 기존 DTO 또는 별도 success envelope로 유지하고, 에러 응답에만 `ProblemDetail`을 쓴다.
- `invalidFields[].field`는 단순 필드명 대신 중첩 경로를 허용한다.
- 도메인별 에러 코드는 enum을 나누고 `ErrorCode` 인터페이스로 묶는다.

## 전환 순서
1. `ErrorCode` 계약을 `ProblemDetail` 기준 필드까지 확장한다.
2. `GlobalExceptionHandler`에서 `ApiResponse.error(...)` 대신 `ProblemDetail`을 반환한다.
3. validation 에러를 `invalidFields` 배열 구조로 표준화한다.
4. 500 에러에 `errorTrackingId`를 추가하고 서버 로그와 연결한다.
5. API 문서 예시를 새 에러 포맷 기준으로 갱신한다.

## 현재 구조와 목표 구조
| 현재 | 목표 |
|---|---|
| `BusinessErrorCode` 하나에 모든 에러 코드 집중 | 도메인별 `*ErrorCode` enum으로 분리 |
| `ApiResponse.error(...)` 기반 응답 | `ProblemDetail` 기반 표준 응답 |
| validation 에러 구조가 단순 문자열 위주 | `invalidFields[]`로 필드 단위 노출 |
| 500 에러에 추적 키 없음 | `errorTrackingId`로 로그와 연결 |

## 남은 결정사항
- `type` URI를 절대 URL로 둘지, 상대 경로(`/problems/...`)로 둘지
- 성공 응답을 지금처럼 `ApiResponse`로 유지할지, 성공도 별도 표준 포맷으로 맞출지
- `errorTrackingId`를 MDC 기반으로 만들지, 핸들러에서 UUID를 즉시 생성할지
- 실제 SSO starter 연동 이후 인증/인가 에러 코드를 어느 도메인 enum에 둘지
