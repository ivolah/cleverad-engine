package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.User;
import it.cleverad.engine.persistence.repository.service.AdvertiserRepository;
import it.cleverad.engine.persistence.repository.service.AffiliateRepository;
import it.cleverad.engine.persistence.repository.service.DictionaryRepository;
import it.cleverad.engine.persistence.repository.service.UserRepository;
import it.cleverad.engine.service.MailService;
import it.cleverad.engine.web.dto.AdvertiserDTO;
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
import java.util.UUID;

@Slf4j
@Component
@Transactional
public class UserBusiness {

    @Autowired
    private UserRepository repository;
    @Autowired
    private AffiliateBusiness affiliateBusiness;
    @Autowired
    private AffiliateRepository affiliateRepository;
    @Autowired
    private AdvertiserBusiness advertiserBusiness;
    @Autowired
    private AdvertiserRepository advertiserRepository;
    @Autowired
    private MailService mailService;
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
        if (map.getRole() == null)
            map.setRole("User");
        map.setCreationDate(LocalDateTime.now());
        map.setPassword(bcryptEncoder.encode(request.getPassword()));
        if (request.getAffiliateId() != null)
            map.setAffiliate(affiliateRepository.findById(request.affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliate", request.affiliateId)));
        if (request.getAdvertiserId() != null)
            map.setAdvertiser(advertiserRepository.findById(request.advertiserId).orElseThrow(() -> new ElementCleveradException("Advertser", request.advertiserId)));
        map.setDictionary(dictionaryRepository.findById(request.roleId).orElseThrow(() -> new ElementCleveradException("Dictionary", request.roleId)));
        UserDTO dto = UserDTO.from(repository.save(map));
        log.info("::: CREATO UTENTE ::: {}", dto);
        return dto;
    }

    // GET BY ID
    public UserDTO findById(Long id) {
        User uuu = repository.findById(id).orElseThrow(() -> new ElementCleveradException("User", id));
        return UserDTO.from(uuu);
    }

    // GET BY username
    public UserDTO findByUsername(String username) {
        try {
            if (username.equals("anonymousUser")) {
                UserDTO dto = new UserDTO();
                dto.setRole("Admin");
                return dto;
            } else if (StringUtils.isNotBlank(username.trim())) {
                Filter request = new Filter();
                request.setUsername(username);
                Page<User> page = repository.findAll(getSpecification(request), PageRequest.of(0, 1, Sort.by(Sort.Order.asc("id"))));
                if (page.getTotalElements() > 0) {
                    UserDTO dto = UserDTO.from(page.stream().findFirst().get());

                    if (dto.getRoleId() == 3) {
                        dto.setRole("Admin");
                    } else if (dto.getRoleId() == 4) {
                        dto.setRole("User");
                        AffiliateDTO affiliate = affiliateBusiness.findById(dto.getAffiliateId());
                        dto.setAffiliateName(affiliate.getName());
                    } else if (dto.getRoleId() == 555) {
                        dto.setRole("Advertiser");
                        AdvertiserDTO advertiserDTO = advertiserBusiness.findById(dto.getAdvertiserId());
                        dto.setAdvertiserName(advertiserDTO.getName());
                    }

                    return dto;
                } else {
                    log.warn("No username found :: {}", username);
                    return null;
                }
            } else {
                UserDTO dto = new UserDTO();
                dto.setRole("Admin");
                log.warn("USERNAME VUOTO?? - setto Administrator");
                return dto;
            }
        } catch (Exception e) {
            log.error("Errore in findByUsername", e);
            return null;
        }
    }

    public UserDTO findByPassword(String passw) {
        try {
            if (StringUtils.isNotBlank(passw.trim())) {
                Filter request = new Filter();
                request.setPassword(passw);
                Page<User> page = repository.findAll(getSpecification(request), PageRequest.of(0, 1));
                if (page.getTotalElements() > 0) {
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
                    log.warn("No username found :: {}", passw);
                    return null;
                }
            } else {
                log.warn("Passw:: {}", passw);
                return null;
            }
        } catch (Exception e) {
            log.error("Errore in findByPassword", e);
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

    public void deleteByIdAffiliate(Long affiliateId) {
        try {
            Page<UserDTO> page = this.searchByAffiliateID(affiliateId, PageRequest.of(0, Integer.MAX_VALUE));
            page.stream().forEach(userDTO -> repository.deleteById(userDTO.getId()));
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    public void deleteByIdAdvertiser(Long advertiserId) {
        try {
            Page<UserDTO> page = this.searchByAdvertiserId(advertiserId, PageRequest.of(0, Integer.MAX_VALUE));
            page.stream().forEach(userDTO -> repository.deleteById(userDTO.getId()));
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // SEARCH PAGINATED
    public Page<UserDTO> search(Filter request, Pageable pageableRequest) {
        Page<User> page = repository.findAll(getSpecification(request), PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id"))));
        return page.map(UserDTO::from);
    }

    // SEARCH By AFFILIATE ID
    public Page<UserDTO> searchByAffiliateID(Long affiliateId, Pageable pageable) {
        Filter request = new Filter();
        request.setAffiliateId(affiliateId);
        return this.search(request, pageable);
    }

    // SEARCH By AFFILIATE ID
    public Page<UserDTO> searchByAdvertiserId(Long advertiserId, Pageable pageable) {
        Filter request = new Filter();
        request.setAdvertiserId(advertiserId);
        return this.search(request, pageable);
    }

    // UPDATE
    public UserDTO update(Long id, Filter filter) {
        User wser = repository.findById(id).orElseThrow(() -> new ElementCleveradException("User", id));
        filter.setId(id);
        mapper.map(filter, wser);
        return UserDTO.from(repository.save(wser));
    }

    public UserDTO resetPassword(Long id, String password) {
        User wser = repository.findById(id).orElseThrow(() -> new ElementCleveradException("User", id));
        wser.setPassword(bcryptEncoder.encode(password));
        return UserDTO.from(repository.save(wser));
    }

    public UserDTO resetPasswordUsername(String username, String password) throws Exception {
        UserDTO u = this.findByPassword(username);
        User wser = repository.findById(u.getId()).orElseThrow(() -> new ElementCleveradException("User", u.getId()));
        wser.setPassword(bcryptEncoder.encode(password));
        return UserDTO.from(repository.save(wser));
    }

    public UserDTO requestResetPassword(String username, Boolean tipoAffilaite) throws Exception {

        if (StringUtils.isNotBlank(username)) {

            UserDTO userDTO = this.findByUsername(username);
            if (userDTO != null) {
                String uuid = UUID.randomUUID().toString();
                log.info("RESET PASSWORD sostituisco nome utenza  " + username + " (" + userDTO.getId() + ") in uuid " + uuid);

                User wser = repository.findById(userDTO.getId()).orElse(null);
                wser.setPassword(uuid);
                repository.save(wser);

                if (userDTO != null) {
                    MailService.BaseCreateRequest mailRequest = new MailService.BaseCreateRequest();
                    if (tipoAffilaite) {
                        AffiliateDTO affiliate = affiliateBusiness.findById(userDTO.getAffiliateId());
                        // invio mail USER Affilaite
                        if (affiliate.getBrandbuddies()) mailRequest.setTemplateId(23L);
                        else mailRequest.setTemplateId(3L);
                        mailRequest.setAffiliateId(userDTO.getAffiliateId());
                    } else {
                        // invio mail USER ADVERTISER
                        mailRequest.setAdvertiserId(userDTO.getAdvertiserId());
                        mailRequest.setTemplateId(3L);
                    }
                    mailRequest.setUserId(userDTO.getId());
                    mailRequest.setEmail(userDTO.getEmail());
                    mailService.invio(mailRequest);
                }
            }
            return null;
        } else {
            log.warn("Username empty or null!");
            throw new Exception();
        }
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

        BaseCreateRequest request = new BaseCreateRequest();
        request.setUsername(rr.getUsername());
        request.setPassword(rr.getPassword());
        request.setSurname(user.getSurname());
        request.setName(user.getName());
        request.setStatus(true);
        if (user.getAffiliate() != null)
            request.setAffiliateId(user.getAffiliate().getId());
        if (user.getAdvertiser() != null)
            request.setAffiliateId(user.getAdvertiser().getId());
        request.setRoleId(4L);
        request.setEmail(user.getEmail());
        request.setRole("User");

        repository.deleteById(user.getId());
        // TODO INVIO MAIL DI CONFERMA ATTIVAZIONE UTENTE???
        log.info(">> " + request);
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
            Predicate completePredicate;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getUsername() != null) {
                predicates.add(cb.equal(root.get("username"), request.getUsername()));
            }
            if (request.getPassword() != null) {
                predicates.add(cb.equal(root.get("password"), request.getPassword()));
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
            if (request.getAdvertiserId() != null) {
                predicates.add(cb.equal(root.get("advertiser").get("id"), request.getAdvertiserId()));
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
    @ToString
    public static class BaseCreateRequest {
        private String name;
        private String username;
        private String surname;
        private String email;
        private Boolean status;
        private Long affiliateId;
        private Long advertiserId;
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
        private String password;
        private Long advertiserId;
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