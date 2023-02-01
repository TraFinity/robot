package xyz.turtlecase.robot.infra.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import tk.mybatis.mapper.common.base.update.UpdateByPrimaryKeySelectiveMapper;

/**
 * sqlmap基础类
 */
public abstract class BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(groups = UpdateByPrimaryKeySelectiveMapper.class, message = "id {properties.NotNull}")
    private Long id;

    /**
     * 以下注解的属性值,都会在AutoFillInterceptor拦截器中自动生成值
     */
    @CreatedDate
    private Date createTime;
    @LastModifiedDate
    private Date updateTime;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
