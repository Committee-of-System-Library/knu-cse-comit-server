package kr.ac.knu.comit.post.service;

import kr.ac.knu.comit.global.exception.BusinessErrorCode;
import kr.ac.knu.comit.global.exception.BusinessException;
import kr.ac.knu.comit.comment.service.CommentQueryService;
import kr.ac.knu.comit.member.domain.Member;
import kr.ac.knu.comit.member.service.MemberService;
import kr.ac.knu.comit.post.domain.*;
import kr.ac.knu.comit.post.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final MemberService memberService;
    private final CommentQueryService commentQueryService;

    // ── 조회 ──────────────────────────────────────────────────────────────────

    /**
     * No-offset cursor 페이지네이션.
     *
     * cursorId == null → 첫 페이지 (최신순 상위 N개)
     * cursorId != null → 해당 id 미만의 다음 페이지
     *
     * JOIN FETCH로 member를 함께 로드 → 작성자 닉네임 N+1 없음.
     */
    public PostCursorPageResponse getPosts(BoardType boardType, Long cursorId, int size) {
        int pageSize = Math.min(size, DEFAULT_PAGE_SIZE);
        PageRequest pageable = PageRequest.of(0, pageSize);

        List<Post> posts = (cursorId == null)
                ? postRepository.findFirstPage(boardType, pageable)
                : postRepository.findByCursor(boardType, cursorId, pageable);

        return PostCursorPageResponse.of(
                posts,
                pageSize,
                commentQueryService.countActiveCommentsByPostIds(posts.stream().map(Post::getId).toList())
        );
    }

    public PostDetailResponse getPost(Long postId, Long memberId) {
        Post post = findPostOrThrow(postId);
        boolean likedByMe = postLikeRepository.existsByPostIdAndMemberId(postId, memberId);
        return PostDetailResponse.of(post, likedByMe);
    }

    // ── 쓰기 ──────────────────────────────────────────────────────────────────

    @Transactional
    public Long createPost(Long memberId, CreatePostRequest request) {
        Member author = memberService.findMemberOrThrow(memberId);
        Post post = Post.create(author, request.boardType(), request.title(),
                request.content(), request.tags());
        return postRepository.save(post).getId();
    }

    @Transactional
    public void updatePost(Long memberId, Long postId, UpdatePostRequest request) {
        Post post = findPostOrThrow(postId);
        checkOwnership(post, memberId);
        post.update(request.title(), request.content(), request.tags());
    }

    @Transactional
    public void deletePost(Long memberId, boolean admin, Long postId) {
        Post post = findPostOrThrow(postId);

        if (!post.isWrittenBy(memberId) && !admin) {
            throw new BusinessException(BusinessErrorCode.FORBIDDEN);
        }
        post.delete();
    }

    // ── 좋아요 토글 ─────────────────────────────────────────────────────────────
    //
    // [동시성 설계]
    // 1. INSERT IGNORE (nativeQuery) → DB UNIQUE(post_id, member_id) 제약으로 원자적 삽입
    //    반환값 1 = 삽입 성공(좋아요), 0 = 이미 존재(좋아요 취소)
    //
    // 2. 카운터 증감은 JPQL UPDATE "likeCount = likeCount ± 1" → DB 원자적 연산
    //    애플리케이션 레벨 read-modify-write 없음 → lost update 불가능
    //
    // 동시에 두 요청이 INSERT IGNORE를 실행하면:
    //   - 첫 번째: 삽입 성공(1) → increment
    //   - 두 번째: 삽입 무시(0) → delete + decrement
    //   → 결과적으로 좋아요 없음 (정합성 유지)

    @Transactional
    public LikeToggleResponse toggleLike(Long memberId, Long postId) {
        findPostOrThrow(postId);

        int inserted = postLikeRepository.insertIgnore(postId, memberId);

        if (inserted == 1) {
            postRepository.incrementLikeCount(postId);
            return LikeToggleResponse.likedState();
        } else {
            postLikeRepository.deleteByPostIdAndMemberId(postId, memberId);
            postRepository.decrementLikeCount(postId);
            return LikeToggleResponse.unlikedState();
        }
    }

    // ── 내부 헬퍼 (private) ───────────────────────────────────────────────────

    public Post getActivePostOrThrow(Long postId) {
        return findPostOrThrow(postId);
    }

    private Post findPostOrThrow(Long postId) {
        return postRepository.findActiveById(postId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.POST_NOT_FOUND));
    }

    private void checkOwnership(Post post, Long memberId) {
        if (!post.isWrittenBy(memberId)) {
            throw new BusinessException(BusinessErrorCode.FORBIDDEN);
        }
    }
}
