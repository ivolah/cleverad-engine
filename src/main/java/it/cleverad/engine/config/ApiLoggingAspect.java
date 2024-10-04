package it.cleverad.engine.config;


import it.cleverad.engine.business.OperationBusiness;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@Log4j2
public class ApiLoggingAspect {

    @Autowired
    private OperationBusiness operationBusiness;

    @Autowired
    private HttpServletRequest httpServletRequest;

    // Intercepts controller methods
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {
    }

    // Before controller method execution
    @Before("controllerMethods()")
    public void logRequest(JoinPoint joinPoint) {

        String requestUri = httpServletRequest.getRequestURI();

        if (!requestUri.contains("encoded") && !requestUri.contains("target")
                && !requestUri.contains("cleverad/file") && !requestUri.contains("cleverad/cpc/refferal")
                && !requestUri.contains("cleverad/cpm/refferal") && !requestUri.contains("cleverad/dictionary")) {

            OperationBusiness.BaseCreateRequest operation = new OperationBusiness.BaseCreateRequest();
            // Get the authenticated username
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                operation.setUsername(authentication.getName()); // Store the username
            } else {
                operation.setUsername("Anonymous"); // If no user is authenticated
            }
            operation.setUrl(httpServletRequest.getRequestURI());
            operation.setMethod(httpServletRequest.getMethod());
            // Filter sensitive information from the request parameters
            // String requestParams = Arrays.stream(joinPoint.getArgs()).filter(arg -> !(arg instanceof HttpServletRequest || arg instanceof HttpServletResponse)) // Skip the HttpServletRequest/Response objects
            String requestParams = Arrays.stream(joinPoint.getArgs()).map(Object::toString).collect(Collectors.joining(", "));

            operation.setData(filterSensitiveData(requestParams));

            log.info("{}>{}>{}", operation.getUsername(), operation.getMethod(), operation.getUrl());
            log.trace("{}:{}={}-{}", operation.getMethod(), operation.getUrl(), requestParams, requestParams, httpServletRequest.getQueryString());

            // Save the request log to the database
            operationBusiness.create(operation);
        }

    }

    // After an exception is thrown in the controller
    @AfterThrowing(pointcut = "controllerMethods()", throwing = "exception")
    public void logException(JoinPoint joinPoint, Exception exception) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();

        log.error("Exception in Method: {} in Class: {} with Cause: {} and Message: {}", methodName, className, exception.getCause(), exception.getMessage());

    }

    // Filter out sensitive data (e.g., passwords, tokens) from request params
    private String filterSensitiveData(String requestParams) {
        return requestParams.replaceAll("(password|token)=([^&]+)", "$1=REDACTED");
    }
}