package kr.ac.knu.comit.post.config;

import java.util.ArrayList;
import java.util.List;
import kr.ac.knu.comit.post.domain.BoardType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "comit.post.hot")
public class HotPostPolicyProperties {

    private int windowDays = 7;
    private int likeWeight = 5;
    private int commentWeight = 3;
    private int visitorWeight = 2;
    private int limit = 5;
    private List<BoardType> excludedBoardTypes = new ArrayList<>(List.of(BoardType.NOTICE, BoardType.EVENT));
}

