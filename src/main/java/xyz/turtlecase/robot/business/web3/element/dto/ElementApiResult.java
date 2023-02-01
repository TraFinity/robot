package xyz.turtlecase.robot.business.web3.element.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ElementApiResult<T> {
    private Integer code;
    private String msg;
    private T data;

}
