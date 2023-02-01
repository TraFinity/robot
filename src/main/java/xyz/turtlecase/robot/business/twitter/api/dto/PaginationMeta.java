package xyz.turtlecase.robot.business.twitter.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class PaginationMeta {
    /**
     * 结果数
     */
    private Integer result_count;

    /**
     * 下一页标记
     */
    private String next_token;
    /**
     * 上一页标记
     */
    private String previous_token;

}
