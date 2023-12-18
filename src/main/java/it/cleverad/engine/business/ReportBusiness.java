package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.Report;
import it.cleverad.engine.persistence.model.service.ReportDaily;
import it.cleverad.engine.persistence.model.service.ReportTopAffiliates;
import it.cleverad.engine.persistence.model.service.ReportTopCampaings;
import it.cleverad.engine.persistence.repository.service.ReportRepository;
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
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("name")));
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

    /**
     * ============================================================================================================
     **/

    public Page<ReportTopCampaings> searchTopCampaigns(TopFilter request, Pageable pageableRequest) {
        request = prepareRequest(request);
        List<ReportTopCampaings> listaCampaigns;
        if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin())) {
            listaCampaigns = reportRepository.searchTopCampaigns(request.getDateTimeFrom(), request.getDateTimeTo(),
                    jwtUserDetailsService.getAffiliateID(),
                    request.getCampaignId(), request.getDictionaryIds(), request.getStatusIds());
        } else {
            listaCampaigns = reportRepository.searchTopCampaigns(request.getDateTimeFrom(), request.getDateTimeTo(),
                    request.getAffiliateid(),
                    request.getCampaignId(), request.getDictionaryIds(), request.getStatusIds());
        }
        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), listaCampaigns.size());
        return new PageImpl<>(
                        listaCampaigns.stream()
                                .distinct()
                                .collect(Collectors.toList())
                                .subList((int) pageableRequest.getOffset(), end), pageableRequest, listaCampaigns.size()
                );
    }

    public Page<ReportTopCampaings> searchTopCampaignsSORT(TopFilter request, Pageable pageableRequest) {

        @NotNull List<Long> ll = new ArrayList<>();
        ll.add(39L);
        ll.add(42L);
        ll.add(41L);
        ll.add(68L);
        ll.add(49L);
        ll.add(48L);
        ll.add(47L);
        ll.add(40L);
        request.setDictionaryIds(ll);
        request.setAffiliateid(null);
        request.setCampaignId(null);

        List<ReportTopCampaings> listaCampaigns ;
        if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin())) {
            listaCampaigns = reportRepository.searchTopCampaignsImp(null, null, jwtUserDetailsService.getAffiliateID());
        } else {
            listaCampaigns = reportRepository.searchTopCampaignsImp(null, null, null);
        }
        Integer end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), listaCampaigns.size());
        Integer offset = Math.toIntExact(pageableRequest.getOffset());
        if (offset > end)
            offset = end;
        return new PageImpl<>(
                        listaCampaigns.stream()
                                .distinct()
                                .collect(Collectors.toList())
                                .subList(offset, end), pageableRequest, listaCampaigns.size()
                );
    }

    /**
     * ============================================================================================================
     **/

    public Page<ReportTopCampaings> searchTopCampaignsChannel(TopFilter request, Pageable pageableRequest) {
        request = prepareRequest(request);
        List<ReportTopCampaings> listaCampaigns ;
        if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin())) {
            listaCampaigns = reportRepository.searchTopCampaignsChannel(request.getDateTimeFrom(), request.getDateTimeTo().plusDays(1), jwtUserDetailsService.getAffiliateID(), request.getCampaignId(), request.getDictionaryIds(), request.getStatusIds());
        } else {
            listaCampaigns = reportRepository.searchTopCampaignsChannel(request.getDateTimeFrom(), request.getDateTimeTo().plusDays(1), request.getAffiliateid(), request.getCampaignId(), request.getDictionaryIds(), request.getStatusIds());
        }
        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), listaCampaigns.size());
        return new PageImpl<>(
                        listaCampaigns.stream()
                                .distinct()
                                .collect(Collectors.toList())
                                .subList((int) pageableRequest.getOffset(), end), pageableRequest, listaCampaigns.size()
                );
    }

    /**
     * ============================================================================================================
     **/

    public Page<ReportTopAffiliates> searchTopAffilaites(TopFilter request, Pageable pageableRequest) {
        request = prepareRequest(request);
        List<ReportTopAffiliates> listaAffiliates;
        if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin())) {
            listaAffiliates = reportRepository.searchTopAffilaites(request.getDateTimeFrom(), request.getDateTimeTo(), jwtUserDetailsService.getAffiliateID(), request.getCampaignId(), request.getDictionaryIds(), request.getStatusIds());
        } else {
            listaAffiliates = reportRepository.searchTopAffilaites(request.getDateTimeFrom(), request.getDateTimeTo(), request.getAffiliateid(), request.getCampaignId(), request.getDictionaryIds(), request.getStatusIds());
        }
        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), listaAffiliates.size());
        return new PageImpl<>(listaAffiliates.stream().distinct().collect(Collectors.toList()).subList((int) pageableRequest.getOffset(), end), pageableRequest, listaAffiliates.size());
    }

    /**
     * ============================================================================================================
     **/

    public Page<ReportTopAffiliates> searchTopAffilaitesChannel(TopFilter request, Pageable pageableRequest) {
        request = prepareRequest(request);
        List<ReportTopAffiliates> listaAffiliates;
        if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin())) {
            listaAffiliates = reportRepository.searchTopAffilaitesChannel(request.getDateTimeFrom(), request.getDateTimeTo(), jwtUserDetailsService.getAffiliateID(), request.getCampaignId(), request.getDictionaryIds(), request.getStatusIds());
        } else {
            listaAffiliates = reportRepository.searchTopAffilaitesChannel(request.getDateTimeFrom(), request.getDateTimeTo(), request.getAffiliateid(), request.getCampaignId(), request.getDictionaryIds(), request.getStatusIds());
        }
        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), listaAffiliates.size());
        return new PageImpl<>(listaAffiliates.stream().distinct().collect(Collectors.toList()).subList((int) pageableRequest.getOffset(), end), pageableRequest, listaAffiliates.size());
    }

    /**
     * ============================================================================================================
     **/

    public Page<ReportDaily> searchDaily(TopFilter request, Pageable pageableRequest) {
        request = prepareRequest(request);
        if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin()))
            request.setAffiliateid(jwtUserDetailsService.getAffiliateID());
        List<ReportDaily> lista = reportRepository.searchDaily(request.getDateTimeFrom(), request.getDateTimeTo().plusDays(1), request.getAffiliateid(), request.getCampaignId(), request.getDictionaryIds(), request.getStatusIds());
        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), lista.size());
        return new PageImpl<>(lista.stream().distinct().collect(Collectors.toList()).subList((int) pageableRequest.getOffset(), end), pageableRequest, lista.size());
    }

    /**
     * ============================================================================================================
     **/

    private TopFilter prepareRequest(TopFilter request) {

        if (request.getDictionaryIds() == null) {
            List<Long> ll = new ArrayList<>();
            ll.add(39L);
            ll.add(42L);
            ll.add(41L);
            ll.add(68L);
            ll.add(49L);
            ll.add(48L);
            ll.add(47L);
            ll.add(40L);
            ll.add(70L);
            request.setDictionaryIds(ll);
        }

        if (request.getStatusIds() == null) {
            List<Long> ll = new ArrayList<>();
            ll.add(72L);
            ll.add(73L);
            ll.add(74L);
            request.setStatusIds(ll);
        }

        return request;
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
        private List<Long> dictionaryIds = null;
        @NotNull
        private List<Long> statusIds = null;
        private Long affiliateid;
        private Long campaignId;
        private Long statusId;
        private Long channelId;
    }

}