package xyz.turtlecase.robot.business.web3.moralis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class MoralisApiResult<T> {
    private Integer total;
    private Integer page;
    private Integer page_size;
    private String cursor;
    private List<T> result;
}
