package org.moonframework.web.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.moonframework.core.security.Signature;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author quzile
 * @version 1.0
 * @since 2018/1/17
 */
public class Oauth2FeignRequestInterceptor implements RequestInterceptor {

    private String salt;

    public Oauth2FeignRequestInterceptor() {
    }

    public Oauth2FeignRequestInterceptor(String salt) {
        this.salt = salt;
    }

    @Override
    public void apply(RequestTemplate template) {
        // TODO 基于spring security重构oauth2.0实现后, 这里也需要修改, 通过zuul网关来传播token, 使用@EnableOAuth2Sso 或 相关配置等

        // get request from thread local, hystrix's isolation need to set to semaphore
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // propagation [Authorization] header
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer")) {
            template.header("Authorization", header);
        }

        // feign client switch user roles by temporary, should add API signature, timestamp, roles informations
        String role = (String) requestAttributes.getAttribute(Signature.REMOTE, RequestAttributes.SCOPE_REQUEST);
        if (role != null) {
            String[] roles = role.split(",");
            long timestamp = System.currentTimeMillis();
            String signature = Signature.signature(Signature.apply(template.queries()), salt, timestamp, list -> list.add(Signature.apply(Signature.REMOTE, roles)));
            template.query(Signature.TIMESTAMP, String.valueOf(timestamp));
            template.query(Signature.SIGNATURE, signature);
            template.query(Signature.REMOTE, roles);
        }

//        RequestTemplate header = template.header(AUTHORIZATION_HEADER);
//        String token = "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoiMTIiLCJ1c2VyX25hbWUiOiIxMzYxMTAxOTIwOSIsImNyZWF0ZWQiOjE1MTU1NzMwNzk2NzMsInJvbGVzIjpbIlJPTEVfQURfVVNFUiIsIlJPTEVfVVNFUiIsIlJPTEVfQURNSU4iLCJST0xFX0FVRElUT1IiXSwiZXhwIjoxNTE1NTgwMjc5LCJhdXRob3JpdGllcyI6WyJjb20uYnVkZWUubzJvLndlYi5jb250cm9sbGVyLlRlc3RDb250cm9sbGVyLmRvRmluZCIsInBob3Rvczp2aWV3OjIiXX0.PbYFwsVaPxK3w7aiXm-nBwvpGi7ABiMzxyWlevxUp_gqcDqrIKtc6Q3e5PzUL2be8NnNCD9bg3mUWvm057NB0g";
//        String format = String.format("%s %s", BEARER_TOKEN_TYPE, token);
//
//        if (template.headers().containsKey(AUTHORIZATION_HEADER)) {
//            log.warn("The Authorization token has been already set");
//        } else {
//            boolean relayed = false;
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            if (authentication instanceof AbstractAuthenticationToken) {
//                AbstractAuthenticationToken aat = (AbstractAuthenticationToken) authentication;
//                OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) aat.getDetails();
//                String type = details.getTokenType();
//                String jwt = details.getTokenValue();
//                if (OAuth2AccessToken.BEARER_TYPE.equalsIgnoreCase(type) && jwt != null) {
//                    relayed = true;
//                    log.debug("The Authorization token has added in header, token:{}", jwt);
//                    template.header("Authorization", String.format("%s %s", OAuth2AccessToken.BEARER_TYPE, jwt));
//                }
//            }
//            if (!relayed) {
//                log.warn("Not relay the JWT for service: {}", template.url());
//            }
//        }
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

}
