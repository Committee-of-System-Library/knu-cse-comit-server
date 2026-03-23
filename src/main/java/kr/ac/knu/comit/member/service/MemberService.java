package kr.ac.knu.comit.member.service;

import kr.ac.knu.comit.global.auth.MemberPrincipal;
import kr.ac.knu.comit.global.exception.BusinessErrorCode;
import kr.ac.knu.comit.global.exception.BusinessException;
import kr.ac.knu.comit.member.domain.Member;
import kr.ac.knu.comit.member.domain.MemberRepository;
import kr.ac.knu.comit.member.dto.MemberProfileResponse;
import kr.ac.knu.comit.member.dto.UpdateNicknameRequest;
import kr.ac.knu.comit.member.dto.UpdateStudentNumberVisibilityRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // ── 조회 ─────────────────────────────────────────────────

    public MemberProfileResponse getMyProfile(Long memberId) {
        return MemberProfileResponse.from(findMemberOrThrow(memberId));
    }

    // ── 쓰기 ─────────────────────────────────────────────────

    /**
     * SSO 최초 로그인 시 member 생성 or 기존 member 반환.
     * Auth Filter에서 호출.
     */
    @Transactional
    public Member findOrCreateBySso(MemberPrincipal principal) {
        return memberRepository.findBySsoSubAndDeletedAtIsNull(principal.ssoSub())
                .map(member -> syncStudentNumber(member, principal.studentNumber()))
                .orElseGet(() -> memberRepository.save(
                        Member.create(principal.ssoSub(), principal.name(), principal.studentNumber())
                ));
    }

    @Transactional
    public void updateNickname(Long memberId, UpdateNicknameRequest request) {
        if (memberRepository.existsByNickname(request.nickname())) {
            throw new BusinessException(BusinessErrorCode.DUPLICATE_NICKNAME);
        }
        Member member = findMemberOrThrow(memberId);
        member.updateNickname(request.nickname());
    }

    @Transactional
    public void updateStudentNumberVisibility(Long memberId, UpdateStudentNumberVisibilityRequest request) {
        findMemberOrThrow(memberId).updateStudentNumberVisibility(request.visible());
    }

    // ── 내부 헬퍼 ────────────────────────────────────────────

    public Member findMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
                .filter(m -> !m.isDeleted())
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MEMBER_NOT_FOUND));
    }

    private Member syncStudentNumber(Member member, String studentNumber) {
        member.syncStudentNumber(studentNumber);
        return member;
    }
}
