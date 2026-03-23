package kr.ac.knu.comit.member.domain;

import jakarta.persistence.*;
import kr.ac.knu.comit.global.exception.BusinessErrorCode;
import kr.ac.knu.comit.global.exception.BusinessException;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ssoSub;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(length = 20)
    private String studentNumber;

    @Column(nullable = false)
    private boolean studentNumberVisible = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    protected Member() {
    }

    // ── 정적 팩토리 ─────────────────────────────────────────

    /**
     * SSO 최초 로그인 시 member 생성.
     * 닉네임 초기값 = SSO name (이후 변경 가능).
     */
    public static Member create(String ssoSub, String initialNickname, String studentNumber) {
        validateNickname(initialNickname);
        Member member = new Member();
        member.ssoSub = ssoSub;
        member.nickname = initialNickname;
        member.studentNumber = normalizeStudentNumber(studentNumber);
        member.studentNumberVisible = true;
        member.createdAt = LocalDateTime.now();
        return member;
    }

    // ── 상태 변경 ────────────────────────────────────────────

    public void updateNickname(String nickname) {
        validateNickname(nickname);
        this.nickname = nickname;
    }

    public void updateStudentNumberVisibility(boolean visible) {
        this.studentNumberVisible = visible;
    }

    public void syncStudentNumber(String studentNumber) {
        this.studentNumber = normalizeStudentNumber(studentNumber);
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public Long getId() {
        return id;
    }

    public String getSsoSub() {
        return ssoSub;
    }

    public String getNickname() {
        return nickname;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public boolean isStudentNumberVisible() {
        return studentNumberVisible;
    }

    // ── 도메인 규칙 검증 ─────────────────────────────────────

    private static void validateNickname(String nickname) {
        if (nickname == null || nickname.isBlank() || nickname.length() > 50) {
            throw new BusinessException(BusinessErrorCode.INVALID_NICKNAME);
        }
    }

    private static String normalizeStudentNumber(String studentNumber) {
        if (studentNumber == null) {
            return null;
        }
        String normalized = studentNumber.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
