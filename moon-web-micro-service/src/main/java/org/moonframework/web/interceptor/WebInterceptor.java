package org.moonframework.web.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/8/18
 */
public class WebInterceptor extends HandlerInterceptorAdapter {

    /**
     * <p>http://logging.apache.org/log4j/2.x/manual/thread-context.html</p>
     *
     * @param request  request
     * @param response response
     * @param handler  handler
     * @return boolean
     * @throws Exception Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return super.preHandle(request, response, handler);
    }

}
