package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Representative;
import it.cleverad.engine.persistence.repository.AdvertiserRepository;
import it.cleverad.engine.persistence.repository.AffiliateRepository;
import it.cleverad.engine.persistence.repository.DictionaryRepository;
import it.cleverad.engine.persistence.repository.RepresentativeRepository;
import it.cleverad.engine.web.dto.RepresentativeDTO;
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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class RepresentativeBusiness {

    @Autowired
    private RepresentativeRepository repository;

    @Autowired
    private AffiliateRepository affiliateRepository;
    @Autowired
    private AdvertiserRepository advertiserRepository;
    @Autowired
    private DictionaryRepository dictionaryRepository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public RepresentativeDTO create(BaseCreateRequest request, String tipo) {
        Representative map = mapper.map(request, Representative.class);
        if (tipo.equals("AFFILIATE")) {
            map.setAffiliate(affiliateRepository.findById(request.affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliate", request.affiliateId)));
        } else if (tipo.equals("ADVERTISER")) {
            map.setAdvertiser(advertiserRepository.findById(request.advertiserId).orElseThrow(() -> new ElementCleveradException("Advertiser", request.advertiserId)));
        }
        map.setDictionary(dictionaryRepository.findById(request.roleId).orElseThrow(() -> new ElementCleveradException("Dictionary", request.roleId)));
        return RepresentativeDTO.from(repository.save(map));
    }

    // GET BY ID
    public RepresentativeDTO findById(Long id) {
        Representative entity = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Representative", id));
        return RepresentativeDTO.from(entity);
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
    public Page<RepresentativeDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Representative> page = repository.findAll(getSpecification(request), pageable);
        return page.map(RepresentativeDTO::from);
    }

    public Page<RepresentativeDTO> findByIdAffilaite(Long id) {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setAffiliateId(id);
        Page<Representative> page = repository.findAll(getSpecification(request), pageable);
        return page.map(RepresentativeDTO::from);
    }

    public Page<RepresentativeDTO> findByIdAdvertiser(Long id) {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setAdvertiserId(id);
        Page<Representative> page = repository.findAll(getSpecification(request), pageable);
        return page.map(RepresentativeDTO::from);
    }

    // UPDATE
    public RepresentativeDTO update(Long id, Filter filter, String tipo) {
        Representative channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Representative", id));
        RepresentativeDTO campaignDTOfrom = RepresentativeDTO.from(channel);

        mapper.map(filter, campaignDTOfrom);

        Representative mappedEntity = mapper.map(channel, Representative.class);
        mapper.map(campaignDTOfrom, mappedEntity);

        if (tipo.equals("AFFILIATE")) {
            mappedEntity.setAffiliate(affiliateRepository.findById(filter.affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliate", filter.affiliateId)));
        } else if (tipo.equals("ADVERTISER")) {
            mappedEntity.setAdvertiser(advertiserRepository.findById(filter.advertiserId).orElseThrow(() -> new ElementCleveradException("Advertiser", filter.advertiserId)));
        }
        mappedEntity.setDictionary(dictionaryRepository.findById(filter.roleId).orElseThrow(() -> new ElementCleveradException("Dictionary", filter.roleId)));
        return RepresentativeDTO.from(repository.save(mappedEntity));
    }

    /**
     * ============================================================================================================
     **/
    
    private Specification<Representative> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
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
        private String phonePrefix;
        private String phone;
        private String mobilePrefix;
        private String mobile;

        private Long affiliateId;
        private Long advertiserId;
        private Long roleId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;

        private String name;
        private String surname;
        private String email;
        private String phonePrefix;
        private String phone;
        private String mobilePrefix;
        private String mobile;

        private Long affiliateId;
        private Long advertiserId;
        private Long roleId;

        private Boolean status;
    }

}
