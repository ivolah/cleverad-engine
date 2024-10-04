package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.Feed;
import it.cleverad.engine.persistence.repository.service.AdvertiserRepository;
import it.cleverad.engine.persistence.repository.service.FeedRepository;
import it.cleverad.engine.web.dto.FeedDTO;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class FeedBusiness {

    @Autowired
    private FeedRepository repository;
    @Autowired
    private AdvertiserRepository advertiserRepository;
    @Autowired
    private FileFeedBusiness fileFeedBusiness;
    @Autowired
    private Mapper mapper;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public FeedDTO create(BaseCreateRequest request) {
        if (jwtUserDetailsService.isAdvertiser())
            request.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());

        Feed map = mapper.map(request, Feed.class);
        map.setAdvertiser(advertiserRepository.findById(request.advertiserId).orElseThrow(() -> new ElementCleveradException("Advertiser", request.advertiserId)));
        return FeedDTO.from(repository.save(map));
    }

    // GET BY ID
    public FeedDTO findById(Long id) {
        Feed channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Feed", id));
        return FeedDTO.from(channel);
    }

    // DELETE BY ID
    public void delete(Long id) {
        try {
            fileFeedBusiness.deleteByFeedID(id);
            repository.deleteById(id);
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // SEARCH PAGINATED
    public Page<FeedDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Feed> page = repository.findAll(getSpecification(request), pageable);
        return page.map(FeedDTO::from);
    }

    // UPDATE
    public FeedDTO update(Long id, Filter filter) {
        if (jwtUserDetailsService.isAdvertiser())
            filter.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
        Feed feed = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Feed", id));
        mapper.map(filter, feed);
        feed.setId(id);
        feed.setAdvertiser(advertiserRepository.findById(filter.advertiserId).orElseThrow(() -> new ElementCleveradException("Advertiser", filter.advertiserId)));
        return FeedDTO.from(repository.save(feed));
    }

    /**
     * ============================================================================================================
     **/
    private Specification<Feed> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate;
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
            if (request.getStartFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("start"), request.getStartFrom()));
            }
            if (request.getStartTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("start"), request.getStartTo()));
            }
            if (request.getEndFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("end"), request.getEndFrom()));
            }
            if (request.getEndTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("end"), request.getEndTo()));
            }
            if (request.getAdvertiserId() != null) {
                predicates.add(cb.equal(root.get("advertiser").get("id"), request.getAdvertiserId()));
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
        private String description;
        private String urlPromo;
        private Boolean status;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
        private Long advertiserId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String name;
        private String description;
        private String urlPromo;
        private Boolean status;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
        private LocalDate startFrom;
        private LocalDate startTo;
        private LocalDate endFrom;
        private LocalDate endTo;
        private Long advertiserId;
    }

}