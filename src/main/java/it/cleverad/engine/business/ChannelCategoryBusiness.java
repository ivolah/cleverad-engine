package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Category;
import it.cleverad.engine.persistence.model.Channel;
import it.cleverad.engine.persistence.model.ChannelCategory;
import it.cleverad.engine.persistence.repository.ChannelCategoryRepository;
import it.cleverad.engine.web.dto.ChannelCategoryDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class ChannelCategoryBusiness {

    @Autowired
    private ChannelCategoryRepository repository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public ChannelCategoryDTO create(BaseCreateRequest request) {
        ChannelCategory map = mapper.map(request, ChannelCategory.class);
        map.setCreationDate(LocalDateTime.now());
        map.setLastModificationDate(LocalDateTime.now());

        Category cat = new Category();
        cat.setId(request.getCategoryId());
        map.setCategory(cat);

        Channel channel = new Channel();
        channel.setId(request.getChannelId());
        map.setChannel(channel);

        return ChannelCategoryDTO.from(repository.save(map));
    }

    // GET BY ID
    public ChannelCategoryDTO findById(Long id) {
        ChannelCategory channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException(id));
        return ChannelCategoryDTO.from(channel);
    }

    // DELETE BY ID
    public void delete(Long id) {
         try {
            repository.deleteById(id);
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // SEARCH PAGINATED
    public Page<ChannelCategoryDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<ChannelCategory> page = repository.findAll(getSpecification(request), pageable);

        return page.map(ChannelCategoryDTO::from);
    }

    // UPDATE
    public ChannelCategoryDTO update(Long id, Filter filter) {
        try {
            ChannelCategory channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException(id));
            ChannelCategoryDTO campaignDTOfrom = ChannelCategoryDTO.from(channel);

            mapper.map(filter, campaignDTOfrom);

            ChannelCategory mappedEntity = mapper.map(channel, ChannelCategory.class);
            mappedEntity.setLastModificationDate(LocalDateTime.now());
            mapper.map(campaignDTOfrom, mappedEntity);

            return ChannelCategoryDTO.from(repository.save(mappedEntity));
        } catch (Exception e) {
            log.error("Errore in update", e);
            return null;
        }
    }


    /**
     * ============================================================================================================
     **/
    private Specification<ChannelCategory> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }

            if (request.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("categoryId"), request.getCategoryId()));
            }
            if (request.getChannelId() != null) {
                predicates.add(cb.equal(root.get("channelId"), request.getChannelId()));
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

        private Long channelId;
        private Long categoryId;

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDate endDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;

        private Long channelId;
        private Long categoryId;

        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;
    }

}

