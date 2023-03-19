package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.FileUser;
import it.cleverad.engine.persistence.model.service.User;
import it.cleverad.engine.persistence.repository.service.FileUserRepository;
import it.cleverad.engine.persistence.repository.service.UserRepository;
import it.cleverad.engine.web.dto.FileUserDTO;
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
public class FileUserBusiness {

    @Autowired
    private FileUserRepository repository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public Long store(MultipartFile file, BaseCreateRequest request) throws IOException {
        User user = userRepository.findById(request.userId).orElseThrow(() -> new ElementCleveradException("User", request.userId));
        FileUser fileDB = new FileUser(StringUtils.cleanPath(file.getOriginalFilename()), file.getContentType(), file.getBytes(), user, request.note, request.avatar);
        return repository.save(fileDB).getId();
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

    // SEARCH PAGINATED
    public Page<FileUserDTO> search(FileUserBusiness.Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
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
        mappedEntity.setUserFiles(userRepository.findById(filter.userId).orElseThrow(() -> new ElementCleveradException("User", filter.userId)));
        return FileUserDTO.from(repository.save(mappedEntity));
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
    public static class BaseCreateRequest {
        private String name;
        private byte[] data;
        private Long userId;
        private Long dictionaryId;
        private String note;
        private Boolean avatar;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String name;
        private byte[] data;
        private Long userId;
        private Long dictionaryId;
        private Instant creationDateFrom;
        private Instant creationDateTo;
        private String note;
        private Boolean avatar;
    }

}
