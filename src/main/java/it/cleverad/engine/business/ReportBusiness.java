package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Report;
import it.cleverad.engine.persistence.model.service.ReportDaily;
import it.cleverad.engine.persistence.model.service.ReportTopAffiliates;
import it.cleverad.engine.persistence.model.service.ReportTopCampaings;
import it.cleverad.engine.persistence.repository.service.ReportRepository;
import it.cleverad.engine.service.JwtUserDetailsService;
import it.cleverad.engine.web.dto.ReportDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class ReportBusiness {

    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private Mapper mapper;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

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
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
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

    public Page<ReportTopCampaings> searchTopCampaigns(TopFilter request, Pageable pageableRequest) {
        List<ReportTopCampaings> listaCampaigns = new ArrayList<>();
        if (!jwtUserDetailsService.isAdmin()) {
            listaCampaigns = reportRepository.searchTopCampaigns(request.getDateTimeFrom(), request.getDateTimeTo().plusDays(1), jwtUserDetailsService.getAffiliateID(), request.getCampaignid());
        } else {
            listaCampaigns = reportRepository.searchTopCampaigns(request.getDateTimeFrom(), request.getDateTimeTo().plusDays(1), request.getAffiliateid(), request.getCampaignid());
        }
        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), listaCampaigns.size());
        Page<ReportTopCampaings> pages =
                new PageImpl<>(
                        listaCampaigns.stream()
                                .distinct()
                                .collect(Collectors.toList())
                                .subList((int) pageableRequest.getOffset(), end), pageableRequest, listaCampaigns.size()
                );
        return pages;
    }

    /**
     * ============================================================================================================
     **/

    public Page<ReportTopCampaings> searchTopCampaignsChannel(TopFilter request, Pageable pageableRequest) {
        List<ReportTopCampaings> listaCampaigns = new ArrayList<>();
        if (!jwtUserDetailsService.isAdmin()) {
            listaCampaigns = reportRepository.searchTopCampaignsChannel(request.getDateTimeFrom(), request.getDateTimeTo().plusDays(1), jwtUserDetailsService.getAffiliateID(), request.getCampaignid());
        } else {
            listaCampaigns = reportRepository.searchTopCampaignsChannel(request.getDateTimeFrom(), request.getDateTimeTo().plusDays(1),request.getAffiliateid(), request.getCampaignid());
        }
        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), listaCampaigns.size());
        Page<ReportTopCampaings> pages =
                new PageImpl<>(
                        listaCampaigns.stream()
                                .distinct()
                                .collect(Collectors.toList())
                                .subList((int) pageableRequest.getOffset(), end), pageableRequest, listaCampaigns.size()
                );
        return pages;
    }

    /**
     * ============================================================================================================
     **/

    public Page<ReportTopAffiliates> searchTopAffilaites(TopFilter request, Pageable pageableRequest) {
        List<ReportTopAffiliates> listaAffiliates = new ArrayList<>();
        if (!jwtUserDetailsService.isAdmin()) {
            listaAffiliates = reportRepository.searchTopAffilaites(request.getDateTimeFrom(), request.getDateTimeTo(),  jwtUserDetailsService.getAffiliateID(), request.getCampaignid());
        } else {
            listaAffiliates = reportRepository.searchTopAffilaites(request.getDateTimeFrom(), request.getDateTimeTo(), request.getAffiliateid(), request.getCampaignid());
        }
        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), listaAffiliates.size());
        Page<ReportTopAffiliates> pages = new PageImpl<>(listaAffiliates.stream().distinct().collect(Collectors.toList()).subList((int) pageableRequest.getOffset(), end), pageableRequest, listaAffiliates.size());
        return pages;
    }

    /**
     * ============================================================================================================
     **/

    public Page<ReportDaily> searchDaily(TopFilter request, Pageable pageableRequest) {
        List<ReportDaily> lista = new ArrayList<>();
        if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin()))
            request.setAffiliateid(jwtUserDetailsService.getAffiliateID());

        lista = reportRepository.searchDaily(request.getDateTimeFrom(), request.getDateTimeTo().plusDays(1), request.getAffiliateid(), request.getCampaignid());

        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), lista.size());
        Page<ReportDaily> pages = new PageImpl<>(lista.stream().distinct().collect(Collectors.toList()).subList((int) pageableRequest.getOffset(), end), pageableRequest, lista.size());
        return pages;
    }

    /**
     * ============================================================================================================
     **/

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class TopFilter {
        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateTimeFrom;
        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateTimeTo;
        @NotNull
        private List<Long> dictionaryIds;
        private Long affiliateid;
        private Long campaignid;
    }

}
