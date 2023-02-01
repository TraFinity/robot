package xyz.turtlecase.robot.infra.model;

/**
 * 基础状态码
 */
public enum BaseStatusEnum {
    ENABLE(1, "enable"),
    DISABLE(0, "disable"),
    DELETED(2, "deleted");

    /**
     * 状态值
     */
    private Integer value;

    /**
     * 状态标记
     */
    private String name;

    BaseStatusEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static BaseStatusEnum get(Integer value) {
        for (BaseStatusEnum baseStatusEnum : BaseStatusEnum.values()) {
            if (baseStatusEnum.value.equals(value)) {
                return baseStatusEnum;
            }
        }

        return null;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }
}
