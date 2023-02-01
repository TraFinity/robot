package xyz.turtlecase.robot.business.system.mapper;

import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.turtlecase.robot.business.system.model.SystemConfig;

@Data
@EqualsAndHashCode
@Table(name = "system_dict")
public class SystemConfigPo extends SystemConfig {

}
