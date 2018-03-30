package org.moonframework.fragment.security.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(TokenProperties.PREFIX)
public class TokenProperties {

    public static final String PREFIX = "moon.data.token";

    private String tokenHeader = "Authorization";
    private String tokenHead = "Bearer ";
    private String secret = "budee123";
    private Long expiration = 7200L;
    private Long rememberMeExpiration = 604800L;
    private String salt;

    public String getTokenHeader() {
        return tokenHeader;
    }

    public void setTokenHeader(String tokenHeader) {
        this.tokenHeader = tokenHeader;
    }

    public String getTokenHead() {
        return tokenHead;
    }

    public void setTokenHead(String tokenHead) {
        this.tokenHead = tokenHead;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public Long getRememberMeExpiration() {
        return rememberMeExpiration;
    }

    public void setRememberMeExpiration(Long rememberMeExpiration) {
        this.rememberMeExpiration = rememberMeExpiration;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
