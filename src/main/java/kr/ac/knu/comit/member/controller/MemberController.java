package kr.ac.knu.comit.member.controller;

import kr.ac.knu.comit.global.auth.MemberPrincipal;
import kr.ac.knu.comit.global.exception.ApiResponse;
import kr.ac.knu.comit.member.controller.api.MemberControllerApi;
import kr.ac.knu.comit.member.dto.MemberProfileResponse;
import kr.ac.knu.comit.member.dto.MyCommentResponse;
import kr.ac.knu.comit.member.dto.MyCursorPageResponse;
import kr.ac.knu.comit.member.dto.UpdateNicknameRequest;
import kr.ac.knu.comit.member.dto.UpdateStudentNumberVisibilityRequest;
import kr.ac.knu.comit.member.service.MemberActivityService;
import kr.ac.knu.comit.member.service.MemberService;
import kr.ac.knu.comit.post.dto.PostSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController implements MemberControllerApi {

    private final MemberService memberService;
    private final MemberActivityService memberActivityService;

    @Override
    public ResponseEntity<ApiResponse<MemberProfileResponse>> getMyProfile(MemberPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(memberService.getMyProfile(principal.memberId())));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> updateNickname(
            UpdateNicknameRequest request,
            MemberPrincipal principal
    ) {
        memberService.updateNickname(principal.memberId(), request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> updateStudentNumberVisibility(
            UpdateStudentNumberVisibilityRequest request,
            MemberPrincipal principal
    ) {
        memberService.updateStudentNumberVisibility(principal.memberId(), request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Override
    public ResponseEntity<ApiResponse<MyCursorPageResponse<PostSummaryResponse>>> getMyPosts(
            Long cursorId, int size, MemberPrincipal principal
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                memberActivityService.getMyPosts(principal.memberId(), cursorId, size)
        ));
    }

    @Override
    public ResponseEntity<ApiResponse<MyCursorPageResponse<MyCommentResponse>>> getMyComments(
            Long cursorId, int size, MemberPrincipal principal
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                memberActivityService.getMyComments(principal.memberId(), cursorId, size)
        ));
    }

    @Override
    public ResponseEntity<ApiResponse<MyCursorPageResponse<PostSummaryResponse>>> getMyLikes(
            Long cursorId, int size, MemberPrincipal principal
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                memberActivityService.getMyLikes(principal.memberId(), cursorId, size)
        ));
    }
}
