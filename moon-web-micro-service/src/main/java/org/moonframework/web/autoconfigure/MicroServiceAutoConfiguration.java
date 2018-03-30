package org.moonframework.web.autoconfigure;

import org.moonframework.core.context.ApplicationContextHolder;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.web.advice.WebControllerAdvice;
import org.moonframework.web.context.ServletContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * @author quzile
 * @version 1.0
 * @since 2018/1/10
 */
@Configuration
@Import(WebControllerAdvice.class)
@ConditionalOnProperty(prefix = MicroServiceProperties.PREFIX, name = "enabled")
@EnableConfigurationProperties(MicroServiceProperties.class)
public class MicroServiceAutoConfiguration {

    private MicroServiceProperties properties;

    @Autowired
    public MicroServiceAutoConfiguration(MicroServiceProperties properties) {
        this.properties = properties;
    }

    @Bean
    public ApplicationContextHolder springContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean
    public ServletContextHolder servletContextHolder() {
        return new ServletContextHolder();
    }

    @Bean
    public Services services() {
        return new Services();
    }

    /**
     * <p>A Validator</p>
     * <p>Use Hibernate Validator e.g: validator.setProviderClass(HibernateValidator.class)</p>
     *
     * @return LocalValidatorFactoryBean
     */
    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource());
        return validator;
    }

    /**
     * <p>I18N config</p>
     * <p>Use ReloadableResourceBundleMessageSource Or ResourceBundleMessageSource</p>
     *
     * @return MessageSource
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(properties.getException(), properties.getMessage());
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}
