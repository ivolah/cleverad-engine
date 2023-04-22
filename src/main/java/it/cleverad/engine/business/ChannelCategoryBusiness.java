package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Category;
import it.cleverad.engine.persistence.model.service.Channel;
import it.cleverad.engine.persistence.model.service.ChannelCategory;
import it.cleverad.engine.persistence.repository.service.CategoryRepository;
import it.cleverad.engine.persistence.repository.service.ChannelCategoryRepository;
import it.cleverad.engine.persistence.repository.service.ChannelRepository;
import it.cleverad.engine.web.dto.ChannelCategoryDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
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
public class ChannelCategoryBusiness {

    @Autowired
    private ChannelCategoryRepository repository;

    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private CategoryRepository categoryRepository;

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

        Category cat = categoryRepository.findById(request.getChannelId()).orElseGet(() -> {
            Category ccc = new Category();
            ccc.setId(request.getCategoryId());
            return ccc;
        });
        map.setCategory(cat);

        Channel channel = channelRepository.findById(request.getChannelId()).orElseGet(() -> {
            Channel cc = new Channel();
            cc.setId(request.getChannelId());
            return cc;
        });
        map.setChannel(channel);

        return ChannelCategoryDTO.from(repository.save(map));
    }


    public ChannelCategory createEntity(BaseCreateRequest request) {
        ChannelCategory map = mapper.map(request, ChannelCategory.class);
        map.setCreationDate(LocalDateTime.now());
        map.setLastModificationDate(LocalDateTime.now());

        Category cat = new Category();
        cat.setId(request.getCategoryId());
        map.setCategory(cat);

        Channel channel = new Channel();
        channel.setId(request.getChannelId());
        map.setChannel(channel);

        return repository.save(map);
    }

    public void deleteByChannelID(Long id) {
        Filter request = new Filter();
        request.setChannelId(id);
        Page<ChannelCategory> page = repository.findAll(getSpecification(request), PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("id"))));
        try {
            page.stream().forEach(channelCategory -> repository.deleteById(channelCategory.getId()));
        } catch (javax.validation.ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // GET BY ID
    public ChannelCategoryDTO findById(Long id) {
        ChannelCategory channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("ChannelCategory", id));
        return ChannelCategoryDTO.from(channel);
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
    public Page<ChannelCategoryDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<ChannelCategory> page = repository.findAll(getSpecification(request), pageable);

        return page.map(ChannelCategoryDTO::from);
    }

    // UPDATE
    public ChannelCategoryDTO update(Long id, Filter filter) {
        ChannelCategory channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("ChannelCategory", id));
        ChannelCategoryDTO channelCategoryDTO = ChannelCategoryDTO.from(channel);

        mapper.map(filter, channelCategoryDTO);

        ChannelCategory mappedEntity = mapper.map(channel, ChannelCategory.class);
        mappedEntity.setLastModificationDate(LocalDateTime.now());
        mapper.map(channelCategoryDTO, mappedEntity);

        return ChannelCategoryDTO.from(repository.save(mappedEntity));
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
                predicates.add(cb.equal(root.get("category").get("id"), request.getCategoryId()));
            }
            if (request.getChannelId() != null) {
                predicates.add(cb.equal(root.get("channel").get("id"), request.getChannelId()));
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
