package it.cleverad.engine.business;

import it.cleverad.engine.persistence.model.service.*;
import it.cleverad.engine.persistence.repository.service.CampaignBudgetRepository;
import it.cleverad.engine.persistence.repository.service.CampaignCostRepository;
import it.cleverad.engine.persistence.repository.service.FileCostRepository;
import it.cleverad.engine.service.FileStoreService;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Slf4j
@Component
@Transactional
public class FileCostBusiness {

    @Autowired
    private FileCostRepository fileCostRepository;
    @Autowired
    private CampaignCostRepository campaignCostRepository;
    @Autowired
    private FileStoreService fileStoreService;

    /**
     * ============================================================================================================
     **/

    // CREATE ORDER
    public Long storeFile(MultipartFile file, BaseCreateRequest request) {
        try {
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            String path = fileStoreService.storeFile(Long.valueOf(request.getCampaignCostId()), "campaignCost", UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(filename), file.getBytes());
            CampaignCost campaignCost = campaignCostRepository.findById(Long.valueOf(request.getCampaignCostId())).orElseThrow(() -> new ElementCleveradException("CampaignCost", request.getCampaignCostId()));
            FileCost fileDB = new FileCost(filename, file.getContentType(), path, campaignCost);
            return fileCostRepository.save(fileDB).getId();
        } catch (Exception e) {
            throw new PostgresCleveradException("Errore uplaod: " + file.getOriginalFilename() + "!", e);
        }
    }

    public ResponseEntity<Resource> downloadFile(Long id) {
        try {
            FileCost fil = fileCostRepository.findById(id).orElseThrow(() -> new ElementCleveradException("FileCost", id));
            byte[] data = fileStoreService.retrieveFile(fil.getPath());
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(fil.getType())).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fil.getName() + "\"").body(new ByteArrayResource(data));
        } catch (IOException e) {
            throw new PostgresCleveradException("Errore downlaod", e);
        }
    }

    // DELETE BY ID AND T§yPE
    public void delete(Long id) {
        try {
            fileCostRepository.deleteById(id);
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
    @ToString
    public static class BaseCreateRequest {
        private String campaignCostId;
    }



}