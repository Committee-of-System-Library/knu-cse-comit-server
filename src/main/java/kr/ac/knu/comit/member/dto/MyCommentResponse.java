package kr.ac.knu.comit.member.dto;

import kr.ac.knu.comit.comment.domain.Comment;

import java.time.LocalDateTime;

public record MyCommentResponse(
        Long id,
        String content,
        Long postId,
        String postTitle,
        int postLikeCount,
        int postCommentCount,
        LocalDateTime createdAt
) {
    public static MyCommentResponse from(Comment comment, int postCommentCount) {
        return new MyCommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getPost().getId(),
                comment.getPost().getTitle(),
                comment.getPost().getLikeCount(),
                postCommentCount,
                comment.getCreatedAt()
        );
    }
}
