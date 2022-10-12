package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.User;
import it.cleverad.engine.persistence.repository.UserRepository;
import it.cleverad.engine.web.dto.AffiliateDTO;
import it.cleverad.engine.web.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class UserBusiness {

    @Autowired
    private UserRepository repository;

    @Autowired
    private AffiliateBusiness affiliateBusiness;

    @Autowired
    private Mapper mapper;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public UserDTO create(BaseCreateRequest request) {
        User map = mapper.map(request, User.class);
        map.setCreationDate(LocalDateTime.now());
        map.setPassword(bcryptEncoder.encode(request.getPassword()));
        return UserDTO.from(repository.save(map));
    }

    // GET BY ID
    public UserDTO findById(Long id) {
        try {
            User uuu = repository.findById(id).orElseThrow(Exception::new);
            UserDTO dto = UserDTO.from(uuu);
            AffiliateDTO affiliate = affiliateBusiness.findById(dto.getAffiliateId());
            dto.setAffiliateName(affiliate.getName());

            if (dto.getRoleId() == 3) {
                dto.setRole("Admin");
            } else {
                dto.setRole("Guest");
            }

            return dto;
        } catch (Exception e) {
            log.error("Errore in findById", e);
            return null;
        }
    }

    // GET BY username
    public UserDTO findByUsername(String username) {
        try {
           // log.debug("USER ::" + username);
            if (username.equals("anonymousUser")) {
                UserDTO dto = new UserDTO();
                dto.setRole("Admin");
                return dto;
            } else if (StringUtils.isNotBlank(username.trim())) {
                Filter request = new Filter();
                request.setUsername(username);
                UserDTO dto = UserDTO.from(repository.findOne(getSpecification(request)).orElseThrow(Exception::new));
                AffiliateDTO affiliate = affiliateBusiness.findById(dto.getAffiliateId());
                dto.setAffiliateName(affiliate.getName());
                if (dto.getRoleId() == 3) {
                    dto.setRole("Admin");
                } else {
                    dto.setRole("Guest");
                }

              //  log.info("role {}" , dto.getRole());
                return dto;
            } else {
                UserDTO dto = new UserDTO();
                dto.setRole("Admin");
                return dto;
            }
        } catch (Exception e) {
            log.error("Errore in findByUsername", e);
            return null;
        }
    }

    // DELETE BY ID
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // SEARCH PAGINATED
    public Page<UserDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<User> page = repository.findAll(getSpecification(request), pageable);

        return page.map(user -> {
            UserDTO dto = UserDTO.from(user);
            if (user.getAffiliateId() != null) {
                try {
                    AffiliateDTO affiliate = affiliateBusiness.findById(user.getAffiliateId());
                    dto.setAffiliateName(affiliate.getName());
                } catch (Exception e) {
                    log.warn("Errore in recupero dati affliliato : " + user.getAffiliateId());
                }
            }
            if (dto.getRoleId() == 3) {
                dto.setRole("Admin");
            } else {
                dto.setRole("Guest");
            }
            return dto;
        });
    }

    // UPDATE
    public UserDTO update(Long id, Filter filter) {
        User wser = repository.findById(id).get();
        UserDTO userDTOfrom = UserDTO.from(wser);
        mapper.map(filter, userDTOfrom);
        User mappedEntity = mapper.map(wser, User.class);
        mapper.map(userDTOfrom, mappedEntity);
        return UserDTO.from(repository.save(mappedEntity));
    }

    public UserDTO resetPassword(Long id, String password) throws Exception {
        User wser = repository.findById(id).orElseThrow(Exception::new);
        wser.setPassword(password);
        return UserDTO.from(repository.save(wser));
    }

    public UserDTO disableUser(Long id) throws Exception {
        User wser = repository.findById(id).orElseThrow(Exception::new);
        wser.setStatus(false);
        return UserDTO.from(repository.save(wser));
    }

    public UserDTO enableUser(Long id) throws Exception {
        User wser = repository.findById(id).orElseThrow(Exception::new);
        wser.setStatus(true);
        return UserDTO.from(repository.save(wser));
    }

    /**
     * ============================================================================================================
     **/
    private Specification<User> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getUsername() != null) {
                predicates.add(cb.equal(root.get("username"), request.getUsername()));
            }
            if (request.getName() != null) {
                predicates.add(cb.equal(root.get("name"), request.getName()));
            }
            if (request.getSurname() != null) {
                predicates.add(cb.equal(root.get("surname"), request.getSurname()));
            }
            if (request.getEmail() != null) {
                predicates.add(cb.equal(root.get("email"), request.getEmail()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }
            if (request.getAffiliateId() != null) {
                predicates.add(cb.equal(root.get("companyId"), request.getAffiliateId()));
            }
            if (request.getRoleId() != null) {
                predicates.add(cb.equal(root.get("roleId"), request.getRoleId()));
            }

            if (request.getLastLoginFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("lastLogin"), LocalDateTime.ofInstant(request.getLastLoginFrom(), ZoneOffset.UTC)));
            }
            if (request.getLastLoginTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("lastLogin"), LocalDateTime.ofInstant(request.getLastLoginTo().plus(1, ChronoUnit.DAYS), ZoneOffset.UTC)));
            }

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));

            return completePredicate;
        };
    }

    /**
     * ============================================================================================================
     **/

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BaseCreateRequest {
        private String name;
        private String username;
        private String surname;
        private String email;
        private Boolean status;
        private Long affiliateId;
        private Long roleId;
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String username;
        private String name;
        private String surname;
        private String email;
        private Boolean status;
        private Long affiliateId;
        private Long roleId;
        private Instant lastLoginFrom;
        private Instant lastLoginTo;
    }

}
