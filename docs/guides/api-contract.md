# API 문서 작성 규칙

`@ApiContract` 인터페이스에 문서와 라우팅을 함께 선언하고, 컨트롤러는 해당 인터페이스를 구현합니다.

## 작성 규칙
- 요약, 필드 설명, 예시는 인터페이스 메서드의 `@ApiDoc`에 작성합니다.
- HTTP method와 메서드 path는 인터페이스 메서드의 `@GetMapping`, `@PostMapping` 등에서 추출합니다.
- base path는 구현 컨트롤러의 클래스 `@RequestMapping`에서 추출합니다.
- `@PathVariable`, `@RequestParam`, `@RequestBody`를 자동 추출합니다.
- 요청 DTO와 응답 DTO의 필드명/타입은 리플렉션으로 자동 추출합니다.
- `@PathVariable`, `@RequestParam` 설명도 `@ApiDoc.descriptions`에서 같은 이름으로 매핑합니다.
- `@NotNull`, `@NotBlank`, `@NotEmpty`가 붙은 필드는 `required`로 표기됩니다.
- `example.request`, `example.response`를 비워두면 타입 기반 더미 JSON을 자동 생성합니다.

## 권장 사항
- QueryDSL `Tuple`, `Map<String, Object>`, `Object` 같은 동적 타입은 컨트롤러 경계에서 직접 반환하지 말고 DTO로 변환합니다.
- 페이징 응답도 Spring `Page<T>`를 직접 노출하기보다 명시적 응답 DTO로 감싸는 편이 문서 품질이 안정적입니다.

## 문서 생성
```bash
./gradlew generateApiDocs
```

생성 결과는 `docs/api/`에 덮어써집니다. 삭제된 API 문서도 함께 정리되므로, 수동 편집은 `docs/api/` 밖에서 관리해야 합니다.
