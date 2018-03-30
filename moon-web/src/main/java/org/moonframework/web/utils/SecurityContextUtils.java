package org.moonframework.web.utils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.moonframework.security.authentication.UserPermissions;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/8/9
 */
public class SecurityContextUtils {

    public static Long currentUserId() {
        Subject subject = SecurityUtils.getSubject();
        UserPermissions principal = (UserPermissions) subject.getPrincipal();
        if (principal != null) {
            return principal.getUserId();
        }
        return null;
    }

}
