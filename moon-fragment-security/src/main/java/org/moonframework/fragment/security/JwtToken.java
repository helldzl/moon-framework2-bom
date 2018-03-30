/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:org.moonframework.fragment.security.JwtToken
 * @description:TODO
 * @version:v0.0.1
 * @author:ZYW Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年3月17日 ZYW v0.0.1 create
 */
package org.moonframework.fragment.security;

import org.apache.shiro.authc.RememberMeAuthenticationToken;

import java.util.Set;

/**
 * @author ZYW
 */
public class JwtToken implements RememberMeAuthenticationToken {

    private static final long serialVersionUID = 5748790623136521556L;

    private String userId;
    private String username;
    private String token;

    // token是否可以解析: 仅在没有传播凭证到下流服务并且传播了签名时才为false
    private boolean analysable = true;
    private Set<String> roles;

    public JwtToken() {
    }

    public JwtToken(String token) {
        super();
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public boolean isRememberMe() {
        return false;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAnalysable() {
        return analysable;
    }

    public void setAnalysable(boolean analysable) {
        this.analysable = analysable;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
