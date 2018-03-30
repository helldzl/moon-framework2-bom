package org.moonframework.model.mybatis.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.validator.constraints.Range;
import org.moonframework.core.json.View;

import java.io.Serializable;
import java.util.Date;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/11/17
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseEntity implements Serializable {

    public static final String ID = "id";
    public static final String ENABLED = "enabled";
    public static final String CREATOR = "creator";
    public static final String MODIFIER = "modifier";
    public static final String CREATED = "created";
    public static final String MODIFIED = "modified";

    private static final long serialVersionUID = -8751935499989702763L;

    private Long id;

    @Range(min = 0, max = 9)
    private Integer enabled;

    private Long creator;

    private Long modifier;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modified;

    public BaseEntity() {
    }

    public BaseEntity(Long id) {
        this.id = id;
    }

    @JsonView(View.class)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonView(View.class)
    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    @JsonView(View.class)
    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    @JsonView(View.class)
    public Long getModifier() {
        return modifier;
    }

    public void setModifier(Long modifier) {
        this.modifier = modifier;
    }

    @JsonView(View.class)
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @JsonView(View.class)
    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }
}
