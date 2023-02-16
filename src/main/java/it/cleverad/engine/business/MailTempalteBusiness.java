package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.MailTemplate;
import it.cleverad.engine.persistence.repository.service.MailTemplateRepository;
import it.cleverad.engine.web.dto.MailTemplateDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
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
public class MailTempalteBusiness {

    @Autowired
    private MailTemplateRepository repository;

    @Autowired
    private Mapper mapper;


    /**
     * ============================================================================================================
     **/

    // CREATE
    public MailTemplateDTO create(BaseCreateRequest request) {
        MailTemplate map = mapper.map(request, MailTemplate.class);
        MailTemplate saved = repository.save(map);
        return MailTemplateDTO.from(saved);
    }

    // GET BY ID
    public MailTemplateDTO findById(Long id) {
        MailTemplate media = repository.findById(id).orElseThrow(() -> new ElementCleveradException("MailTemplate", id));
        MailTemplateDTO dto = MailTemplateDTO.from(media);
        return dto;
    }

    // DELETE BY ID
    public void delete(Long id) {
        MailTemplate media = repository.findById(id).orElseThrow(() -> new ElementCleveradException("MailTemplate", id));
        repository.deleteById(id);
    }

    // UPDATE
    public MailTemplateDTO update(Long id, Filter filter) {
        MailTemplate media = repository.findById(id).orElseThrow(() -> new ElementCleveradException("MailTemplate", id));
        MailTemplateDTO mediaDTOfrom = MailTemplateDTO.from(media);
        mapper.map(filter, mediaDTOfrom);
        MailTemplate mappedEntity = mapper.map(media, MailTemplate.class);
        mapper.map(mediaDTOfrom, mappedEntity);
        mappedEntity.setLastModificationDate(LocalDateTime.now());
        return MailTemplateDTO.from(repository.save(mappedEntity));
    }

    // SEARCH PAGINATED
    public Page<MailTemplateDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<MailTemplate> page = repository.findAll(getSpecification(request), pageable);
        return page.map(MailTemplateDTO::from);
    }

    /**
     * ============================================================================================================
     **/

    private Specification<MailTemplate> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getName() != null) {
                predicates.add(cb.equal(root.get("name"), request.getName()));
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
        private String subject;
        private String content;
        private Boolean status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;

        private String name;
        private String subject;
        private String content;
        private Boolean status;

        private Instant creationDateFrom;
        private Instant creationDateTo;
    }

}
