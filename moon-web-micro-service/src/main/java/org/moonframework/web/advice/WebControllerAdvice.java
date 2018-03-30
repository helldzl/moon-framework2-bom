package org.moonframework.web.advice;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import feign.FeignException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.moonframework.validation.InvalidException;
import org.moonframework.validation.domain.FieldErrorResource;
import org.moonframework.web.jsonapi.Error;
import org.moonframework.web.jsonapi.Errors;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/8/3
 */
// Target all Controllers annotated with @RestController
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class WebControllerAdvice extends ResponseEntityExceptionHandler {

    private final Log logger = LogFactory.getLog(this.getClass());

    /**
     * <p>全局异常处理器</p>
     *
     * @param e exception
     * @return ResponseEntity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException(Exception e) {
        if (logger.isErrorEnabled()) {
            logger.error("Application Error!", e);
        }
        return ResponseEntity.badRequest().body(Responses.builder().error(Responses.error(String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage())));
    }

    @ExceptionHandler(HystrixRuntimeException.class)
    public ResponseEntity<Response> handleFeignException(HystrixRuntimeException e) {
        if (logger.isErrorEnabled()) {
            logger.error("Feign Error!", e);
        }
        Throwable throwable = e.getCause();
        if (throwable instanceof FeignException) {
            String message = throwable.getMessage();
            if (message != null) {
                message = message.replaceAll(".*\n", "");
                Errors errors = Errors.fromJson(message);
                if (errors != null) {
                    return ResponseEntity.badRequest().body(errors);
                }
            }
        }
        return ResponseEntity.badRequest().body(Responses.builder().error(Responses.error(String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage())));
    }

    @ExceptionHandler(InvalidException.class)
    public ResponseEntity<Response> handleInvalidException(InvalidException e) {
        List<Error> errors = new ArrayList<>();
        for (FieldErrorResource resource : e.getErrors()) {
            Error error = Error.builder()
                    .status(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                    .code(resource.getCode())
                    .title(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .detail(resource.getMessage())
                    .source(resource.getResource() + " -> " + resource.getField(), null)
                    .build();
            errors.add(error);
        }
        return ResponseEntity.badRequest().body(Responses.builder().errors(errors));
    }

    /**
     * <p>认证与授权异常处理器</p>
     *
     * @param e exception
     * @return ResponseEntity
     */
    @ExceptionHandler({
            UnauthenticatedException.class,
            UnauthorizedException.class})
    public ResponseEntity<Response> processUnauthenticatedException(Exception e) {
        if (logger.isErrorEnabled()) {
            logger.error("Unauthenticated Error!", e);
        }
        Error error = Error.builder()
                .status(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                .title(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .detail(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Responses.builder().errors(error));
    }

}
