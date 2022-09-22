package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.ContactForm;
import it.cleverad.engine.persistence.repository.ContactFormRepository;
import it.cleverad.engine.web.dto.ContactFormDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@Transactional
public class ContactFormBusiness {

    @Autowired
    private ContactFormRepository repository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public ContactFormDTO create(BaseCreateRequest request) {
        ContactForm map = mapper.map(request, ContactForm.class);
        map.setCreationDate(LocalDateTime.now());
        return ContactFormDTO.from(repository.save(map));
    }

    // GET BY ID
    public ContactFormDTO findById(Long id) {
        try {
            ContactForm ContactForm = repository.findById(id).orElseThrow(Exception::new);
            return ContactFormDTO.from(ContactForm);
        } catch (Exception e) {
            log.error("Errore in findById", e);
            return null;
        }
    }

    // DELETE BY ID
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // SEARCH PAGINATED
    public Page<ContactFormDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<ContactForm> page = repository.findAll(getSpecification(request), pageable);

        return page.map(ContactFormDTO::from);
    }

    // UPDATE
    public ContactFormDTO update(Long id, Filter filter) {
        try {
            ContactForm ContactForm = repository.findById(id).orElseThrow(Exception::new);
            ContactFormDTO ContactFormDTOfrom = ContactFormDTO.from(ContactForm);

            mapper.map(filter, ContactFormDTOfrom);

            ContactForm mappedEntity = mapper.map(ContactForm, ContactForm.class);
            mapper.map(ContactFormDTOfrom, mappedEntity);

            return ContactFormDTO.from(repository.save(mappedEntity));
        } catch (Exception e) {
            log.error("Errore in update", e);
            return null;
        }
    }

    /**
     * ============================================================================================================
     **/
    private Specification<ContactForm> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getCreationDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("creationDate"), LocalDateTime.ofInstant(request.getCreationDateFrom(), ZoneOffset.UTC)));
            }
            if (request.getCreationDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("creationDate"), LocalDateTime.ofInstant(request.getCreationDateTo().plus(1, ChronoUnit.DAYS), ZoneOffset.UTC)));
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
        private String surname;
        private String email;
        private String phoneNumber;
        private String companyName;
        private String country;
        private String requestType;
        private String enquiry;
        private Boolean agreeMailingList;
        private Boolean agreeDataProcetction;

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime creationDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String name;
        private String surname;
        private String email;
        private String phoneNumber;
        private String companyName;
        private String country;
        private String requestType;
        private String enquiry;
        private Boolean agreeMailingList;
        private Boolean agreeDataProcetction;

        private Instant creationDateFrom;
        private Instant creationDateTo;
    }

}
