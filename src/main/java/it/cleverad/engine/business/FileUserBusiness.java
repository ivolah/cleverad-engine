package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.FileUser;
import it.cleverad.engine.persistence.model.service.User;
import it.cleverad.engine.persistence.repository.service.FileUserRepository;
import it.cleverad.engine.persistence.repository.service.UserRepository;
import it.cleverad.engine.service.FileStoreService;
import it.cleverad.engine.web.dto.FileUserDTO;
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
public class FileUserBusiness {

    @Autowired
    private FileUserRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private Mapper mapper;
    @Autowired
    private FileStoreService fileStoreService;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public Long storeFile(MultipartFile file, BaseCreateRequest request) {
        try {
            log.info("Req {}", request);
            User user = userRepository.findById(request.userId).orElseThrow(() -> new ElementCleveradException("User", request.userId));
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            byte[] bytes = file.getBytes();
            Long affiliateID = user.getAffiliate().getId();
            String path = fileStoreService.storeFile(affiliateID, "user", UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(filename), bytes);
            FileUser fileDB = new FileUser(filename, file.getContentType(), user, request.avatar, path);
            return repository.save(fileDB).getId();
        } catch (Exception e) {
            throw new PostgresCleveradException("Errore uplaod: " + file.getOriginalFilename() + "!", e);
        }
    }

    // GET BY ID
    public FileUserDTO findById(Long id) {
        FileUser file = repository.findById(id).orElseThrow(() -> new ElementCleveradException("File User ", id));
        return FileUserDTO.from(file);
    }

    public FileUserDTO findByIdUser(Long id) {
        FileUser file = repository.findByUserId(id);
        return FileUserDTO.from(file);
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
    public Page<FileUserDTO> search(FileUserBusiness.Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        request.setAvatar(false);
        Page<FileUser> page = repository.findAll(getSpecification(request), pageable);
        return page.map(FileUserDTO::from);
    }

    // UPDATE
    public FileUserDTO update(Long id, FileUserBusiness.Filter filter) {
        FileUser fil = repository.findById(id).orElseThrow(() -> new ElementCleveradException("File", id));
        FileUserDTO from = FileUserDTO.from(fil);
        mapper.map(filter, from);
        FileUser mappedEntity = mapper.map(fil, FileUser.class);
        mapper.map(from, mappedEntity);
        mappedEntity.setUser(userRepository.findById(filter.userId).orElseThrow(() -> new ElementCleveradException("User", filter.userId)));
        return FileUserDTO.from(repository.save(mappedEntity));
    }

    public ResponseEntity<Resource> downloadFile(Long id) throws IOException {
        FileUser fil = repository.findById(id).orElseThrow(() -> new ElementCleveradException("FileUser", id));
        byte[] data = fileStoreService.retrieveFile(fil.getPath());
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(fil.getType())).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fil.getName() + "\"").body(new ByteArrayResource(data));
    }

    public Long storeAvatarFile(MultipartFile file, BaseCreateRequest request) throws IOException {
        // cancello tutti gli avatar
        Filter rr = new Filter();
        rr.setAvatar(true);
        rr.setUserId(jwtUserDetailsService.getUserID());
        Page<FileUser> page = repository.findAll(getSpecification(rr), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id"))));
        page.stream().forEach(fileUser -> repository.delete(fileUser));

        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        // salvo avatar
        request.setAvatar(true);
        request.setUserId(jwtUserDetailsService.getUserID());
        User user = userRepository.findById(jwtUserDetailsService.getUserID()).orElseThrow(() -> new ElementCleveradException("User", jwtUserDetailsService.getUserID()));
        String path = fileStoreService.storeFile(user.getAffiliate().getId(), "user", UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(filename), file.getBytes());
        FileUser fileDB = new FileUser(filename, file.getContentType(), user, request.avatar, path);
        return repository.save(fileDB).getId();
    }

    public FileUserDTO getAvatarFile() throws IOException {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setAvatar(true);
        request.setUserId(jwtUserDetailsService.getUserID());
        FileUser fu = repository.findAll(getSpecification(request), pageable).stream().findFirst().orElse(null);

        if (fu != null) {
            FileUserDTO f = FileUserDTO.from(fu);
            f.setData(fileStoreService.retrieveFile(fu.getPath()));
            return f;
        } else return null;
    }

    /**
     * ============================================================================================================
     **/
    private Specification<FileUser> getSpecification(FileUserBusiness.Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getName() != null) {
                predicates.add(cb.equal(root.get("name"), request.getName()));
            }
            if (request.getAvatar() != null) {
                predicates.add(cb.equal(root.get("avatar"), request.getAvatar()));
            }
            if (request.getUserId() != null) {
                predicates.add(cb.equal(root.get("user").get("id"), request.getUserId()));
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
        private Long userId;
        private Long dictionaryId;
        private Boolean avatar;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String name;
        private Long userId;
        private Long dictionaryId;
        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Boolean avatar;
        private String path;
    }

}