package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Operation;
import it.cleverad.engine.persistence.repository.service.OperationRepository;
import it.cleverad.engine.web.dto.OperationDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@Transactional
public class OperationBusiness {

    @Autowired
    private OperationRepository repository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public OperationDTO create(BaseCreateRequest request) {
        Operation map = mapper.map(request, Operation.class);
        map.setCreationDate(LocalDateTime.now());
        request.setMethod(request.getMethod());
        request.setData(request.getData());
        request.setUrl(request.getUrl());
        request.setUsername(request.getUsername());
        return OperationDTO.from(repository.save(map));
    }

    // GET BY ID
    public OperationDTO findById(Long id) {
        Operation op = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Operations", id));
        return OperationDTO.from(op);
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


    /**
     * ============================================================================================================
     **/

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BaseCreateRequest {
        private LocalDateTime creationDate = LocalDateTime.now();
        private String method;
        private String url;
        private String username;
        private String data;
    }


}