package kr.ac.knu.comit.member.dto;

import java.util.List;

public record MyCursorPageResponse<T>(
        List<T> items,
        Long nextCursorId,
        boolean hasNext
) {
}
