package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Editor;
import it.cleverad.engine.persistence.repository.service.EditorRepository;
import it.cleverad.engine.web.dto.EditorDTO;
import it.cleverad.engine.web.dto.WalletDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
public class EditorBusiness {

    @Autowired
    private EditorRepository repository;

    @Autowired
    private WalletBusiness walletBusiness;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public EditorDTO create(BaseCreateRequest request) {
        Editor map = mapper.map(request, Editor.class);
        return EditorDTO.from(repository.save(map));
    }

    // GET BY ID
    public EditorDTO findById(Long id) {
        Editor editor = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Editor", id));
        return EditorDTO.from(editor);
    }

    // DELETE BY ID
    public void delete(Long id) {
        try {
            Page<WalletDTO> dd = walletBusiness.findByIdAffilaite(id);
            WalletDTO dto = dd.stream().findFirst().get();
            if (dto.getTotal() > 0) {
                // invio messaggio specifico su lfatto che c'è un wallet con contenuto
                throw new PostgresCleveradException("Impossibile cancellare affiliato " + dto.getNome() + " perchè associato ad un wallet.");
            } else {
                walletBusiness.delete(dto.getId());
                repository.deleteById(id);
            }
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // SEARCH PAGINATED
    public Page<EditorDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Editor> page = repository.findAll(getSpecification(request), pageable);
        return page.map(EditorDTO::from);
    }

    // UPDATE
    public EditorDTO update(Long id, Filter filter) {

        Editor editor = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Editor", id));
        EditorDTO affiliateDTOfrom = EditorDTO.from(editor);
        mapper.map(filter, affiliateDTOfrom);

        Editor mappedEntity = mapper.map(editor, Editor.class);
        mappedEntity.setLastModificationDate(LocalDateTime.now());
        mapper.map(affiliateDTOfrom, mappedEntity);

        return EditorDTO.from(repository.save(mappedEntity));
    }

    /**
     * ============================================================================================================
     **/
    private Specification<Editor> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getName() != null) {
                predicates.add(cb.equal(root.get("name"), request.getName()));
            }
            if (request.getVatNumber() != null) {
                predicates.add(cb.equal(root.get("vatNumber"), request.getVatNumber()));
            }
            if (request.getStreet() != null) {
                predicates.add(cb.equal(root.get("street"), request.getStreet()));
            }
            if (request.getStreetNumber() != null) {
                predicates.add(cb.equal(root.get("streetNumber"), request.getStreetNumber()));
            }

            if (request.getCity() != null) {
                predicates.add(cb.equal(root.get("city"), request.getCity()));
            }
            if (request.getZipCode() != null) {
                predicates.add(cb.equal(root.get("zipCode"), request.getZipCode()));
            }
            if (request.getPrimaryMail() != null) {
                predicates.add(cb.equal(root.get("primaryMail"), request.getPrimaryMail()));
            }
            if (request.getSecondaryMail() != null) {
                predicates.add(cb.equal(root.get("secondaryMail"), request.getSecondaryMail()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }
            if (request.getCreationDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("creationDate"), LocalDateTime.ofInstant(request.getCreationDateFrom(), ZoneOffset.UTC)));
            }
            if (request.getCreationDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("creationDate"), LocalDateTime.ofInstant(request.getCreationDateTo().plus(1, ChronoUnit.DAYS), ZoneOffset.UTC)));
            }

            if (request.getLastModificationDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("lastModificationDate"), LocalDateTime.ofInstant(request.getLastModificationDateFrom(), ZoneOffset.UTC)));
            }
            if (request.getLastModificationDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("lastModificationDate"), LocalDateTime.ofInstant(request.getLastModificationDateTo().plus(1, ChronoUnit.DAYS), ZoneOffset.UTC)));
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
        private String vatNumber;
        private String street;
        private String streetNumber;
        private String city;
        private String zipCode;
        private String primaryMail;
        private String secondaryMail;
        private Boolean status;
        private String country;
        private String phonePrefix;
        private String phoneNumber;
        private String note;
        private String bank;
        private String iban;
        private String swift;
        private String paypal;
        private String province;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;

        private String name;
        private String vatNumber;
        private String street;
        private String streetNumber;
        private String city;
        private String zipCode;
        private String primaryMail;
        private String secondaryMail;
        private Boolean status;
        private String phonePrefix;
        private String phoneNumber;
        private String note;
        private String bank;
        private String iban;
        private String swift;
        private String paypal;
        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;
        private String province;
        private String country;
    }

}