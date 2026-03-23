package kr.ac.knu.comit.comment.service;

import java.util.List;
import java.util.Set;
import kr.ac.knu.comit.comment.domain.Comment;
import kr.ac.knu.comit.comment.domain.CommentHelpfulRepository;
import kr.ac.knu.comit.comment.domain.CommentRepository;
import kr.ac.knu.comit.comment.dto.CommentListResponse;
import kr.ac.knu.comit.comment.dto.CommentResponse;
import kr.ac.knu.comit.comment.dto.CreateCommentRequest;
import kr.ac.knu.comit.comment.dto.HelpfulToggleResponse;
import kr.ac.knu.comit.comment.dto.UpdateCommentRequest;
import kr.ac.knu.comit.global.exception.BusinessErrorCode;
import kr.ac.knu.comit.global.exception.BusinessException;
import kr.ac.knu.comit.member.domain.Member;
import kr.ac.knu.comit.member.service.MemberService;
import kr.ac.knu.comit.post.domain.Post;
import kr.ac.knu.comit.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentHelpfulRepository commentHelpfulRepository;
    private final MemberService memberService;
    private final PostService postService;

    public CommentListResponse getComments(Long postId, Long memberId) {
        postService.getActivePostOrThrow(postId);
        List<Comment> comments = commentRepository.findActiveByPostId(postId);
        if (comments.isEmpty()) {
            return new CommentListResponse(List.of());
        }

        Set<Long> helpfulIds = Set.copyOf(commentHelpfulRepository.findHelpfulCommentIds(
                memberId,
                comments.stream().map(Comment::getId).toList()
        ));

        return new CommentListResponse(
                comments.stream()
                        .map(comment -> CommentResponse.from(
                                comment,
                                helpfulIds.contains(comment.getId()),
                                comment.isWrittenBy(memberId)
                        ))
                        .toList()
        );
    }

    @Transactional
    public Long createComment(Long postId, Long memberId, CreateCommentRequest request) {
        Post post = postService.getActivePostOrThrow(postId);
        Member author = memberService.findMemberOrThrow(memberId);
        Comment comment = Comment.create(post, author, request.content());
        return commentRepository.save(comment).getId();
    }

    @Transactional
    public void updateComment(Long commentId, Long memberId, UpdateCommentRequest request) {
        Comment comment = findCommentOrThrow(commentId);
        checkOwnership(comment, memberId);
        comment.update(request.content());
    }

    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = findCommentOrThrow(commentId);
        checkOwnership(comment, memberId);
        comment.delete();
    }

    @Transactional
    public HelpfulToggleResponse toggleHelpful(Long commentId, Long memberId) {
        findCommentOrThrow(commentId);

        int inserted = commentHelpfulRepository.insertIgnore(commentId, memberId);
        if (inserted == 1) {
            commentRepository.incrementHelpfulCount(commentId);
            return HelpfulToggleResponse.helpfulState();
        }

        commentHelpfulRepository.deleteByCommentIdAndMemberId(commentId, memberId);
        commentRepository.decrementHelpfulCount(commentId);
        return HelpfulToggleResponse.notHelpfulState();
    }

    private Comment findCommentOrThrow(Long commentId) {
        return commentRepository.findActiveById(commentId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.COMMENT_NOT_FOUND));
    }

    private void checkOwnership(Comment comment, Long memberId) {
        if (!comment.isWrittenBy(memberId)) {
            throw new BusinessException(BusinessErrorCode.FORBIDDEN);
        }
    }
}
