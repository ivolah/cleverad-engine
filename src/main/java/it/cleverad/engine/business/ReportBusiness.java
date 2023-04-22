package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Report;
import it.cleverad.engine.persistence.model.service.StatClickCpc;
import it.cleverad.engine.persistence.model.service.TopAffiliates;
import it.cleverad.engine.persistence.model.service.TopCampaings;
import it.cleverad.engine.persistence.repository.service.ReportRepository;
import it.cleverad.engine.persistence.repository.service.TransactionAllRepository;
import it.cleverad.engine.web.dto.ReportDTO;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class ReportBusiness {

    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private TransactionAllRepository transactionAllRepository;
    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public ReportDTO create(BaseCreateRequest request) {
        Report map = mapper.map(request, Report.class);
        return ReportDTO.from(reportRepository.save(map));
    }

    // GET BY ID
    public ReportDTO findById(Long id) {
        Report channel = reportRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Report", id));
        return ReportDTO.from(channel);
    }

    // DELETE BY ID
    public void delete(Long id) {
        try {
            reportRepository.deleteById(id);
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // SEARCH PAGINATED
    public Page<ReportDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<Report> page = reportRepository.findAll(getSpecification(request), pageable);
        return page.map(ReportDTO::from);
    }


    private Specification<Report> getSpecification(Filter request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getName() != null) {
                predicates.add(cb.equal(root.get("name"), request.getName()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String name;
        private Long reportTypeId;
    }

    /**
     * ============================================================================================================
     **/

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BaseCreateRequest {
        private Long id;
        private String name;
        private String description;
        private Long reportTypeId;
    }

    /**
     * ============================================================================================================
     **/

    public Page<TopCampaings> searchTopCampaigns(TopFilter request, Pageable pageableRequest) {
        return transactionAllRepository.findGroupByCampaignId(pageableRequest, request.getDateTimeFrom().atStartOfDay(), request.getDateTimeTo().atStartOfDay(), request.getDictionaryIds());
    }

    public Page<TopAffiliates> searchTopAffilaites(TopFilter request, Pageable pageableRequest) {
        return transactionAllRepository.findAffiliatesGroupByCampaignId(pageableRequest, request.getDateTimeFrom().atStartOfDay(), request.getDateTimeTo().atStartOfDay(), request.getDictionaryIds());
    }

//    public Page<StatClickCpc> getGroupedCpcClicks(TopFilter request, Pageable pageableRequest) {
//        return transactionAllRepository.getGroupedCpcClicks(pageableRequest, request.getDateTimeFrom().atStartOfDay(), request.getDateTimeTo().atStartOfDay());
//    }

    /**
     * ============================================================================================================
     **/


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopFilter {
        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateTimeFrom;
        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateTimeTo;
        @NotNull
        private List<Long> dictionaryIds;
    }

}
