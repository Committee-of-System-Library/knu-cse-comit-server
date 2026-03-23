package kr.ac.knu.comit.post.domain;

import jakarta.persistence.*;
import kr.ac.knu.comit.global.exception.BusinessErrorCode;
import kr.ac.knu.comit.global.exception.BusinessException;
import kr.ac.knu.comit.member.domain.Member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BoardType boardType;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private int likeCount = 0;

    // 태그는 Post 생명주기에 종속 → CascadeType.ALL + orphanRemoval
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTag> tags = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    protected Post() {
    }

    // ── 정적 팩토리 ──────────────────────────────────────────────────────────

    public static Post create(Member author, BoardType boardType, String title, String content,
                              List<String> tagNames) {
        validateTitle(title);
        validateContent(content);
        validateTagNames(tagNames);

        Post post = new Post();
        post.member = author;
        post.boardType = boardType;
        post.title = title;
        post.content = content;
        post.createdAt = LocalDateTime.now();

        tagNames.forEach(name -> post.tags.add(PostTag.of(post, name)));
        return post;
    }

    // ── 상태 변경 ─────────────────────────────────────────────────────────────

    public void update(String title, String content, List<String> tagNames) {
        validateTitle(title);
        validateContent(content);
        validateTagNames(tagNames);

        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();

        // 태그 전체 교체: orphanRemoval이 삭제 처리
        this.tags.clear();
        tagNames.forEach(name -> this.tags.add(PostTag.of(this, name)));
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    // ── 도메인 판단 ───────────────────────────────────────────────────────────

    public boolean isWrittenBy(Long memberId) {
        return this.member.getId().equals(memberId);
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public BoardType getBoardType() {
        return boardType;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public List<PostTag> getTags() {
        return tags;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // ── 도메인 규칙 검증 (private) ────────────────────────────────────────────

    private static void validateTitle(String title) {
        if (title == null || title.isBlank() || title.length() > 255) {
            throw new BusinessException(BusinessErrorCode.INVALID_TITLE);
        }
    }

    private static void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new BusinessException(BusinessErrorCode.INVALID_CONTENT);
        }
    }

    private static void validateTagNames(List<String> tagNames) {
        if (tagNames == null) return;
        if (tagNames.size() > 5) {
            throw new BusinessException(BusinessErrorCode.TAG_LIMIT_EXCEEDED);
        }
        tagNames.forEach(PostTag::validateName);
    }
}
