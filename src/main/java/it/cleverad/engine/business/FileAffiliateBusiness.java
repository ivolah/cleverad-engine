package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Affiliate;
import it.cleverad.engine.persistence.model.Dictionary;
import it.cleverad.engine.persistence.model.FileAffiliate;
import it.cleverad.engine.persistence.repository.AffiliateRepository;
import it.cleverad.engine.persistence.repository.DictionaryRepository;
import it.cleverad.engine.persistence.repository.FileAffiliateRepository;
import it.cleverad.engine.web.dto.DictionaryDTO;
import it.cleverad.engine.web.dto.FileAffiliateDTO;
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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class FileAffiliateBusiness {

    @Autowired
    private FileAffiliateRepository repository;
    @Autowired
    private AffiliateRepository affiliateRepository;
    @Autowired
    private DictionaryRepository dictionaryRepository;
    @Autowired
    private DictionaryBusiness dictionaryBusiness;
    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public Long store(MultipartFile file, BaseCreateRequest request) throws IOException {
        Affiliate aff = affiliateRepository.findById(request.affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliate", request.affiliateId));
        Dictionary dictionary = (dictionaryRepository.findById(request.dictionaryId).orElseThrow(() -> new ElementCleveradException("Dictionary", request.dictionaryId)));
        FileAffiliate fileDB = new FileAffiliate(
                StringUtils.cleanPath(file.getOriginalFilename()),
                file.getContentType(),
                request.getDocType(),
                file.getBytes(),
                aff,
                dictionary);
        return repository.save(fileDB).getId();
    }

    // GET BY ID
    public FileAffiliateDTO findById(Long id) {
        FileAffiliate file = repository.findById(id).orElseThrow(() -> new ElementCleveradException("FileAffiliate", id));
        return FileAffiliateDTO.from(file);
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
    public Page<FileAffiliateDTO> search(FileAffiliateBusiness.Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<FileAffiliate> page = repository.findAll(getSpecification(request), pageable);
        return page.map(FileAffiliateDTO::from);
    }

    // UPDATE
    public FileAffiliateDTO update(Long id, FileAffiliateBusiness.Filter filter) {
        FileAffiliate fil = repository.findById(id).orElseThrow(() -> new ElementCleveradException("File", id));
        FileAffiliateDTO from = FileAffiliateDTO.from(fil);
        mapper.map(filter, from);
        FileAffiliate mappedEntity = mapper.map(fil, FileAffiliate.class);
        mapper.map(from, mappedEntity);
        mappedEntity.setDictionary(dictionaryRepository.findById(filter.dictionaryId).orElseThrow(() -> new ElementCleveradException("Dictionary", filter.dictionaryId)));
        mappedEntity.setAffiliate(affiliateRepository.findById(filter.affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliate", filter.affiliateId)));
        return FileAffiliateDTO.from(repository.save(mappedEntity));
    }

    //  GET TIPI
    public Page<DictionaryDTO> getTypes() {
        return dictionaryBusiness.getTypeDocument();
    }

    /**
     * ============================================================================================================
     **/
    private Specification<FileAffiliate> getSpecification(FileAffiliateBusiness.Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
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

            if (request.getAffiliateId() != null) {
                predicates.add(cb.equal(root.get("affiliate").get("id"), request.getAffiliateId()));
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
        private String type;
        private byte[] data;
        private Long affiliateId;
        private Long dictionaryId;
        private String docType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String name;
        private String type;
        private byte[] data;
        private Long affiliateId;
        private Long dictionaryId;
        private String docType;
        private Instant creationDateFrom;
        private Instant creationDateTo;

    }

}