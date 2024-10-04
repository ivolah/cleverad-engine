package it.cleverad.engine.config.security;


import it.cleverad.engine.business.UserBusiness;
import it.cleverad.engine.persistence.repository.service.UserRepository;
import it.cleverad.engine.web.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserBusiness userBusiness;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDTO userDTO = userBusiness.findByUsername(username);
        if (userDTO != null) {
            return new org.springframework.security.core.userdetails.User(userDTO.getUsername(), userDTO.getPassword(), true, true, true, true, new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("Nessun utente Cleverad trovato per: " + username);
        }

    }

//    private List<SimpleGrantedAuthority> getAuthority(User user) {
//        return user.getRoles().stream()
//                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
//                .collect(Collectors.toList());
//    }

    public Long getAffiliateId() {
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

    public Long getAdvertiserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userBusiness.findByUsername(username).getAdvertiserId() != null)
            return userBusiness.findByUsername(username).getAdvertiserId();
        else return 0L;
    }

    public String getRole() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username.equals("anonymousUser") || userBusiness.findByUsername(username).getRoleId() == 3) {
            return "Admin";
        } else if (userBusiness.findByUsername(username).getRoleId() == 555) {
            return "Advertiser";
        } else {
            return "User";
        }
    }

    public Boolean isAdmin() {
        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return username != null && (username.equals("anonymousUser") || userBusiness.findByUsername(username).getRoleId() == 3);
        } else return false;
    }

    public Boolean isAdvertiser() {
        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return username != null && userBusiness.findByUsername(username).getRoleId() == 555;
        } else return false;
    }

    public Boolean isAffiliate() {
        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return username != null && userBusiness.findByUsername(username).getRoleId() == 4;
        } else return false;
    }

}