package kr.ac.knu.comit.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.ac.knu.comit.post.domain.BoardType;

import java.util.List;

public record CreatePostRequest(
        @NotNull(message = "게시판 유형을 선택해주세요.")
        BoardType boardType,

        @NotBlank(message = "제목을 입력해주세요.")
        @Size(max = 255, message = "제목은 255자 이하이어야 합니다.")
        String title,

        @NotBlank(message = "내용을 입력해주세요.")
        String content,

        // Bean Validation은 형식만. 개수·길이 검증은 도메인(Post.create)에서 수행.
        @Size(max = 5, message = "태그는 최대 5개까지 입력할 수 있습니다.")
        List<String> tags
) {
    public List<String> tags() {
        return tags == null ? List.of() : tags;
    }
}
