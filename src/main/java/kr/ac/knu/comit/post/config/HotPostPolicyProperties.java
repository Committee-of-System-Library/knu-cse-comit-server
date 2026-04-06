package kr.ac.knu.comit.post.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;
import kr.ac.knu.comit.post.domain.BoardType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "comit.post.hot")
public class HotPostPolicyProperties {

    /** 인기글 집계 기간 (일). 최소 1일, 최대 365일. */
    @Min(1)
    @Max(365)
    private int windowDays;

    /** 좋아요 가중치. 양수여야 한다. */
    @Positive
    private int likeWeight;

    /** 댓글 가중치. 양수여야 한다. */
    @Positive
    private int commentWeight;

    /** 방문자 가중치. 양수여야 한다. */
    @Positive
    private int visitorWeight;

    /** 인기글 최대 개수. 최소 1개, 최대 20개. */
    @Min(1)
    @Max(20)
    private int limit;

    /** 인기글 집계에서 제외할 게시판 목록. null 불가. */
    @NotNull
    private List<BoardType> excludedBoardTypes = new ArrayList<>();
}
