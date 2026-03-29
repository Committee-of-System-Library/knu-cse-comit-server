package kr.ac.knu.comit.member.dto;

import java.time.LocalDateTime;
import kr.ac.knu.comit.member.domain.MemberStatus;

public record AdminMemberStatusRequest(
        MemberStatus status,
        LocalDateTime suspendedUntil
) {
}
