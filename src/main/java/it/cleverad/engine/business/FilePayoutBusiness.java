package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Dictionary;
import it.cleverad.engine.persistence.model.service.FilePayout;
import it.cleverad.engine.persistence.model.service.Payout;
import it.cleverad.engine.persistence.repository.service.DictionaryRepository;
import it.cleverad.engine.persistence.repository.service.FilePayoutRepository;
import it.cleverad.engine.persistence.repository.service.PayoutRepository;
import it.cleverad.engine.service.FileStoreService;
import it.cleverad.engine.service.MailService;
import it.cleverad.engine.web.dto.DictionaryDTO;
import it.cleverad.engine.web.dto.FilePayoutDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import java.util.UUID;

@Slf4j
@Component
@Transactional
public class FilePayoutBusiness {

    @Autowired
    private FilePayoutRepository repository;
    @Autowired
    private PayoutRepository payoutRepository;
    @Autowired
    private DictionaryRepository dictionaryRepository;
    @Autowired
    private DictionaryBusiness dictionaryBusiness;
    @Autowired
    private Mapper mapper;
    @Autowired
    private FileStoreService fileStoreService;
    @Autowired
    private MailService mailService;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public Long storeFile(MultipartFile file, BaseCreateRequest request) {
        try {
            Payout payout = payoutRepository.findById(request.payoutId).orElseThrow(() -> new ElementCleveradException("Payout", request.payoutId));
            Dictionary dictionary = (dictionaryRepository.findById(request.dictionaryId).orElseThrow(() -> new ElementCleveradException("Dictionary", request.dictionaryId)));
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            String path = fileStoreService.storeFile(payout.getAffiliate().getId(), "payout", UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(filename), file.getBytes());
            FilePayout fileDB = new FilePayout(filename, file.getContentType(), payout, dictionary, path);

            MailService.BaseCreateRequest mailRequest = new MailService.BaseCreateRequest();
            mailRequest.setAffiliateId(payout.getAffiliate().getId());
            mailRequest.setPayoutId(payout.getId());
            mailRequest.setEmail("linda.loturco@cleverad.it");
            mailService.inviaMailFatturaCaricata(mailRequest);

            return repository.save(fileDB).getId();
        } catch (Exception e) {
            throw new PostgresCleveradException("Errore uplaod: " + file.getOriginalFilename() + "!", e);
        }
    }

    // GET BY ID
    public FilePayoutDTO findById(Long id) {
        FilePayout file = repository.findById(id).orElseThrow(() -> new ElementCleveradException("File Payout ", id));
        return FilePayoutDTO.from(file);
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

    public void deleteFile(Long id) {
        try {
            fileStoreService.deleteFile(this.findById(id).getPath());
            repository.deleteById(id);
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // SEARCH PAGINATED
    public Page<FilePayoutDTO> search(FilePayoutBusiness.Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<FilePayout> page = repository.findAll(getSpecification(request), pageable);
        return page.map(FilePayoutDTO::from);
    }

    // UPDATE
    public FilePayoutDTO update(Long id, FilePayoutBusiness.Filter filter) {
        FilePayout fil = repository.findById(id).orElseThrow(() -> new ElementCleveradException("File", id));
        FilePayoutDTO from = FilePayoutDTO.from(fil);
        mapper.map(filter, from);
        FilePayout mappedEntity = mapper.map(fil, FilePayout.class);
        mapper.map(from, mappedEntity);
        mappedEntity.setDictionary(dictionaryRepository.findById(filter.dictionaryId).orElseThrow(() -> new ElementCleveradException("Dictionary", filter.dictionaryId)));
        mappedEntity.setPayout(payoutRepository.findById(filter.payoutId).orElseThrow(() -> new ElementCleveradException("Payout", filter.payoutId)));
        return FilePayoutDTO.from(repository.save(mappedEntity));
    }

    //  GET TIPI
    public Page<DictionaryDTO> getTypes() {
        return dictionaryBusiness.getFilePayoutTypes();
    }

    public ResponseEntity<Resource> downloadFile(Long id) {
        try {
            FilePayout fil = repository.findById(id).orElseThrow(() -> new ElementCleveradException("FilePayout", id));
            byte[] data = fileStoreService.retrieveFile(fil.getPath());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fil.getType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fil.getName() + "\"")
                    .body(new ByteArrayResource(data));
        } catch (IOException e) {
            throw new PostgresCleveradException("Errore downlaod", e);
        }
    }

    /**
     * ============================================================================================================
     **/
    private Specification<FilePayout> getSpecification(FilePayoutBusiness.Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getName() != null) {
                predicates.add(cb.equal(root.get("name"), request.getName()));
            }
            if (request.getPayoutId() != null) {
                predicates.add(cb.equal(root.get("payout").get("id"), request.getPayoutId()));
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
        private byte[] data;
        private Long payoutId;
        private Long dictionaryId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String name;
        private byte[] data;
        private Long payoutId;
        private Long dictionaryId;
        private Instant creationDateFrom;
        private Instant creationDateTo;
        private String path;
    }

}