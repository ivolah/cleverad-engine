package it.cleverad.engine.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// This class handles unauthorized access attempts
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // This is invoked when user tries to access a secured REST resource without supplying any credentials
        // We should just send a 401 Unauthorized response
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}


//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.stereotype.Component;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.Serializable;
//
//@Component
//public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
//
//    private static final long serialVersionUID = -7858869558953243875L;
//
//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
//    }
//
//}