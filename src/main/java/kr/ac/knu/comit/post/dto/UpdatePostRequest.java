package kr.ac.knu.comit.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdatePostRequest(
        @NotBlank(message = "제목을 입력해주세요.")
        @Size(max = 255, message = "제목은 255자 이하이어야 합니다.")
        String title,

        @NotBlank(message = "내용을 입력해주세요.")
        String content,

        @Size(max = 5, message = "태그는 최대 5개까지 입력할 수 있습니다.")
        List<
                @NotBlank(message = "태그는 비어 있을 수 없습니다.")
                @Size(max = 20, message = "태그는 20자 이하이어야 합니다.")
                String> tags
) {
    public List<String> tags() {
        return tags == null ? List.of() : tags;
    }
}
