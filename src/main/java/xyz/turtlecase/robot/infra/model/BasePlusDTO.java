package xyz.turtlecase.robot.infra.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BasePlusDTO extends BaseDTO {
    /**
     * 是否生效
     */
    private Integer status;
    private BaseStatusEnum statusShow;

    public void setStatus(Integer status) {
        this.status = status;
        this.statusShow = BaseStatusEnum.get(status);
    }
}
