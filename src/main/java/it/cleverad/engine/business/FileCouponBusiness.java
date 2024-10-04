package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.Advertiser;
import it.cleverad.engine.persistence.model.service.FileCoupon;
import it.cleverad.engine.persistence.repository.service.AdvertiserRepository;
import it.cleverad.engine.persistence.repository.service.CouponRepository;
import it.cleverad.engine.persistence.repository.service.FileCouponRepository;
import it.cleverad.engine.service.FileStoreService;
import it.cleverad.engine.web.dto.FileCouponDTO;
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

import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@Transactional
public class FileCouponBusiness {

    @Autowired
    private FileCouponRepository repository;
    @Autowired
    private AdvertiserRepository advertiserRepository;
    @Autowired
    private CouponRepository couponRepository;
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

            String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String path = fileStoreService.storeFileNew(advertiser.getId(), "coupon", UUID.randomUUID() + "." + FilenameUtils.getExtension(filename), file.getBytes());

            FileCoupon fileDB = new FileCoupon(filename, file.getContentType(), request.note, path, LocalDateTime.now(), advertiser);
            fileDB.setAdvertiser(advertiser);

            fileDB.setCoupon(null);
            return repository.save(fileDB).getId();
        } catch (Exception e) {
            throw new PostgresCleveradException("Errore uplaod: " + file.getOriginalFilename() + "!");
        }
    }

    // GET BY ID
    public FileCouponDTO findById(Long id) {
        FileCoupon file = repository.findById(id).orElseThrow(() -> new ElementCleveradException("FileCoupon", id));
        return FileCouponDTO.from(file);
    }

    // DELETE BY ID
    public void deleteByCouponID(Long id) {
        try {
            Filter request = new Filter();
            request.setCouponId(id);
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
    public Page<FileCouponDTO> search(FileCouponBusiness.Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<FileCoupon> page = repository.findAll(getSpecification(request), pageable);
        return page.map(FileCouponDTO::from);
    }

    // UPDATE
    public FileCouponDTO update(Long id, FileCouponBusiness.Filter filter) {
        if (jwtUserDetailsService.isAdvertiser())
            filter.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
        FileCoupon fil = repository.findById(id).orElseThrow(() -> new ElementCleveradException("File", id));
        mapper.map(filter, fil);
        fil.setAdvertiser(advertiserRepository.findById(filter.advertiserId).orElseThrow(() -> new ElementCleveradException("Advertiser", filter.advertiserId)));
        fil.setCoupon(couponRepository.findById(filter.couponId).orElseThrow(() -> new ElementCleveradException("cupon", filter.couponId)));
        return FileCouponDTO.from(repository.save(fil));
    }

    public FileCouponDTO updateFile(Long id, Long coupionId) {
        FileCoupon fil = repository.findById(id).orElseThrow(() -> new ElementCleveradException("File", id));
        fil.setCoupon(couponRepository.findById(coupionId).get());
        return FileCouponDTO.from(repository.save(fil));
    }

    public ResponseEntity<Resource> downloadFile(Long id) {
        try {
            FileCoupon fil = repository.findById(id).orElseThrow(() -> new ElementCleveradException("FileCoupon", id));
            byte[] data = fileStoreService.retrieveFile(fil.getPath());
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(fil.getType())).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fil.getName() + "\"").body(new ByteArrayResource(data));
        } catch (Exception e) {
            throw new PostgresCleveradException("Errore download ", e);
        }
    }

    /**
     * ============================================================================================================
     **/
    private Specification<FileCoupon> getSpecification(FileCouponBusiness.Filter request) {
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
            if (request.getCouponId() != null) {
                predicates.add(cb.equal(root.get("coupon").get("id"), request.getCouponId()));
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
        private Long couponId;
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
        private Long couponId;
        private Instant creationDateFrom;
        private Instant creationDateTo;
        private String note;
    }

}