package org.moonframework.fragment.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.moonframework.core.security.Signature;
import org.moonframework.model.mybatis.domain.Response;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

public class StatelessAuthenticationFilter extends AuthenticatingFilter {

    private String tokenHeader;

    private String tokenHead;

    private String salt;

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authHeader = httpRequest.getHeader(tokenHeader);

        JwtToken token = Signature.apply(request.getParameterMap(), salt, list -> {
            JwtToken jwtToken = new JwtToken();
            jwtToken.setRoles(new HashSet<>(list));
            return jwtToken;
        });

        if (authHeader == null || !authHeader.startsWith(tokenHead)) {
            if (token != null) {
                token.setAnalysable(false);
                token.setToken("remote");
            }
        } else {
            String authToken = authHeader.substring(tokenHead.length()).trim();
            if (token == null) {
                token = new JwtToken(authToken);
            } else {
                token.setToken(authToken);
            }
        }

        return token;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        AuthenticationToken token = createToken(request, response);
        // 如果不存在token，则不执行登录操作，直接跳过filter
        if (token != null) {
            try {
                Subject subject = getSubject(request, response);
                subject.login(token);
            } catch (Exception ignore) {
            }
        }
        return true;
    }


    //登录失败时默认返回401状态码
    private void onLoginFail(ServletResponse response, Exception e) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        ObjectMapper objectMapper = new ObjectMapper();
        String result = objectMapper.writeValueAsString(new Response(String.valueOf(HttpServletResponse.SC_UNAUTHORIZED), e.getMessage()));
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json;charset=utf-8");
        httpResponse.setContentLength(result.length());
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter out = httpResponse.getWriter();
        out.append(result);
    }

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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
