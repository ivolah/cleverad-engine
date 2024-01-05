package it.cleverad.engine.business;

import it.cleverad.engine.persistence.model.service.CampaignBudget;
import it.cleverad.engine.persistence.model.service.FileCampaignBudgetInvoice;
import it.cleverad.engine.persistence.model.service.FileCampaignBudgetOrder;
import it.cleverad.engine.persistence.model.service.FilePayout;
import it.cleverad.engine.persistence.repository.service.CampaignBudgetRepository;
import it.cleverad.engine.persistence.repository.service.FileCampaignBudgetInvoiceRepository;
import it.cleverad.engine.persistence.repository.service.FileCampaignBudgetOrderRepository;
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
public class FileCampaignBudgetBusiness {

    @Autowired
    private FileCampaignBudgetOrderRepository fileCampaignBudgetOrderRepository;
    @Autowired
    private FileCampaignBudgetInvoiceRepository fileCampaignBudgetInvoiceRepository;
    @Autowired
    private CampaignBudgetRepository campaignBudgetRepository;
    @Autowired
    private FileStoreService fileStoreService;

    /**
     * ============================================================================================================
     **/

    // CREATE ORDER
    public Long storeFile(MultipartFile file, BaseCreateRequest request, String type) {
        log.info(">>  ::  " + request);
        try {
            if (type.equals("INVOICE")) {
                log.info(">> INV ::  " + request);
                String filename = StringUtils.cleanPath(file.getOriginalFilename());
                String path = fileStoreService.storeFile(Long.valueOf(request.getCampaignId()), "campaignBudgetInvoice", UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(filename), file.getBytes());
                CampaignBudget campaignBudget = campaignBudgetRepository.findById(Long.valueOf(request.getCampaignBudgetId())).orElseThrow(() -> new ElementCleveradException("CampaignBudget", request.getCampaignBudgetId()));
              log.info("1");
                FileCampaignBudgetInvoice fileDB = new FileCampaignBudgetInvoice(filename, file.getContentType(), path, campaignBudget);
                log.info("2");
                return fileCampaignBudgetInvoiceRepository.save(fileDB).getId();
            } else if (type.equals("ORDER")) {
                String filename = StringUtils.cleanPath(file.getOriginalFilename());
                String path = fileStoreService.storeFile(Long.valueOf(request.getCampaignId()), "campaignBudgetOrder", UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(filename), file.getBytes());
                CampaignBudget campaignBudget = campaignBudgetRepository.findById(Long.valueOf(request.getCampaignBudgetId())).orElseThrow(() -> new ElementCleveradException("CampaignBudget", request.getCampaignBudgetId()));
                FileCampaignBudgetOrder fileDB = new FileCampaignBudgetOrder(filename, file.getContentType(), path, campaignBudget);
                return fileCampaignBudgetOrderRepository.save(fileDB).getId();
            } else return null;
        } catch (Exception e) {
            throw new PostgresCleveradException("Errore uplaod: " + file.getOriginalFilename() + "!", e);
        }
    }

    public ResponseEntity<Resource> downloadFile(Long id, String type) {
        try {
            if (type.equals("INVOICE")) {
                FileCampaignBudgetInvoice fil = fileCampaignBudgetInvoiceRepository.findById(id).orElseThrow(() -> new ElementCleveradException("FileCampaignBudgetInvoice", id));
                byte[] data = fileStoreService.retrieveFile(fil.getPath());
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(fil.getType())).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fil.getName() + "\"").body(new ByteArrayResource(data));
            } else if (type.equals("ORDER")) {
                FileCampaignBudgetOrder fil = fileCampaignBudgetOrderRepository.findById(id).orElseThrow(() -> new ElementCleveradException("FileCampaignBudgetOrder", id));
                byte[] data = fileStoreService.retrieveFile(fil.getPath());
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(fil.getType())).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fil.getName() + "\"").body(new ByteArrayResource(data));
            } else return null;
        } catch (IOException e) {
            throw new PostgresCleveradException("Errore downlaod", e);
        }
    }

    // DELETE BY ID AND T§yPE
    public void delete(Long id, String type) {
        try {
            if (type.equals("INVOICE")) {
                fileCampaignBudgetInvoiceRepository.deleteById(id);
            } else if (type.equals("ORDER")) {
                fileCampaignBudgetOrderRepository.deleteById(id);
            }
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // UPDATE
    public void updateInterno(Long id, String type, Long idCampaignBudget) {
        if (type.equals("INVOICE")) {
            FileCampaignBudgetInvoice fil = fileCampaignBudgetInvoiceRepository.findById(id).orElseThrow(() -> new ElementCleveradException("FileCampaignBudgetInvoice", id));
            fil.setCampaignBudget(campaignBudgetRepository.findById(idCampaignBudget).orElseThrow(() -> new ElementCleveradException("CampaignBudget", id)));
            fileCampaignBudgetInvoiceRepository.save(fil);
        } else if (type.equals("ORDER")) {
            FileCampaignBudgetOrder fil = fileCampaignBudgetOrderRepository.findById(id).orElseThrow(() -> new ElementCleveradException("FileCampaignBudgetOrder", id));
            fil.setCampaignBudget(campaignBudgetRepository.findById(idCampaignBudget).orElseThrow(() -> new ElementCleveradException("CampaignBudget", id)));
            fileCampaignBudgetOrderRepository.save(fil);
        }
    }

    // SEARCH PAGINATED
//    public Page<FilePayoutDTO> search(FileCampaignBudgetBusiness.Filter request, Pageable pageableRequest) {
//        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
//        Page<FilePayout> page = repository.findAll(getSpecification(request), pageable);
//        return page.map(FilePayoutDTO::from);
//    }

    /**
     * ============================================================================================================
     **/
    private Specification<FilePayout> getSpecification(FileCampaignBudgetBusiness.Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getName() != null) {
                predicates.add(cb.equal(root.get("name"), request.getName()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaign").get("id"), request.getCampaignId()));
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
        private String campaignId;
        private String campaignBudgetId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String name;
        private Long campaignId;
        private String path;
        private Instant creationDateFrom;
        private Instant creationDateTo;
    }

}