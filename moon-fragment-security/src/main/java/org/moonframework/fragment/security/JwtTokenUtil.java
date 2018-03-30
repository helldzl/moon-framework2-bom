package org.moonframework.fragment.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.moonframework.security.authentication.PermissionControl;
import org.moonframework.security.domain.User;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -3301605591108950415L;

    private static final String CLAIM_KEY_USERNAME = "user_name";
    private static final String CLAIM_KEY_USERID = "user_id";
    private static final String CLAIM_KEY_CREATED = "created";
    private static final String CLAIM_KEY_ROLE = "roles";
    private static final String CLAIM_KEY_AUTHORITIES = "authorities";

    private String secret;

    private Long expiration;
    
    private Long remembermeExpiration;

    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = getClaimsFromToken(token);
            username = (String)claims.get(CLAIM_KEY_USERNAME);
        } catch (Exception e) {
            username = null;
        }
        return username;
    }
    
    public String getUseridFromToken(String token) {
        String userId;
        try {
            final Claims claims = getClaimsFromToken(token);
            userId = (String)claims.get(CLAIM_KEY_USERID);
        } catch (Exception e) {
            userId = null;
        }
        return userId;
    }

    public Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = getClaimsFromToken(token);
            created = new Date((Long) claims.get(CLAIM_KEY_CREATED));
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }
    
    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    private Date generateExpirationDate(Date created ,int rememberme) {
        if(rememberme == 1){
            return new Date(created.getTime() + remembermeExpiration * 1000);
        }
        return new Date(created.getTime() + expiration * 1000);
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /*public static void main(String[] args) {
        JwtTokenUtil util = new JwtTokenUtil();
    }*/
    
    /*private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }*/

    public String generateToken(String username,String userId,int rememberme,PermissionControl control) {//创建token
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, username);
        claims.put(CLAIM_KEY_USERID, userId);
        if(control != null) {
        	claims.put(CLAIM_KEY_ROLE, control.getRoles());
        	claims.put(CLAIM_KEY_AUTHORITIES, control.getAuthorities());
        }
        
        
        Date created = new Date();
        claims.put(CLAIM_KEY_CREATED, created);
        Date expirationDate = generateExpirationDate(created,rememberme);
        String token = generateToken(claims,expirationDate);
        return token;
    }

    String generateToken(Map<String, Object> claims,Date expirationDate) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
//        final Date created = getCreatedDateFromToken(token);
        return  !isTokenExpired(token);//&& !isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
        
    }

    public String refreshToken(String token) {
        String refreshedToken;
        try {
            final Claims claims = getClaimsFromToken(token);
            Date created = new Date();
            claims.put(CLAIM_KEY_CREATED, created);
            Date expirationDate = generateExpirationDate(created,0);
            refreshedToken = generateToken(claims,expirationDate);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    public Boolean validateToken(String token, User user) {
        final String username = getUsernameFromToken(token);
        return (
                username.equals(user.getUsername())
                        && !isTokenExpired(token));//&& !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate())
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

    public Long getRemembermeExpiration() {
        return remembermeExpiration;
    }

    public void setRemembermeExpiration(Long remembermeExpiration) {
        this.remembermeExpiration = remembermeExpiration;
    }
}

