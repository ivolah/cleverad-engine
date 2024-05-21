package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Advertiser;
import it.cleverad.engine.persistence.model.service.Dictionary;
import it.cleverad.engine.persistence.model.service.FileAdvertiser;
import it.cleverad.engine.persistence.repository.service.AdvertiserRepository;
import it.cleverad.engine.persistence.repository.service.DictionaryRepository;
import it.cleverad.engine.persistence.repository.service.FileAdvertiserRepository;
import it.cleverad.engine.service.FileStoreService;
import it.cleverad.engine.web.dto.DictionaryDTO;
import it.cleverad.engine.web.dto.FileAdvertiserDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
public class FileAdvertiserBusiness {

    @Autowired
    private FileAdvertiserRepository repository;
    @Autowired
    private AdvertiserRepository advertiserRepository;
    @Autowired
    private DictionaryRepository dictionaryRepository;
    @Autowired
    private DictionaryBusiness dictionaryBusiness;
    @Autowired
    private Mapper mapper;
    @Autowired
    private FileStoreService fileStoreService;

    /**
     * ============================================================================================================
     **/

    public Long storeFile(MultipartFile file, BaseCreateRequest request) {
        try {
            Advertiser advertiser = advertiserRepository.findById(request.advertiserId).orElseThrow(() -> new ElementCleveradException("Advertiser", request.advertiserId));
            Dictionary dictionary = (dictionaryRepository.findById(request.dictionaryId).orElseThrow(() -> new ElementCleveradException("Dictionary", request.dictionaryId)));
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            String path = fileStoreService.storeFileNew(advertiser.getId(), "advertiser", UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(filename), file.getBytes());
            FileAdvertiser fileDB = new FileAdvertiser(filename, file.getContentType(), request.note, path, LocalDateTime.now(), advertiser, dictionary);
            return repository.save(fileDB).getId();
        } catch (Exception e) {
            throw new PostgresCleveradException("Errore uplaod: " + file.getOriginalFilename() + "!");
        }
    }

    // GET BY ID
    public FileAdvertiserDTO findById(Long id) {
        FileAdvertiser file = repository.findById(id).orElseThrow(() -> new ElementCleveradException("FileAdvertiser", id));
        return FileAdvertiserDTO.from(file);
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
    public Page<FileAdvertiserDTO> search(FileAdvertiserBusiness.Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<FileAdvertiser> page = repository.findAll(getSpecification(request), pageable);
        return page.map(FileAdvertiserDTO::from);
    }

    // UPDATE
    public FileAdvertiserDTO update(Long id, FileAdvertiserBusiness.Filter filter) {
        FileAdvertiser fil = repository.findById(id).orElseThrow(() -> new ElementCleveradException("File", id));
        FileAdvertiserDTO from = FileAdvertiserDTO.from(fil);
        mapper.map(filter, from);
        FileAdvertiser mappedEntity = mapper.map(fil, FileAdvertiser.class);
        mapper.map(from, mappedEntity);
        mappedEntity.setDictionary(dictionaryRepository.findById(filter.dictionaryId).orElseThrow(() -> new ElementCleveradException("Dictionary", filter.dictionaryId)));
        mappedEntity.setAdvertiser(advertiserRepository.findById(filter.advertiserId).orElseThrow(() -> new ElementCleveradException("Advertiser", filter.advertiserId)));
        return FileAdvertiserDTO.from(repository.save(mappedEntity));
    }

    //  GET TIPI
    public Page<DictionaryDTO> getTypes() {
        return dictionaryBusiness.getTypeDocument();
    }

    public ResponseEntity<Resource> downloadFile(Long id) {
        try {
            FileAdvertiser fil = repository.findById(id).orElseThrow(() -> new ElementCleveradException("FileAdvertiser", id));
            byte[] data = fileStoreService.retrieveFile(fil.getPath());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fil.getType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fil.getName() + "\"")
                    .body(new ByteArrayResource(data));
        } catch (Exception e) {
            throw new PostgresCleveradException("Errore download ", e);
        }
    }

    /**
     * ============================================================================================================
     **/
    private Specification<FileAdvertiser> getSpecification(FileAdvertiserBusiness.Filter request) {
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

            if (request.getAdvertiserId() != null) {
                predicates.add(cb.equal(root.get("advertiser").get("id"), request.getAdvertiserId()));
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
    @ToString
    public static class BaseCreateRequest {
        private String name;
        private String type;
        private Long advertiserId;
        private Long dictionaryId;
        private String docType;
        private String note;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String name;
        private String type;
        private Long advertiserId;
        private Long dictionaryId;
        private Instant creationDateFrom;
        private Instant creationDateTo;
        private String note;
    }

}