package it.cleverad.engine.config.security;

import io.jsonwebtoken.ExpiredJwtException;
import it.cleverad.engine.business.OperationBusiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

@Slf4j
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private OperationBusiness operationBusiness;

    @Override
    protected void doFilterInternal(HttpServletRequest requeste, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        // Wrap the original request
        HttpServletRequest cachedBodyRequest = new CachedBodyHttpServletRequest((HttpServletRequest) requeste);

        final String requestTokenHeader = cachedBodyRequest.getHeader("Authorization");
        String uri = cachedBodyRequest.getRequestURI();
        String username = null;
        String jwtToken = null;

        // JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ") && requestTokenHeader.length() > 20) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                log.error("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                log.trace("JWT Token has expired");
            }
        } else {
            log.trace("JWT Token does not begin with Bearer String");
        }

        if (!uri.contains("encoded") && !uri.contains("target") && !uri.contains("cleverad/file")
                && !uri.contains("cleverad/cpc/refferal") && !uri.contains("cleverad/cpm/refferal")) {
            log.info("{}>{}>{}", username, cachedBodyRequest.getMethod(), uri);
        }

//        if (uri.contains("campaign/134")) {
            // SALVO IL DATO ANCHE IN DB
            OperationBusiness.BaseCreateRequest operation = new OperationBusiness.BaseCreateRequest();
            operation.setUsername(username);
            operation.setUrl(uri);
            try (InputStream inputStream = cachedBodyRequest.getInputStream()) {
                String bodyContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8); // Specify encoding
                operation.setData(bodyContent);
            } catch (IOException e) {
                log.error("Error reading input stream", e);
            }
            operation.setMethod(cachedBodyRequest.getMethod());
            operationBusiness.create(operation);
//        }

        //Once we get the token validate it.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
            // if token is valid configure Spring Security to manually set authentication
            if (Boolean.TRUE.equals(jwtTokenUtil.validateToken(jwtToken, userDetails))) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(cachedBodyRequest));
                // After setting the Authentication in the context, we specify
                // that the current user is authenticated. So it passes the Spring Security Configurations successfully.
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(cachedBodyRequest, response);
    }

}