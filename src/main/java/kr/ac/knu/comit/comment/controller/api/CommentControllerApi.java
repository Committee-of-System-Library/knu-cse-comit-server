package kr.ac.knu.comit.comment.controller.api;

import jakarta.validation.Valid;
import kr.ac.knu.comit.comment.dto.CommentListResponse;
import kr.ac.knu.comit.comment.dto.CreateCommentRequest;
import kr.ac.knu.comit.comment.dto.HelpfulToggleResponse;
import kr.ac.knu.comit.comment.dto.UpdateCommentRequest;
import kr.ac.knu.comit.global.docs.annotation.ApiContract;
import kr.ac.knu.comit.global.docs.annotation.ApiDoc;
import kr.ac.knu.comit.global.docs.annotation.FieldDesc;
import kr.ac.knu.comit.global.auth.AuthenticatedMember;
import kr.ac.knu.comit.global.auth.MemberPrincipal;
import kr.ac.knu.comit.global.exception.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@ApiContract
public interface CommentControllerApi {

    @ApiDoc(
            summary = "댓글 목록 조회",
            descriptions = {
                    @FieldDesc(name = "postId", value = "댓글을 조회할 게시글 ID"),
                    @FieldDesc(name = "content", value = "댓글 내용"),
                    @FieldDesc(name = "authorNickname", value = "댓글 작성자 닉네임"),
                    @FieldDesc(name = "helpfulCount", value = "도움이 됐어요 수"),
                    @FieldDesc(name = "helpfulByMe", value = "현재 로그인한 사용자의 도움이 됐어요 여부"),
                    @FieldDesc(name = "mine", value = "현재 로그인한 사용자가 작성한 댓글인지 여부")
            }
    )
    @GetMapping("/posts/{postId}/comments")
    ResponseEntity<ApiResponse<CommentListResponse>> getComments(
            @PathVariable Long postId,
            @AuthenticatedMember MemberPrincipal principal
    );

    @ApiDoc(
            summary = "댓글 작성",
            descriptions = {
                    @FieldDesc(name = "postId", value = "댓글을 작성할 게시글 ID"),
                    @FieldDesc(name = "content", value = "댓글 본문")
            }
    )
    @PostMapping("/posts/{postId}/comments")
    ResponseEntity<ApiResponse<Long>> createComment(
            @PathVariable Long postId,
            @RequestBody @Valid CreateCommentRequest request,
            @AuthenticatedMember MemberPrincipal principal
    );

    @ApiDoc(
            summary = "댓글 수정",
            descriptions = {
                    @FieldDesc(name = "commentId", value = "수정할 댓글 ID"),
                    @FieldDesc(name = "content", value = "수정할 댓글 본문")
            }
    )
    @PatchMapping("/comments/{commentId}")
    ResponseEntity<ApiResponse<Void>> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Valid UpdateCommentRequest request,
            @AuthenticatedMember MemberPrincipal principal
    );

    @ApiDoc(
            summary = "댓글 삭제",
            descriptions = {
                    @FieldDesc(name = "commentId", value = "삭제할 댓글 ID")
            }
    )
    @DeleteMapping("/comments/{commentId}")
    ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @AuthenticatedMember MemberPrincipal principal
    );

    @ApiDoc(
            summary = "댓글 도움이 됐어요 토글",
            descriptions = {
                    @FieldDesc(name = "commentId", value = "도움이 됐어요를 토글할 댓글 ID"),
                    @FieldDesc(name = "helpful", value = "true면 도움이 됐어요가 추가되고 false면 취소됩니다.")
            }
    )
    @PostMapping("/comments/{commentId}/helpful")
    ResponseEntity<ApiResponse<HelpfulToggleResponse>> toggleHelpful(
            @PathVariable Long commentId,
            @AuthenticatedMember MemberPrincipal principal
    );
}
