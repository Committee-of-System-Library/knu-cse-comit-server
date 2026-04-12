# 트랜잭션 컨벤션

이 문서는 COMIT 서버에서 `@Transactional`을 어디에, 어떤 기준으로 선언할지 정리한 기준이다.

## 배경

OSIV(`spring.jpa.open-in-view`)가 `false`인 환경에서 불필요한 트랜잭션은 DB 커넥션 점유 시간을 늘린다.

특히 클래스 레벨 `@Transactional(readOnly = true)`는 두 가지 문제를 만든다.

1. **의도치 않은 상속** — write 메서드가 readOnly 트랜잭션을 물려받아 예외가 발생하거나, 메서드 레벨 선언으로 의도적으로 오버라이드해야 하는 번거로움이 생긴다.
2. **불필요한 DB 요청** — 단건 조회는 `SimpleJpaRepository` 내부에 이미 `@Transactional(readOnly = true)`가 선언되어 있다. 서비스 레벨에서 추가로 감싸면 `SET autocommit`, `SET SESSION TRANSACTION READ ONLY`, `COMMIT` 등 최대 6개의 불필요한 DB 네트워크 요청이 발생한다. ([카카오페이 기술 블로그 참고](https://tech.kakaopay.com/post/jpa-transactional-bri/))

## 규칙

### 1. 클래스 레벨 `@Transactional` 선언 금지

클래스 레벨 선언을 두지 않는다. 모든 트랜잭션은 메서드 단위로 명시한다.

```java
// 나쁜 예
@Service
@Transactional(readOnly = true)
public class PostService { ... }

// 좋은 예
@Service
public class PostService { ... }
```

### 2. 단건 조회 메서드에 `@Transactional` 선언 금지

`findById`, `existsBy`, `countBy` 등 단건·단순 조회는 `SimpleJpaRepository` 내부 트랜잭션으로 충분하다.

```java
// 나쁜 예
@Transactional(readOnly = true)
public Member getMyProfile(Long memberId) {
    return memberRepository.findById(memberId).orElseThrow(...);
}

// 좋은 예
public Member getMyProfile(Long memberId) {
    return memberRepository.findById(memberId).orElseThrow(...);
}
```

### 3. 아래 경우에만 `@Transactional` 명시

| 케이스 | 이유 |
|---|---|
| 여러 write 작업의 원자성이 필요한 경우 | 부분 성공 방지 |
| dirty checking으로 update가 발생하는 경우 | 영속성 컨텍스트 내 변경 감지 필요 |
| 엔티티를 로드한 뒤 그 참조로 다른 엔티티를 저장하는 경우 | 같은 영속성 컨텍스트 보장 필요 |

단건 `save()`는 `SimpleJpaRepository` 내부 트랜잭션으로 충분하다.

## 케이스별 판단 기준

```
조회만 한다
  └─ 단건 조회 (findById 등)         → @Transactional 없음
  └─ 여러 쿼리를 묶어 읽어야 한다     → @Transactional (논의 후 결정)

저장/수정/삭제한다
  └─ 단건 save()                     → @Transactional 없음
  └─ entity.상태변경() — dirty check  → @Transactional
  └─ 여러 write가 원자적이어야 한다   → @Transactional
  └─ 엔티티 로드 후 연관 엔티티 저장  → @Transactional
```

## 예시

### dirty checking — `@Transactional` 필요

```java
// post.hideByAdmin() 이 영속성 컨텍스트 안에서 변경 감지되어야 함
@Transactional
public void hidePost(Long postId) {
    Post post = findPostOrThrow(postId);
    post.hideByAdmin();
}
```

### 여러 write 원자성 — `@Transactional` 필요

```java
// insertIgnore + incrementLikeCount 가 하나의 단위로 묶여야 함
@Transactional
public LikeToggleResponse toggleLike(Long postId, Long memberId) {
    int inserted = postLikeRepository.insertIgnore(postId, memberId);
    if (inserted == 1) {
        postRepository.incrementLikeCount(postId);
        return LikeToggleResponse.likedState();
    }
    postLikeRepository.deleteByPostIdAndMemberId(postId, memberId);
    postRepository.decrementLikeCount(postId);
    return LikeToggleResponse.unlikedState();
}
```

### 엔티티 로드 후 연관 엔티티 저장 — `@Transactional` 필요

```java
// Member를 로드하고 그 참조를 Post에 넣어 저장하므로 같은 영속성 컨텍스트 필요
@Transactional
public Long createPost(Long memberId, CreatePostRequest request) {
    Member author = findActiveMemberOrThrow(memberId);
    Post post = Post.create(author, request.boardType(), request.title(), ...);
    return postRepository.save(post).getId();
}
```

### 단건 save() — `@Transactional` 불필요

```java
// SimpleJpaRepository.save() 내부에 이미 @Transactional 선언됨
public Long createComment(Long postId, Long memberId, CreateCommentRequest request) {
    Post post = findActivePostOrThrow(postId);
    Member author = findMemberOrThrow(memberId);
    Comment comment = Comment.create(post, author, request.content());
    return commentRepository.save(comment).getId();
}
```

## 체크리스트

- 클래스 레벨 `@Transactional`이 선언되어 있지 않은가
- 단순 조회 메서드에 `@Transactional(readOnly = true)`가 붙어 있지 않은가
- `@Transactional`을 선언했다면 위 3가지 기준 중 하나에 해당하는가
- 같은 서비스 내에서 비슷한 메서드 간 `@Transactional` 선언이 일관성 있게 맞춰져 있는가
