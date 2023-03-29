package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.User;
import it.cleverad.engine.persistence.repository.service.AffiliateRepository;
import it.cleverad.engine.persistence.repository.service.DictionaryRepository;
import it.cleverad.engine.persistence.repository.service.UserRepository;
import it.cleverad.engine.web.dto.AffiliateDTO;
import it.cleverad.engine.web.dto.UserDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
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
    private FileUserBusiness fileUserBusiness;

    @Autowired
    private AffiliateRepository affiliateRepository;
    @Autowired
    private DictionaryRepository dictionaryRepository;

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
        map.setAffiliate(affiliateRepository.findById(request.affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliate", request.affiliateId)));
        map.setDictionary(dictionaryRepository.findById(request.roleId).orElseThrow(() -> new ElementCleveradException("Dictionary", request.roleId)));
        return UserDTO.from(repository.save(map));
    }

    // GET BY ID
    public UserDTO findById(Long id) {
        User uuu = repository.findById(id).orElseThrow(() -> new ElementCleveradException("User", id));
        return UserDTO.from(uuu);
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
                Page<User> page = repository.findAll(getSpecification(request), PageRequest.of(0, 1, Sort.by(Sort.Order.asc("id"))));
                UserDTO dto = UserDTO.from(page.stream().findFirst().get());

                if (dto.getRoleId() == 3) {
                    dto.setRole("Admin");
                } else {
                    dto.setRole("User");
                    AffiliateDTO affiliate = affiliateBusiness.findById(dto.getAffiliateId());
                    dto.setAffiliateName(affiliate.getName());
                }
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
        try {
            repository.deleteById(id);
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // SEARCH PAGINATED
    public Page<UserDTO> search(Filter request, Pageable pageableRequest) {
        Page<User> page = repository.findAll(getSpecification(request), PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id"))));
        return page.map(user -> {
            return UserDTO.from(user);
        });
    }

    // SEARCH By AFFILIATE ID
    public Page<UserDTO> searchByAffiliateID(Long affiliateId, Pageable pageable) {
        Filter request = new Filter();
        request.setAffiliateId(affiliateId);
        return this.search(request, pageable);
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
        User wser = repository.findById(id).orElseThrow(() -> new ElementCleveradException("User", id));
        wser.setPassword(bcryptEncoder.encode(password));
        return UserDTO.from(repository.save(wser));
    }

    public UserDTO disableUser(Long id) throws Exception {
        User wser = repository.findById(id).orElseThrow(() -> new ElementCleveradException("User", id));
        wser.setStatus(false);
        return UserDTO.from(repository.save(wser));
    }

    public UserDTO enableUser(Long id) throws Exception {
        User wser = repository.findById(id).orElseThrow(() -> new ElementCleveradException("User", id));
        wser.setStatus(true);
        return UserDTO.from(repository.save(wser));
    }

    public UserDTO confirm(Confirm rr) {

        log.info(rr.toString());
        User user = repository.findByUsername(rr.uuid);
        log.info(user.toString());

        BaseCreateRequest request = new BaseCreateRequest();
        request.setUsername(rr.getUsername());
        request.setPassword(rr.getPassword());
        request.setSurname(user.getSurname());
        request.setName(user.getName());
        request.setStatus(true);
        request.setAffiliateId(user.getAffiliate().getId());
        request.setRoleId(4L);
        request.setEmail(user.getEmail());
        request.setRole("User");

        repository.deleteById(user.getId());
        // TODO INVIO MAIL DI CONFERMA ATTIVAZIONE UTENTE???
        log.info(">> " + request.toString());
        return this.create(request);
    }

    public UserDTO getUserToRegister(String uuid) {
        User user = repository.findByUsername(uuid);
        //user.setId(0L);
        return UserDTO.from(user);
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
                predicates.add(cb.equal(root.get("affiliate").get("id"), request.getAffiliateId()));
            }
            if (request.getRoleId() != null) {
                predicates.add(cb.equal(root.get("dictionary").get("id"), request.getRoleId()));
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
        private String role;
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
        private String uuid;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Confirm {
        private String username;
        private String password;
        private String uuid;
    }
}
