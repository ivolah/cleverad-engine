package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.File;
import it.cleverad.engine.persistence.model.service.Media;
import it.cleverad.engine.persistence.repository.service.FileRepository;
import it.cleverad.engine.service.RefferalService;
import it.cleverad.engine.web.dto.FileDTO;
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
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class FileBusiness {

    @Autowired
    private FileRepository repository;

    @Autowired
    private Mapper mapper;

    @Autowired
    private RefferalService refferalService;

    @Autowired
    private MediaBusiness mediaBusiness;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public Long store(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        File fileDB = new File(fileName, file.getContentType(), file.getBytes());
        return repository.save(fileDB).getId();
    }

    // GET BY ID
    public FileDTO findById(Long id) {
        File file = repository.findById(id).orElseThrow(() -> new ElementCleveradException("File", id));
        return FileDTO.from(file);
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
    public Page<FileDTO> search(FileBusiness.Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<File> page = repository.findAll(getSpecification(request), pageable);
        return page.map(FileDTO::from);
    }

    // UPDATE
    public FileDTO update(Long id, FileBusiness.Filter filter) {
        File fil = repository.findById(id).orElseThrow(() -> new ElementCleveradException("File", id));
        FileDTO from = FileDTO.from(fil);

        mapper.map(filter, from);

        File mappedEntity = mapper.map(fil, File.class);
        mapper.map(from, mappedEntity);

        return FileDTO.from(repository.save(mappedEntity));
    }

    public List<FileDTO> listaFileCodificati() {
        Filter request = new Filter();
        Page<File> page = repository.findAll(getSpecification(request), PageRequest.of(0, 10, Sort.by(Sort.Order.desc("creationDate"))));

        List<FileDTO> fileDTOList = page.stream().distinct().map(ele -> {
            // trovo media id collegato e campaign id a cui e colelgato il media
            Media mm = mediaBusiness.getByFileId(ele.getId());
            if (mm != null) {
                Long mediaId = mm.getId();
                // trovo campaignID
                Long campaignId = mediaBusiness.findById(mediaId).getCampaignId();
                String stringa = refferalService.encode(String.valueOf(campaignId)) + "-" + refferalService.encode(String.valueOf(mediaId));
                FileDTO dto = FileDTO.from(ele);
                dto.setNomeCodificato(stringa);
                return dto;
            }else{
                return null;
            }
        }).collect(Collectors.toList());

        return fileDTOList.stream().distinct().collect(Collectors.toList());
    }

    /**
     * ============================================================================================================
     **/
    private Specification<File> getSpecification(FileBusiness.Filter request) {
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
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String name;
        private String type;
        private byte[] data;
        private Instant creationDateFrom;
        private Instant creationDateTo;
    }

}
