/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:org.moonframework.fragment.security.autoconfigure.ShiroConfiguration
 * @description:TODO
 * @version:v0.0.1
 * @author:ZYW Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年3月31日 ZYW v0.0.1 create
 */
package org.moonframework.fragment.security.autoconfigure;

import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.moonframework.fragment.security.JwtRealm;
import org.moonframework.fragment.security.JwtTokenUtil;
import org.moonframework.fragment.security.StatelessAuthenticationFilter;
import org.moonframework.fragment.security.StatelessDefaultSubjectFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author ZYW
 */
@Configuration
@EnableConfigurationProperties(TokenProperties.class)
public class ShiroConfiguration {

    private final TokenProperties properties;

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public ShiroConfiguration(TokenProperties properties, StringRedisTemplate redisTemplate) {
        this.properties = properties;
        this.redisTemplate = redisTemplate;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(proxy);
        filterRegistrationBean.setName("shiroFilter");
        filterRegistrationBean.setUrlPatterns(Collections.singletonList("/*"));
        filterRegistrationBean.setEnabled(true);

        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean disableStatelessAuthenticationFilter(StatelessAuthenticationFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean(name = "jwtTokenUtil")
    public JwtTokenUtil jwtTokenUtil() {
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
        jwtTokenUtil.setExpiration(properties.getExpiration());
        jwtTokenUtil.setSecret(properties.getSecret());
        jwtTokenUtil.setRemembermeExpiration(properties.getRememberMeExpiration());
        return jwtTokenUtil;
    }

    @Bean(name = "jwtRealm")
    @DependsOn("lifecycleBeanPostProcessor")
    public JwtRealm jwtRealm() {
        JwtRealm realm = new JwtRealm(redisTemplate);
        realm.setJwtTokenUtil(jwtTokenUtil());

        realm.setCachingEnabled(false);
        realm.setAuthenticationCacheName("authenticationCache");
        realm.setAuthenticationCachingEnabled(false);
        realm.setAuthorizationCacheName("authorizationCache");
        realm.setAuthorizationCachingEnabled(false);
        return realm;
    }

    @Bean(name = "subjectFactory")
    public StatelessDefaultSubjectFactory statelessDefaultSubjectFactory() {
        return new StatelessDefaultSubjectFactory();
    }

    @Bean(name = "sessionManager")
    public DefaultSessionManager sessionManager() {
        DefaultSessionManager sessionManager = new DefaultSessionManager();
        sessionManager.setSessionValidationSchedulerEnabled(false);//禁用掉会话调度器
        return sessionManager;
    }

    @Bean(name = "securityManager")
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(jwtRealm());
        securityManager.setSubjectFactory(statelessDefaultSubjectFactory());
        securityManager.setSessionManager(sessionManager());

        EhCacheManager ehCacheManager = new EhCacheManager();
        ehCacheManager.setCacheManagerConfigFile("classpath:META-INF/spring/ehcache.xml");
        securityManager.setCacheManager(ehCacheManager);

        DefaultSubjectDAO subjectDao = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDao.setSessionStorageEvaluator(sessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDao);
        return securityManager;
    }

    @Bean(name = "statelessAuthenticationFilter")
    public StatelessAuthenticationFilter statelessAuthenticationFilter() {
        StatelessAuthenticationFilter statelessAuthenticationFilter = new StatelessAuthenticationFilter();
        statelessAuthenticationFilter.setTokenHead(properties.getTokenHead());
        statelessAuthenticationFilter.setTokenHeader(properties.getTokenHeader());
        statelessAuthenticationFilter.setSalt(properties.getSalt());
        return statelessAuthenticationFilter;
    }

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager());

        Map<String, Filter> filters = new LinkedHashMap<String, Filter>();
        filters.put("authc", statelessAuthenticationFilter());
        shiroFilterFactoryBean.setFilters(filters);

        Map<String, String> filterChainDefinitionManager = new LinkedHashMap<String, String>();
        filterChainDefinitionManager.put("/**", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionManager);

        return shiroFilterFactoryBean;
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
        daap.setProxyTargetClass(true);
        return daap;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
        aasa.setSecurityManager(securityManager());
        return aasa;
    }

    @Bean
    public MethodInvokingFactoryBean methodInvokingFactoryBean() {
        MethodInvokingFactoryBean methodBean = new MethodInvokingFactoryBean();
        methodBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        methodBean.setArguments(new Object[]{securityManager()});
        return methodBean;
    }

    /**
     * <p><a href="http://shiro.apache.org/spring.html">Integrating Apache Shiro into Spring-based Applications</a></p>
     */
    @Configuration
    @ConditionalOnMissingBean(LifecycleBeanPostProcessor.class)
    protected static class ShiroBeanPostProcessor {

        @Bean
        @Order(value = Ordered.LOWEST_PRECEDENCE)
        public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
            return new LifecycleBeanPostProcessor();
        }

    }

}
