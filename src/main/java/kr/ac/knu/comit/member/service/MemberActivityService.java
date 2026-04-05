package kr.ac.knu.comit.member.service;

import kr.ac.knu.comit.comment.domain.Comment;
import kr.ac.knu.comit.comment.domain.CommentRepository;
import kr.ac.knu.comit.comment.service.CommentQueryService;
import kr.ac.knu.comit.member.dto.MyCursorPageResponse;
import kr.ac.knu.comit.member.dto.MyCommentResponse;
import kr.ac.knu.comit.post.domain.Post;
import kr.ac.knu.comit.post.domain.PostImage;
import kr.ac.knu.comit.post.domain.PostImageRepository;
import kr.ac.knu.comit.post.domain.PostLike;
import kr.ac.knu.comit.post.domain.PostLikeRepository;
import kr.ac.knu.comit.post.domain.PostRepository;
import kr.ac.knu.comit.post.dto.PostSummaryResponse;
import kr.ac.knu.comit.post.service.ContentPreviewGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberActivityService {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostImageRepository postImageRepository;
    private final CommentRepository commentRepository;
    private final CommentQueryService commentQueryService;
    private final ContentPreviewGenerator contentPreviewGenerator;

    public MyCursorPageResponse<PostSummaryResponse> getMyPosts(Long memberId, Long cursorId, int size) {
        int pageSize = Math.min(size, DEFAULT_PAGE_SIZE);
        List<Post> posts = postRepository.findActiveByMemberId(memberId, cursorId, PageRequest.of(0, pageSize + 1));

        boolean hasNext = posts.size() > pageSize;
        List<Post> visible = hasNext ? posts.subList(0, pageSize) : posts;
        Long nextCursorId = hasNext ? visible.get(visible.size() - 1).getId() : null;

        List<Long> postIds = visible.stream().map(Post::getId).toList();
        Map<Long, Integer> commentCounts = commentQueryService.countActiveCommentsByPostIds(postIds);
        Map<Long, List<String>> imageUrls = imageUrlsByPostId(postIds);

        List<PostSummaryResponse> items = visible.stream()
                .map(p -> PostSummaryResponse.from(
                        p,
                        commentCounts.getOrDefault(p.getId(), 0),
                        imageUrls.getOrDefault(p.getId(), Collections.emptyList()),
                        contentPreviewGenerator.generate(p.getContent())
                ))
                .toList();

        return new MyCursorPageResponse<>(items, nextCursorId, hasNext);
    }

    public MyCursorPageResponse<MyCommentResponse> getMyComments(Long memberId, Long cursorId, int size) {
        int pageSize = Math.min(size, DEFAULT_PAGE_SIZE);
        List<Comment> comments = commentRepository.findActiveByMemberId(memberId, cursorId, PageRequest.of(0, pageSize + 1));

        boolean hasNext = comments.size() > pageSize;
        List<Comment> visible = hasNext ? comments.subList(0, pageSize) : comments;
        Long nextCursorId = hasNext ? visible.get(visible.size() - 1).getId() : null;

        List<Long> postIds = visible.stream().map(c -> c.getPost().getId()).distinct().toList();
        Map<Long, Integer> postCommentCounts = commentQueryService.countActiveCommentsByPostIds(postIds);

        List<MyCommentResponse> items = visible.stream()
                .map(c -> MyCommentResponse.from(c, postCommentCounts.getOrDefault(c.getPost().getId(), 0)))
                .toList();

        return new MyCursorPageResponse<>(items, nextCursorId, hasNext);
    }

    public MyCursorPageResponse<PostSummaryResponse> getMyLikes(Long memberId, Long cursorId, int size) {
        int pageSize = Math.min(size, DEFAULT_PAGE_SIZE);
        List<PostLike> likes = postLikeRepository.findByMemberId(memberId, cursorId, PageRequest.of(0, pageSize + 1));

        boolean hasNext = likes.size() > pageSize;
        List<PostLike> visibleLikes = hasNext ? likes.subList(0, pageSize) : likes;
        Long nextCursorId = hasNext ? visibleLikes.get(visibleLikes.size() - 1).getId() : null;

        List<Long> postIds = visibleLikes.stream().map(PostLike::getPostId).toList();
        List<Post> posts = postRepository.findActiveWithMemberAndTagsByIds(postIds);

        Map<Long, Post> postsById = posts.stream().collect(Collectors.toMap(Post::getId, p -> p));
        List<Long> activePostIds = posts.stream().map(Post::getId).toList();
        Map<Long, Integer> commentCounts = commentQueryService.countActiveCommentsByPostIds(activePostIds);
        Map<Long, List<String>> imageUrls = imageUrlsByPostId(activePostIds);

        // 좋아요한 순서(postIds 순서) 유지, 삭제된 게시글은 제외
        List<PostSummaryResponse> items = postIds.stream()
                .filter(postsById::containsKey)
                .map(pid -> {
                    Post p = postsById.get(pid);
                    return PostSummaryResponse.from(
                            p,
                            commentCounts.getOrDefault(pid, 0),
                            imageUrls.getOrDefault(pid, Collections.emptyList()),
                            contentPreviewGenerator.generate(p.getContent())
                    );
                })
                .toList();

        return new MyCursorPageResponse<>(items, nextCursorId, hasNext);
    }

    private Map<Long, List<String>> imageUrlsByPostId(List<Long> postIds) {
        if (postIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return postImageRepository.findByPost_IdInOrderBySortOrderAsc(postIds)
                .stream()
                .collect(Collectors.groupingBy(
                        PostImage::getPostId,
                        Collectors.mapping(PostImage::getImageUrl, Collectors.toList())
                ));
    }
}
