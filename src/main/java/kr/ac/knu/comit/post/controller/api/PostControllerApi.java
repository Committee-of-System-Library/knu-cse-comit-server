package kr.ac.knu.comit.post.controller.api;

import kr.ac.knu.comit.global.docs.annotation.ApiContract;
import kr.ac.knu.comit.global.docs.annotation.ApiDoc;
import kr.ac.knu.comit.global.docs.annotation.ApiError;
import kr.ac.knu.comit.global.docs.annotation.FieldDesc;
import kr.ac.knu.comit.global.auth.AuthenticatedMember;
import kr.ac.knu.comit.global.auth.MemberPrincipal;
import kr.ac.knu.comit.global.exception.ApiResponse;
import kr.ac.knu.comit.global.exception.BusinessErrorCode;
import kr.ac.knu.comit.post.domain.BoardType;
import kr.ac.knu.comit.post.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ApiContract
@RequestMapping("/posts")
public interface PostControllerApi {

    @ApiDoc(
            summary = "게시글 목록 조회 (cursor 페이지네이션)",
            descriptions = {
                    @FieldDesc(name = "boardType", value = "QNA | FREE"),
                    @FieldDesc(name = "cursor", value = "이전 응답의 nextCursorId. 첫 페이지는 생략합니다."),
                    @FieldDesc(name = "size", value = "기본값 20, 최대 20입니다.")
            }
    )
    @GetMapping
    ResponseEntity<ApiResponse<PostCursorPageResponse>> getPosts(
            @RequestParam BoardType boardType,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticatedMember MemberPrincipal principal
    );

    @ApiDoc(
            summary = "게시글 상세 조회",
            errors = {
                    @ApiError(code = BusinessErrorCode.POST_NOT_FOUND, when = "조회 대상 게시글이 없거나 삭제된 상태일 때")
            }
    )
    @GetMapping("/{postId}")
    ResponseEntity<ApiResponse<PostDetailResponse>> getPost(
            @PathVariable Long postId,
            @AuthenticatedMember MemberPrincipal principal
    );

    @ApiDoc(
            summary = "게시글 작성",
            descriptions = {
                    @FieldDesc(name = "tags", value = "선택 항목입니다. 최대 5개, 각 20자 이하입니다.")
            },
            errors = {
                    @ApiError(code = BusinessErrorCode.MEMBER_NOT_FOUND, when = "인증된 사용자의 로컬 회원 정보가 존재하지 않을 때"),
                    @ApiError(code = BusinessErrorCode.INVALID_TAG, when = "태그 길이가 도메인 규칙을 벗어날 때")
            }
    )
    @PostMapping
    ResponseEntity<ApiResponse<Long>> createPost(
            @RequestBody @Valid CreatePostRequest request,
            @AuthenticatedMember MemberPrincipal principal
    );

    @ApiDoc(
            summary = "게시글 수정",
            errors = {
                    @ApiError(code = BusinessErrorCode.POST_NOT_FOUND, when = "수정 대상 게시글이 없거나 삭제된 상태일 때"),
                    @ApiError(code = BusinessErrorCode.FORBIDDEN, when = "작성자가 아닌 사용자가 수정하려고 할 때"),
                    @ApiError(code = BusinessErrorCode.INVALID_TAG, when = "태그 길이가 도메인 규칙을 벗어날 때")
            }
    )
    @PatchMapping("/{postId}")
    ResponseEntity<ApiResponse<Void>> updatePost(
            @PathVariable Long postId,
            @RequestBody @Valid UpdatePostRequest request,
            @AuthenticatedMember MemberPrincipal principal
    );

    @ApiDoc(
            summary = "게시글 삭제",
            descriptions = {
                    @FieldDesc(name = "postId", value = "삭제할 게시글 ID")
            },
            errors = {
                    @ApiError(code = BusinessErrorCode.POST_NOT_FOUND, when = "삭제 대상 게시글이 없거나 삭제된 상태일 때"),
                    @ApiError(code = BusinessErrorCode.FORBIDDEN, when = "작성자 또는 관리자 권한 없이 삭제하려고 할 때")
            }
    )
    @DeleteMapping("/{postId}")
    ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            @AuthenticatedMember MemberPrincipal principal
    );

    @ApiDoc(
            summary = "좋아요 토글",
            descriptions = {
                    @FieldDesc(name = "liked", value = "true면 좋아요가 추가되고 false면 좋아요가 취소됩니다.")
            },
            errors = {
                    @ApiError(code = BusinessErrorCode.POST_NOT_FOUND, when = "좋아요를 누를 게시글이 없거나 삭제된 상태일 때")
            }
    )
    @PostMapping("/{postId}/like")
    ResponseEntity<ApiResponse<LikeToggleResponse>> toggleLike(
            @PathVariable Long postId,
            @AuthenticatedMember MemberPrincipal principal
    );
}
