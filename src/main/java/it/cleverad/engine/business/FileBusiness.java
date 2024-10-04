package it.cleverad.engine.business;

import it.cleverad.engine.persistence.model.service.File;
import it.cleverad.engine.persistence.model.service.Media;
import it.cleverad.engine.persistence.repository.service.FileRepository;
import it.cleverad.engine.service.FileStoreService;
import it.cleverad.engine.service.ReferralService;
import it.cleverad.engine.web.dto.FileDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
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

import jakarta.persistence.criteria.Predicate;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class FileBusiness {

    @Autowired
    private FileRepository repository;
    @Autowired
    private ReferralService referralService;
    @Autowired
    private MediaBusiness mediaBusiness;
    @Autowired
    private FileStoreService fileStoreService;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public Long storeFile(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String path = fileStoreService.storeFile(1L, "misc", UUID.randomUUID() + "." + FilenameUtils.getExtension(fileName), file.getBytes());
        File fileDB = new File(fileName, file.getContentType(), path);
        return repository.save(fileDB).getId();
    }

    // GET BY ID
    public FileDTO findById(Long id) throws IOException {
        File file = repository.findById(id).orElseThrow(() -> new ElementCleveradException("File", id));
        FileDTO dto = FileDTO.from(file);
        dto.setData(fileStoreService.retrieveFile(file.getPath()));
        return dto;
    }

    // DELETE BY ID
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
    public Page<FileDTO> search(FileBusiness.Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<File> page = repository.findAll(getSpecification(request), pageable);
        return page.map(FileDTO::from);
    }

    public List<FileDTO> listaFileCodificati() {
        Filter request = new Filter();
        Page<File> page = repository.findAll(getSpecification(request), PageRequest.of(0, 10, Sort.by(Sort.Order.desc("creationDate"))));
        List<FileDTO> fileDTOList = page.stream().distinct().map(ele -> {
            byte[] data = new byte[0];
            try {
                data = fileStoreService.retrieveFile(ele.getPath());
            } catch (IOException e) {
                log.trace("Eccezione in cariamento file {}", ele.getPath());
            }
            // trovo media id collegato e campaign id a cui e colelgato il media
            Media mm = mediaBusiness.getByFileId(ele.getId());
            if (mm != null) {
                Long mediaId = mm.getId();
                // trovo campaignID
                Long campaignId = 0L;
                if (mediaBusiness.findById(mediaId) != null)
                    campaignId = mediaBusiness.findById(mediaId).getCampaignId();
                String stringa = referralService.encode(String.valueOf(campaignId)) + "-" + referralService.encode(String.valueOf(mediaId));
                FileDTO dto = FileDTO.from(ele);
                dto.setNomeCodificato(stringa);
                dto.setData(data);
                return dto;
            } else {
                return null;
            }
        }).collect(Collectors.toList());

        return fileDTOList.stream().distinct().collect(Collectors.toList());
    }

    public ResponseEntity<Resource> downloadFile(Long id) throws IOException {
        File fil = repository.findById(id).orElseThrow(() -> new ElementCleveradException("File", id));
        byte[] data = fileStoreService.retrieveFile(fil.getPath());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fil.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fil.getName() + "\"")
                .body(new ByteArrayResource(data));
    }

    /**
     * ============================================================================================================
     **/
    private Specification<File> getSpecification(FileBusiness.Filter request) {
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
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String name;
        private String type;
        private Instant creationDateFrom;
        private Instant creationDateTo;
    }

}