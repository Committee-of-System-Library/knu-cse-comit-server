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

    public MemberProfileResponse getMyProfile(Long memberId) {
        return MemberProfileResponse.from(findMemberOrThrow(memberId));
    }

    /**
     * 인증된 사용자의 로컬 회원을 찾고, 첫 로그인이라면 새로 만든다.
     *
     * @implNote 현재 헤더 기반 인증 흐름을 member 도메인과 연결하는 동안
     * 인증 필터가 이 메서드를 호출한다.
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
