package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.Advertiser;
import it.cleverad.engine.persistence.model.service.Feed;
import it.cleverad.engine.persistence.model.service.FileFeed;
import it.cleverad.engine.persistence.repository.service.AdvertiserRepository;
import it.cleverad.engine.persistence.repository.service.FeedRepository;
import it.cleverad.engine.persistence.repository.service.FileFeedRepository;
import it.cleverad.engine.service.FileStoreService;
import it.cleverad.engine.web.dto.FileFeedDTO;
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
public class FileFeedBusiness {

    @Autowired
    private FileFeedRepository repository;
    @Autowired
    private AdvertiserRepository advertiserRepository;
    @Autowired
    private FeedRepository feedRepository;
    @Autowired
    private Mapper mapper;
    @Autowired
    private FileStoreService fileStoreService;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    /**
     * ============================================================================================================
     **/

    public Long storeFile(MultipartFile file, BaseCreateRequest request) {
        try {
            if (jwtUserDetailsService.isAdvertiser())
                request.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
            Advertiser advertiser = advertiserRepository.findById(request.advertiserId).orElseThrow(() -> new ElementCleveradException("Advertiser", request.advertiserId));
            Feed feed = feedRepository.findById(request.feedId).orElseThrow(() -> new ElementCleveradException("feedRepository", request.feedId));
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            String path = fileStoreService.storeFileNew(advertiser.getId(), "feed", UUID.randomUUID() + "." + FilenameUtils.getExtension(filename), file.getBytes());
            FileFeed fileDB = new FileFeed(filename, file.getContentType(), request.note, path, LocalDateTime.now(), advertiser);
            fileDB.setAdvertiser(advertiser);
            fileDB.setFeed(feed);
            return repository.save(fileDB).getId();
        } catch (Exception e) {
            throw new PostgresCleveradException("Errore uplaod: " + file.getOriginalFilename() + "!");
        }
    }

    // GET BY ID
    public FileFeedDTO findById(Long id) {
        FileFeed file = repository.findById(id).orElseThrow(() -> new ElementCleveradException("FileFeed", id));
        return FileFeedDTO.from(file);
    }

    // DELETE BY ID
    public void deleteByFeedID(Long id) {
        try {
            Filter request = new Filter();
            request.setFeedId(id);
            repository.findAll(getSpecification(request), PageRequest.of(0, Integer.MAX_VALUE)).stream().forEach(fileFeed -> {
                fileStoreService.deleteFile(this.findById(fileFeed.getId()).getPath());
                repository.deleteById(fileFeed.getId());
            });
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
    public Page<FileFeedDTO> search(FileFeedBusiness.Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<FileFeed> page = repository.findAll(getSpecification(request), pageable);
        return page.map(FileFeedDTO::from);
    }

    // UPDATE
    public FileFeedDTO update(Long id, FileFeedBusiness.Filter filter) {
        if (jwtUserDetailsService.isAdvertiser())
            filter.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
        FileFeed fil = repository.findById(id).orElseThrow(() -> new ElementCleveradException("File", id));
        mapper.map(filter, fil);
        fil.setAdvertiser(advertiserRepository.findById(filter.advertiserId).orElseThrow(() -> new ElementCleveradException("Advertiser", filter.advertiserId)));
        fil.setFeed(feedRepository.findById(filter.feedId).orElseThrow(() -> new ElementCleveradException("feed", filter.feedId)));
        return FileFeedDTO.from(repository.save(fil));
    }

    public ResponseEntity<Resource> downloadFile(Long id) {
        try {
            FileFeed fil = repository.findById(id).orElseThrow(() -> new ElementCleveradException("FileFeed", id));
            byte[] data = fileStoreService.retrieveFile(fil.getPath());
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(fil.getType())).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fil.getName() + "\"").body(new ByteArrayResource(data));
        } catch (Exception e) {
            throw new PostgresCleveradException("Errore download ", e);
        }
    }

    /**
     * ============================================================================================================
     **/
    private Specification<FileFeed> getSpecification(FileFeedBusiness.Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getName() != null) {
                predicates.add(cb.equal(root.get("name"), request.getName()));
            }
            if (request.getAdvertiserId() != null) {
                predicates.add(cb.equal(root.get("advertiser").get("id"), request.getAdvertiserId()));
            }
            if (request.getFeedId() != null) {
                predicates.add(cb.equal(root.get("feed").get("id"), request.getFeedId()));
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
        private Long advertiserId;
        private Long feedId;
        private String docType;
        private String note;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String name;
        private Long advertiserId;
        private Long feedId;
        private Instant creationDateFrom;
        private Instant creationDateTo;
        private String note;
    }

}