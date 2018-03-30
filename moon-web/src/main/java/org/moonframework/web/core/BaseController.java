package org.moonframework.web.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.support.AbstractGenericEntity;
import org.moonframework.security.authentication.UserPermissions;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/21
 */
public abstract class BaseController<T extends BaseEntity> extends AbstractGenericEntity<T> {

    protected final Log logger = LogFactory.getLog(this.getClass());

    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    public static final String ROLE_REMOTE = "ROLE_REMOTE";

    public static final String ROLE_AD_USER = "ROLE_AD_USER";

    public static final String ROLE_AUDITOR = "ROLE_AUDITOR";

    /**
     * 默认参数
     *
     * @param model model
     */
    protected void model(Map<String, Object> model) {
        model.put("time", new Date());
    }

    protected List<Long> convert(List<String> list) {
        return list.stream().map(Long::valueOf).collect(Collectors.toList());
    }

    protected Byte getByte(String name) {
        return computeIfAbsent(name, Byte::valueOf, () -> null);
    }

    protected Byte getByte(String name, Supplier<Byte> supplier) {
        return computeIfAbsent(name, Byte::valueOf, supplier);
    }

    protected Short getShort(String name) {
        return computeIfAbsent(name, Short::valueOf, () -> null);
    }

    protected Short getShort(String name, Supplier<Short> supplier) {
        return computeIfAbsent(name, Short::valueOf, supplier);
    }

    protected Integer getInteger(String name) {
        return computeIfAbsent(name, Integer::valueOf, () -> null);
    }

    protected Integer getInteger(String name, Supplier<Integer> supplier) {
        return computeIfAbsent(name, Integer::valueOf, supplier);
    }

    protected Long getLong(String name) {
        return computeIfAbsent(name, Long::valueOf, () -> null);
    }

    protected Long getLong(String name, Supplier<Long> supplier) {
        return computeIfAbsent(name, Long::valueOf, supplier);
    }

    protected Float getFloat(String name) {
        return computeIfAbsent(name, Float::valueOf, () -> null);
    }

    protected Float getFloat(String name, Supplier<Float> supplier) {
        return computeIfAbsent(name, Float::valueOf, supplier);
    }

    protected Double getDouble(String name) {
        return computeIfAbsent(name, Double::valueOf, () -> null);
    }

    protected Double getDouble(String name, Supplier<Double> supplier) {
        return computeIfAbsent(name, Double::valueOf, supplier);
    }

    protected String getString(String name) {
        return computeIfAbsent(name, s -> s, () -> null);
    }

    protected String getString(String name, Supplier<String> supplier) {
        return computeIfAbsent(name, s -> s, supplier);
    }

    protected Boolean getBoolean(String name) {
        return computeIfAbsent(name, Boolean::valueOf, () -> null);
    }

    protected Boolean getBoolean(String name, Supplier<Boolean> supplier) {
        return computeIfAbsent(name, Boolean::valueOf, supplier);
    }

    protected HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    protected <R> R computeIfAbsent(String name, Function<String, R> fun, Supplier<R> supplier) {
        HttpServletRequest request = getHttpServletRequest();
        String param = request.getParameter(name);
        if (param == null)
            return supplier.get();

        try {
            return fun.apply(param);
        } catch (Exception e) {
            return supplier.get();
        }
    }

    protected Subject getSubject() {
        Subject subject = SecurityUtils.getSubject();
        return subject;
    }

    protected UserPermissions getPrincipal() {
        return (UserPermissions) getSubject().getPrincipal();
    }

    protected Long getCurrentUserId() {
        UserPermissions principal = getPrincipal();
        return principal == null ? null : principal.getUserId();
    }

    protected boolean isRoleADUser() {
        return getSubject().hasRole(ROLE_AD_USER);
    }

    protected boolean isRoleAuditor() {
        return getSubject().hasRole(ROLE_AUDITOR);
    }

    protected boolean isRoleAdmin() {
        return getSubject().hasRole(ROLE_ADMIN);
    }

    protected void hasPermissions(Long userId) {
        if (isRoleAdmin())
            return;
        if (userId == null || !userId.equals(getCurrentUserId()))
            throw new UnauthorizedException();
    }

    protected void assertPermitted(Long userId) {
        if (isAdmin())
            return;
        if (userId == null || !userId.equals(getCurrentUserId()))
            throw new UnauthorizedException();
    }

    protected boolean isAdmin() {
        // This is a super root user, is different from 'ROLE_ADMIN' roles
        return getSubject().hasRole("administrator");
    }

    protected boolean isPermitted(String permission) {
        return getSubject().isPermitted(permission);
    }

    protected boolean hasRole(String role) {
        return getSubject().hasRole(role);
    }

}
