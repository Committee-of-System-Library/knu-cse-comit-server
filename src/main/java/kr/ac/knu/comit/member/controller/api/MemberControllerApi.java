package kr.ac.knu.comit.member.controller.api;

import jakarta.validation.Valid;
import kr.ac.knu.comit.docs.annotation.ApiContract;
import kr.ac.knu.comit.docs.annotation.ApiDoc;
import kr.ac.knu.comit.docs.annotation.FieldDesc;
import kr.ac.knu.comit.global.auth.AuthenticatedMember;
import kr.ac.knu.comit.global.auth.MemberPrincipal;
import kr.ac.knu.comit.global.exception.ApiResponse;
import kr.ac.knu.comit.member.dto.MemberProfileResponse;
import kr.ac.knu.comit.member.dto.UpdateNicknameRequest;
import kr.ac.knu.comit.member.dto.UpdateStudentNumberVisibilityRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@ApiContract
@RequestMapping("/members/me")
public interface MemberControllerApi {

    @ApiDoc(
            summary = "내 프로필 조회",
            descriptions = {
                    @FieldDesc(name = "nickname", value = "현재 회원 닉네임"),
                    @FieldDesc(name = "studentNumber", value = "현재 회원의 학번"),
                    @FieldDesc(name = "studentNumberVisible", value = "학번 공개 여부")
            }
    )
    @GetMapping
    ResponseEntity<ApiResponse<MemberProfileResponse>> getMyProfile(
            @AuthenticatedMember MemberPrincipal principal
    );

    @ApiDoc(
            summary = "내 닉네임 수정",
            descriptions = {
                    @FieldDesc(name = "nickname", value = "1자 이상 50자 이하 닉네임")
            }
    )
    @PatchMapping
    ResponseEntity<ApiResponse<Void>> updateNickname(
            @RequestBody @Valid UpdateNicknameRequest request,
            @AuthenticatedMember MemberPrincipal principal
    );

    @ApiDoc(
            summary = "내 학번 공개 여부 수정",
            descriptions = {
                    @FieldDesc(name = "visible", value = "true면 학번을 공개하고 false면 비공개합니다.")
            }
    )
    @PatchMapping("/student-number-visibility")
    ResponseEntity<ApiResponse<Void>> updateStudentNumberVisibility(
            @RequestBody @Valid UpdateStudentNumberVisibilityRequest request,
            @AuthenticatedMember MemberPrincipal principal
    );
}
