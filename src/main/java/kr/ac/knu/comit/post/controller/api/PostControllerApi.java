package kr.ac.knu.comit.post.controller.api;

import kr.ac.knu.comit.docs.annotation.ApiContract;
import kr.ac.knu.comit.docs.annotation.ApiDoc;
import kr.ac.knu.comit.docs.annotation.FieldDesc;
import kr.ac.knu.comit.global.auth.AuthenticatedMember;
import kr.ac.knu.comit.global.auth.MemberPrincipal;
import kr.ac.knu.comit.global.exception.ApiResponse;
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

    @ApiDoc(summary = "게시글 상세 조회")
    @GetMapping("/{postId}")
    ResponseEntity<ApiResponse<PostDetailResponse>> getPost(
            @PathVariable Long postId,
            @AuthenticatedMember MemberPrincipal principal
    );

    @ApiDoc(
            summary = "게시글 작성",
            descriptions = {
                    @FieldDesc(name = "tags", value = "선택 항목입니다. 최대 5개, 각 20자 이하입니다.")
            }
    )
    @PostMapping
    ResponseEntity<ApiResponse<Long>> createPost(
            @RequestBody @Valid CreatePostRequest request,
            @AuthenticatedMember MemberPrincipal principal
    );

    @ApiDoc(summary = "게시글 수정")
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
            }
    )
    @PostMapping("/{postId}/like")
    ResponseEntity<ApiResponse<LikeToggleResponse>> toggleLike(
            @PathVariable Long postId,
            @AuthenticatedMember MemberPrincipal principal
    );
}
