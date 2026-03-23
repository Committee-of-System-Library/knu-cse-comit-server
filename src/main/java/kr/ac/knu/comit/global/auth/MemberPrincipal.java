package kr.ac.knu.comit.global.auth;

/**
 * KNU CSE SSO JWT Claims → 서버 인증 컨텍스트
 *
 * SSO JWT Claims:
 *   sub            - SSO 식별자 (우리 DB member.sso_sub 과 매핑)
 *   student_number - 학번
 *   name           - 실명
 *   email          - 학교 이메일
 *   major          - 전공
 *   user_type      - CSE_STUDENT | KNU_OTHER_DEPT | EXTERNAL
 *   role           - ADMIN | STUDENT | null
 */
public record MemberPrincipal(
        Long memberId,       // 우리 DB PK (최초 로그인 시 생성 후 세팅)
        String ssoSub,       // SSO sub claim
        String name,
        String email,
        String studentNumber,
        UserType userType,
        MemberRole role
) {

    public boolean isAdmin() {
        return role == MemberRole.ADMIN;
    }

    public enum UserType {
        CSE_STUDENT, KNU_OTHER_DEPT, EXTERNAL
    }

    public enum MemberRole {
        ADMIN, STUDENT
    }
}
