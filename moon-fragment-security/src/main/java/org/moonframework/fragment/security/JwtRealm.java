package org.moonframework.fragment.security;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.moonframework.core.util.BeanUtils;
import org.moonframework.security.authentication.UserPermissions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Set;

public class JwtRealm extends AuthorizingRealm {

    public static final String TOKEN = "jwt_token";

    private JwtTokenUtil jwtTokenUtil;

    private StringRedisTemplate redisTemplate;

    public JwtRealm(StringRedisTemplate redisTemplate) {
        if (redisTemplate == null)
            throw new IllegalArgumentException("Please set redisTemplate, redisTemplate can not be null");
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        //仅支持JwtToken类型的Token
        return token instanceof JwtToken;
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // null username are invalid
        if (principals == null)
            throw new AuthorizationException("Principals argument cannot be null.");

        // get the principal this realm cares about:
        UserPermissions userPermissions = (UserPermissions) getAvailablePrincipal(principals);
        Set<String> roles = userPermissions.getRoles();
        Set<String> permissions = userPermissions.getPermissions();

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        roles.forEach(info::addRole);
        permissions.forEach(info::addStringPermission);
        return info;
    }

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) {
        if (authenticationToken == null)
            throw new AuthenticationException("AuthenticationToken cannot be null.");

        try {
            JwtToken jwtToken = (JwtToken) authenticationToken;
            UserPermissions permissions;

            if (jwtToken.isAnalysable()) {
                String username = jwtTokenUtil.getUsernameFromToken(jwtToken.getToken());
                String userId = jwtTokenUtil.getUseridFromToken(jwtToken.getToken());

                if (username == null || userId == null || jwtTokenUtil.isTokenExpired(jwtToken.getToken())) {//校验
                    throw new AuthenticationException("token解析失败或token已过期");
                }

                String json = redisTemplate.opsForValue().get(UserPermissions.OBJECT_KEY + username);
                if (json == null) {
                    throw new AuthenticationException("没有找到token");
                }

                permissions = BeanUtils.readValue(json, UserPermissions.class);
                if (permissions == null || (!permissions.getTokens().contains(jwtToken.getToken())) && (!jwtToken.getToken().equals(permissions.getOldToken()))) {//同时验证当前token与旧的token，可以保证刷新token时用户的请求被允许
                    throw new AuthenticationException("服务端不存在此token");
                }

                jwtToken.setUserId(userId);
                jwtToken.setUsername(username);
            } else {
                permissions = new UserPermissions();
                permissions.setUserId(0L);
                permissions.setUsername(jwtToken.getToken());
            }

            // 仅限微服务间的调用, 用于临时提权
            if (!CollectionUtils.isEmpty(jwtToken.getRoles()))
                permissions.getRoles().addAll(jwtToken.getRoles());
            return new SimpleAuthenticationInfo(new SimplePrincipalCollection(permissions, getName()), jwtToken.getToken());
        } catch (Exception e) {
            throw new AuthenticationException(e);
        }
    }

    public void setJwtTokenUtil(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

}
