package org.moonframework.web.core;

import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/11/27
 */
public abstract class BaseServiceController<T extends BaseEntity, S extends BaseService<T>> extends BaseController<T> {

    @Autowired
    protected S service;

}
