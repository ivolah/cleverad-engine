package it.cleverad.engine.service;


import it.cleverad.engine.business.UserBusiness;
import it.cleverad.engine.web.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;

@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    UserBusiness userBusiness;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO userDTO = userBusiness.findByUsername(username);
        //log.info(">>>" + username + " --- " + userDTO.getUsername() + " " + userDTO.getAffiliateId());
        if (userDTO != null) {
            return new User(userDTO.getUsername(), userDTO.getPassword(), new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("Nessun utente Cleverad trovato per: " + username);
        }
    }

    public String getUserFromToken(String token) {

        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        //String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));

        return new JSONObject(payload).getString("sub");
    }

    public Long getAffiliateID() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userBusiness.findByUsername(username).getAffiliateId() != null)
            return userBusiness.findByUsername(username).getAffiliateId();
        else return 0L;
    }

    public Long getUserID() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!Objects.isNull(userBusiness.findByUsername(username).getId()))
            return userBusiness.findByUsername(username).getId();
        else return 0L;
    }

    public String getRole() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username.equals("anonymousUser") || userBusiness.findByUsername(username).getRoleId() == 3) {
            return "Admin";
        } else {
            return "User";
        }
    }

    public Boolean isAdmin() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return username.equals("anonymousUser") || userBusiness.findByUsername(username).getRoleId() == 3;
    }

    public Long getAffilaite() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userBusiness.findByUsername(username).getAffiliateId();
    }

}