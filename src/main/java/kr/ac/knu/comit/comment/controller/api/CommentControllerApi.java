package kr.ac.knu.comit.comment.controller.api;

import jakarta.validation.Valid;
import kr.ac.knu.comit.comment.dto.CommentListResponse;
import kr.ac.knu.comit.comment.dto.CreateCommentRequest;
import kr.ac.knu.comit.comment.dto.HelpfulToggleResponse;
import kr.ac.knu.comit.comment.dto.UpdateCommentRequest;
import kr.ac.knu.comit.global.docs.annotation.ApiContract;
import kr.ac.knu.comit.global.docs.annotation.ApiDoc;
import kr.ac.knu.comit.global.docs.annotation.ApiError;
import kr.ac.knu.comit.global.docs.annotation.Example;
import kr.ac.knu.comit.global.docs.annotation.FieldDesc;
import kr.ac.knu.comit.global.auth.AuthenticatedMember;
import kr.ac.knu.comit.global.auth.MemberPrincipal;
import kr.ac.knu.comit.global.exception.ApiResponse;
import kr.ac.knu.comit.global.exception.BusinessErrorCode;
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
            description = "게시글에 달린 댓글 목록을 조회합니다.",
            descriptions = {
                    @FieldDesc(name = "postId", value = "댓글을 조회할 게시글 ID"),
                    @FieldDesc(name = "comments", value = "댓글 목록입니다. 각 항목은 댓글 ID, 내용, 작성자, 도움이 됐어요 수, 내 반응 여부, 작성/수정 시각을 포함합니다.")
            },
            errors = {
                    @ApiError(code = BusinessErrorCode.POST_NOT_FOUND, when = "댓글을 조회할 게시글이 없거나 삭제된 상태일 때")
            },
            example = @Example(
                    response = """
                            {
                              "result": "SUCCESS",
                              "data": {
                                "comments": [
                                  {
                                    "id": 201,
                                    "content": "entity graph도 같이 비교해보면 좋습니다.",
                                    "authorNickname": "orm-master",
                                    "helpfulCount": 4,
                                    "helpfulByMe": true,
                                    "mine": false,
                                    "createdAt": "2026-03-24T11:00:00",
                                    "updatedAt": null
                                  }
                                ]
                              }
                            }
                            """
            )
    )
    @GetMapping("/posts/{postId}/comments")
    ResponseEntity<ApiResponse<CommentListResponse>> getComments(
            @PathVariable Long postId,
            @AuthenticatedMember MemberPrincipal principal
    );

    @ApiDoc(
            summary = "댓글 작성",
            description = "게시글에 새 댓글을 작성합니다.",
            descriptions = {
                    @FieldDesc(name = "postId", value = "댓글을 작성할 게시글 ID"),
                    @FieldDesc(name = "content", value = "댓글 본문")
            },
            errors = {
                    @ApiError(code = BusinessErrorCode.POST_NOT_FOUND, when = "댓글을 작성할 게시글이 없거나 삭제된 상태일 때"),
                    @ApiError(code = BusinessErrorCode.MEMBER_NOT_FOUND, when = "인증된 사용자의 로컬 회원 정보가 존재하지 않을 때")
            },
            example = @Example(
                    request = """
                            {
                              "content": "entity graph도 같이 비교해보면 좋습니다."
                            }
                            """,
                    response = """
                            {
                              "result": "SUCCESS",
                              "data": 201
                            }
                            """
            )
    )
    @PostMapping("/posts/{postId}/comments")
    ResponseEntity<ApiResponse<Long>> createComment(
            @PathVariable Long postId,
            @RequestBody @Valid CreateCommentRequest request,
            @AuthenticatedMember MemberPrincipal principal
    );

    @ApiDoc(
            summary = "댓글 수정",
            description = "내가 작성한 댓글의 내용을 수정합니다.",
            descriptions = {
                    @FieldDesc(name = "commentId", value = "수정할 댓글 ID"),
                    @FieldDesc(name = "content", value = "수정할 댓글 본문")
            },
            errors = {
                    @ApiError(code = BusinessErrorCode.COMMENT_NOT_FOUND, when = "수정 대상 댓글이 없거나 삭제된 상태일 때"),
                    @ApiError(code = BusinessErrorCode.FORBIDDEN, when = "작성자가 아닌 사용자가 댓글을 수정하려고 할 때")
            },
            example = @Example(
                    request = """
                            {
                              "content": "entity graph와 fetch join 차이도 비교해보면 좋겠습니다."
                            }
                            """,
                    response = """
                            {
                              "result": "SUCCESS"
                            }
                            """
            )
    )
    @PatchMapping("/comments/{commentId}")
    ResponseEntity<ApiResponse<Void>> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Valid UpdateCommentRequest request,
            @AuthenticatedMember MemberPrincipal principal
    );

    @ApiDoc(
            summary = "댓글 삭제",
            description = "내가 작성한 댓글을 삭제 상태로 변경합니다.",
            descriptions = {
                    @FieldDesc(name = "commentId", value = "삭제할 댓글 ID")
            },
            errors = {
                    @ApiError(code = BusinessErrorCode.COMMENT_NOT_FOUND, when = "삭제 대상 댓글이 없거나 삭제된 상태일 때"),
                    @ApiError(code = BusinessErrorCode.FORBIDDEN, when = "작성자가 아닌 사용자가 댓글을 삭제하려고 할 때")
            },
            example = @Example(
                    response = """
                            {
                              "result": "SUCCESS"
                            }
                            """
            )
    )
    @DeleteMapping("/comments/{commentId}")
    ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @AuthenticatedMember MemberPrincipal principal
    );

    @ApiDoc(
            summary = "댓글 도움 토글",
            description = "댓글의 도움이 됐어요 상태를 토글합니다.",
            descriptions = {
                    @FieldDesc(name = "commentId", value = "도움이 됐어요를 토글할 댓글 ID"),
                    @FieldDesc(name = "helpful", value = "true면 도움이 됐어요가 추가되고 false면 취소됩니다.")
            },
            errors = {
                    @ApiError(code = BusinessErrorCode.COMMENT_NOT_FOUND, when = "도움이 됐어요를 누를 댓글이 없거나 삭제된 상태일 때")
            },
            example = @Example(
                    response = """
                            {
                              "result": "SUCCESS",
                              "data": {
                                "helpful": true
                              }
                            }
                            """
            )
    )
    @PostMapping("/comments/{commentId}/helpful")
    ResponseEntity<ApiResponse<HelpfulToggleResponse>> toggleHelpful(
            @PathVariable Long commentId,
            @AuthenticatedMember MemberPrincipal principal
    );
}
