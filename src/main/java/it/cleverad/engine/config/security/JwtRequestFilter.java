package it.cleverad.engine.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil; // Utility class for JWT operations

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ") && requestTokenHeader.length() > 20) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (Exception e) {
                logger.error("Unable to get JWT Token or JWT Token has expired", e);
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String or is missing");
        }

        // Validate token and set authentication context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

            // If token is valid, configure Spring Security to manually set authentication
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // After setting the authentication in the context, specify that the current user is authenticated
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
}


//package it.cleverad.engine.config.security;
//
//import io.jsonwebtoken.ExpiredJwtException;
//import it.cleverad.engine.business.OperationBusiness;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Slf4j
//@Component
//public class JwtRequestFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtUserDetailsService jwtUserDetailsService;
//
//    @Autowired
//    private JwtTokenUtil jwtTokenUtil;
//
//    @Autowired
//    private OperationBusiness operationBusiness;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest cachedBodyRequest, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
//
//        // Wrap the original request
//        //        HttpServletRequest cachedBodyRequest = new CachedBodyHttpServletRequest((HttpServletRequest) requeste);
//
//        final String requestTokenHeader = cachedBodyRequest.getHeader("Authorization");
//        String username = null;
//        String jwtToken = null;
//
//        // JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
//        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ") && requestTokenHeader.length() > 20) {
//            jwtToken = requestTokenHeader.substring(7);
//            try {
//                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
//            } catch (IllegalArgumentException e) {
//                log.error("Unable to get JWT Token");
//            } catch (ExpiredJwtException e) {
//                log.trace("JWT Token has expired");
//            }
//        } else {
//            log.trace("JWT Token does not begin with Bearer String");
//        }
//
//        //Once we get the token validate it.
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
//            // if token is valid configure Spring Security to manually set authentication
//            if (Boolean.TRUE.equals(jwtTokenUtil.validateToken(jwtToken, userDetails))) {
//                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(cachedBodyRequest));
//                // After setting the Authentication in the context, we specify
//                // that the current user is authenticated. So it passes the Spring Security Configurations successfully.
//                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//            }
//        }
//        chain.doFilter(cachedBodyRequest, response);
//    }
//
//}