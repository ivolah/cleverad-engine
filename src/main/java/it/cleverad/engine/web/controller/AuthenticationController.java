package it.cleverad.engine.web.controller;

import it.cleverad.engine.business.UserBusiness;
import it.cleverad.engine.config.model.JwtRequest;
import it.cleverad.engine.config.model.JwtResponse;
import it.cleverad.engine.service.JwtTokenUtil;
import it.cleverad.engine.web.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Slf4j
@RestController
@CrossOrigin
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserBusiness userBusiness;

    @Autowired
    private UserDetailsService jwtInMemoryUserDetailsService;

    @PostMapping(value = "/authenticate")
    public ResponseEntity<JwtResponse> generateAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws DisabledException, BadCredentialsException {

        log.info(">>>>> LOGIN <<<<<");
        this.authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final UserDetails userDetails = jwtInMemoryUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);

        UserDTO user = userBusiness.findByUsername(authenticationRequest.getUsername());
        if (user != null) {
            return ResponseEntity.ok(new JwtResponse(token, user.getId()));
        } else {
            return ResponseEntity.ok(new JwtResponse(token, 0L));
        }

    }

    private void authenticate(String username, String password) throws DisabledException, BadCredentialsException {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new DisabledException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", e);
        }
    }
}
