# docs/

프로젝트 문서 루트. 큰 작업이 들어갔을 때는 코드만 바꾸지 말고 이 폴더의 관련 문서도 같이 갱신한다.

## 먼저 볼 문서

- [API 계약 가이드](/Users/bohyeong/IdeaProjects/knu-cse-comit-server/docs/guides/api-contract.md)
- [API 문서 생성기 동작 가이드](/Users/bohyeong/IdeaProjects/knu-cse-comit-server/docs/guides/api-doc-generator-flow.md)
- [Java 주석 규칙](/Users/bohyeong/IdeaProjects/knu-cse-comit-server/docs/guides/java-comment-convention.md)
- [로컬 실행 가이드](/Users/bohyeong/IdeaProjects/knu-cse-comit-server/docs/ops/local-development.md)
- [ProblemDetail 에러 응답 설계](/Users/bohyeong/IdeaProjects/knu-cse-comit-server/docs/features/problem-detail-error-response.md)

## 디렉토리 구조

| 폴더 | 용도 |
|---|---|
| `adr/` | 중요한 기술 결정과 대안 비교 |
| `guides/` | 개발자가 따라야 하는 규칙, 작성법, 내부 구조 설명 |
| `features/` | 기능 설계, 전환 계획, 예외 케이스 |
| `ops/` | 로컬 실행, 배포, 환경변수, 운영 체크리스트 |
| `api/` | `./gradlew generateApiDocs`로 생성되는 산출물 |

## 문서 갱신 기준

- 새 API surface를 추가하거나 바꾸면 `guides/api-contract.md`와 `docs/api/`를 함께 본다.
- 생성기 구조를 바꾸면 `guides/api-doc-generator-flow.md`를 갱신한다.
- 임시 브리지나 구현 제약을 도입하면 `guides/java-comment-convention.md` 기준으로 Javadoc과 기능 문서를 같이 남긴다.
- 로컬 실행 방식이나 필수 환경값이 바뀌면 `ops/` 문서를 갱신한다.
