package org.moonframework.security.authentication;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserPermissions implements Serializable {

    private static final long serialVersionUID = 6214255537350140246L;

    public static final String OBJECT_KEY = "token_permissions_object";

    private String ip;
    private Long userId;
    private String username;
    private List<String> tokens;//redis中保存多个token，支持多点登录
    private String oldToken;//在刷新token后，旧的token依然在其过期时间 内有效，防止用户在刷新token的同时其他请求带着旧的token验证而导致的验证不通过
    private Set<String> roles = new HashSet<>();
    private Set<String> permissions = new HashSet<>();

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getOldToken() {
        return oldToken;
    }

    public void setOldToken(String oldToken) {
        this.oldToken = oldToken;
    }
}
