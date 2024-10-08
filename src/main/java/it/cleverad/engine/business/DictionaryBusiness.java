package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Dictionary;
import it.cleverad.engine.persistence.repository.service.DictionaryRepository;
import it.cleverad.engine.web.dto.DictionaryDTO;
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
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class DictionaryBusiness {

    @Autowired
    private DictionaryRepository repository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public DictionaryDTO create(BaseCreateRequest request) {
        Dictionary map = mapper.map(request, Dictionary.class);
        return DictionaryDTO.from(repository.save(map));
    }

    // GET BY ID
    public DictionaryDTO findById(Long id) {
        Dictionary media = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Dictionary", id));
        return DictionaryDTO.from(media);
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

    // UPDATE
    public DictionaryDTO update(Long id, Filter filter) {
        Dictionary media = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Dictionary", id));
        DictionaryDTO mediaDTOfrom = DictionaryDTO.from(media);
        mapper.map(filter, mediaDTOfrom);

        Dictionary mappedEntity = mapper.map(media, Dictionary.class);
        mapper.map(mediaDTOfrom, mappedEntity);
        return DictionaryDTO.from(repository.save(mappedEntity));
    }

    // SEARCH PAGINATED
    public Page<DictionaryDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Dictionary> page = repository.findAll(getSpecification(request), pageable);
        return page.map(DictionaryDTO::from);
    }

    public Page<DictionaryDTO> getTypeStatus(Filter request, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        request.setType("STATUS");
        Page<Dictionary> page = repository.findAll(getSpecification(request), pageable);
        return page.map(DictionaryDTO::from);
    }

    public Page<DictionaryDTO> getTypeRole(Filter request, @PageableDefault(value = Integer.MAX_VALUE) Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        request.setType("ROLE");
        Page<Dictionary> page = repository.findAll(getSpecification(request), pageable);
        return page.map(DictionaryDTO::from);
    }

    public Page<DictionaryDTO> getTypeCommission() {
        return this.getStatusTypes("COMMISSION");
    }

    public Page<DictionaryDTO> getTypeChannel() {
        return this.getStatusTypes("CHANNEL");
    }

    public Page<DictionaryDTO> getBusinessTypeChannel() {
        return this.getStatusTypes("CHANNELBT");
    }

    public Page<DictionaryDTO> getTypePayout() {
        return this.getStatusTypes("PAYOUT");
    }

    public Page<DictionaryDTO> getTypeDocument() {
        return this.getStatusTypes("DOCTYPE");
    }

    public Page<DictionaryDTO> getTypeCompany() {
        return this.getStatusTypes("COMPANYTYPE");
    }

    public Page<DictionaryDTO> getChannelTypeAffiliate() {
        return this.getStatusTypes("CHANNELTYPE");
    }

    public Page<DictionaryDTO> getTransactionTypes() {
        return this.getStatusTypes("TRANSACTIONS");
    }

    public Page<DictionaryDTO> getFilePayoutTypes() {
        return this.getStatusTypes("FILEPAYOUT");
    }

    public Page<DictionaryDTO> getPlatfromBB() {
        return this.getStatusTypes("BBCHANNEL");
    }

    public Page<DictionaryDTO> getAffiliateStatusTypes() {
        return this.getStatusTypes("AFFILIATESTATUS");
    }

    public Page<DictionaryDTO> getAffiliateCampaignRequestStatusTypes() {
        return this.getStatusTypes("AFFILIATEREQUESTSTATUS");
    }

    private Page<DictionaryDTO> getStatusTypes(String tipo) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setType(tipo);
        Page<Dictionary> page = repository.findAll(getSpecification(request), pageable);
        return page.map(DictionaryDTO::from);
    }

    /**
     * ============================================================================================================
     **/
    private Specification<Dictionary> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getName() != null) {
                predicates.add(cb.equal(root.get("name"), request.getName()));
            }

            if (request.getType() != null) {
                predicates.add(cb.equal(root.get("type"), request.getType()));
            }

            if (request.getDescription() != null) {
                predicates.add(cb.equal(root.get("description"), request.getDescription()));
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
        private String type;
        private boolean status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String name;
        private String description;
        private String type;
        private boolean status;
    }

}