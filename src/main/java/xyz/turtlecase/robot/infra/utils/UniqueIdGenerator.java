package xyz.turtlecase.robot.infra.utils;

import java.util.UUID;

public class UniqueIdGenerator {
    public static String generatorUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
