package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.QueryTopElenco;
import it.cleverad.engine.persistence.model.service.Report;
import it.cleverad.engine.persistence.repository.service.QueryRepository;
import it.cleverad.engine.persistence.repository.service.ReportRepository;
import it.cleverad.engine.web.dto.*;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.decimal4j.util.DoubleRounder;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import jakarta.validation.constraints.NotNull;
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
    private QueryRepository queryRepository;
    @Autowired
    private CampaignBusiness campaignBusiness;

    @Autowired
    private Mapper mapper;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public it.cleverad.engine.web.dto.ReportDTO create(BaseCreateRequest request) {
        Report map = mapper.map(request, Report.class);
        return it.cleverad.engine.web.dto.ReportDTO.from(reportRepository.save(map));
    }

    // GET BY ID
    public it.cleverad.engine.web.dto.ReportDTO findById(Long id) {
        Report channel = reportRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Report", id));
        return it.cleverad.engine.web.dto.ReportDTO.from(channel);
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
    public Page<it.cleverad.engine.web.dto.ReportDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("name")));
        Page<Report> page = reportRepository.findAll(getSpecification(request), pageable);
        return page.map(it.cleverad.engine.web.dto.ReportDTO::from);
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

    public Page<QueryTopElenco> searchCampaginsOrderedStatWidget(TopFilter request) {

        if (jwtUserDetailsService.isAffiliate()) {
            request.setAffiliateid(jwtUserDetailsService.getAffiliateId());
        } else if (jwtUserDetailsService.isAdvertiser()) {
            request.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
        }

        log.trace(request.toString());
        List<QueryTopElenco> listaCampaigns = queryRepository.listaTopCampagneSortate(request.getDateTimeFrom(), request.getDateTimeTo(), request.getAffiliateid(), request.getCampaignId(), request.getAdvertiserId());
        return new PageImpl<>(new ArrayList<>(listaCampaigns));
    }

    /**
     * ============================================================================================================
     **/
    public Page<ReportCampagneDTO> searchReportCampaign(TopFilter request, Pageable pageableRequest) {
        request = prepareRequest(request);
        if (jwtUserDetailsService.isAffiliate()) {
            request.setAffiliateid(jwtUserDetailsService.getAffiliateId());
        } else if (jwtUserDetailsService.isAdvertiser()) {
            request.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
        }
        Long dictId = null;
        if (!request.getDictionaryIds().isEmpty()) {
            dictId = request.getDictionaryIds().get(0);
        }
        if (request.campaignActive != null && request.campaignActive && request.getDateTimeFrom() != null) {
            List<Long> cids = campaignBusiness.getCampaignsinDateRange(request.getDateTimeFrom(), request.getDateTimeTo()).stream().mapToLong(CampaignDTO::getId).boxed().collect(Collectors.toList());
            log.info(cids.size() + " !!!");
            cids.forEach(aLong -> log.info("Campaign {} ", aLong));
            request.setCampaignIds(cids);
        }
        List<ReportCampagneDTO> lista = reportRepository.searchReportCampaign(request.getDateTimeFrom().atStartOfDay(), request.getDateTimeTo().atTime(23, 59, 59, 99999), request.getStatusId(), null, request.getAffiliateid(), null, request.getCampaignId(), request.getAdvertiserId(), dictId, request.getStatusIds(), request.getCampaignIds());
        ReportCampagneDTO ultimaThule = new ReportCampagneDTO();
        ultimaThule.setClickNumber(lista.stream().mapToLong(ReportCampagneDTO::getClickNumber).sum());
        ultimaThule.setClickNumberRigettato(lista.stream().mapToLong(ReportCampagneDTO::getClickNumberRigettato).sum());
        ultimaThule.setLeadNumber(lista.stream().mapToLong(ReportCampagneDTO::getLeadNumber).sum());
        ultimaThule.setLeadNumberRigettato(lista.stream().mapToLong(ReportCampagneDTO::getLeadNumberRigettato).sum());
        ultimaThule.setImpressionNumber(lista.stream().mapToLong(ReportCampagneDTO::getImpressionNumber).sum());
        if (ultimaThule.getImpressionNumber() != null && ultimaThule.getImpressionNumber() > 0) {
            double ctr = (ultimaThule.getClickNumber().doubleValue() / ultimaThule.getImpressionNumber().doubleValue()) * 100;
            ultimaThule.setCtr("" + ctr + "");
        }
        if (ultimaThule.getClickNumber() != null && ultimaThule.getClickNumber() > 0) {
            double lr = (ultimaThule.getLeadNumber().doubleValue() / ultimaThule.getClickNumber().doubleValue()) * 100;
            ultimaThule.setLr("" + lr);
        }
        ultimaThule.setCommission(lista.stream().mapToDouble(ReportCampagneDTO::getCommission).sum());
        ultimaThule.setCommissionRigettato(lista.stream().mapToDouble(ReportCampagneDTO::getCommissionRigettato).sum());
        ultimaThule.setRevenue(lista.stream().mapToDouble(ReportCampagneDTO::getRevenue).sum());
        ultimaThule.setRevenueRigettato(lista.stream().mapToDouble(ReportCampagneDTO::getRevenueRigettato).sum());
        ultimaThule.setMargine(ultimaThule.getRevenue() - ultimaThule.getCommission());
        if (ultimaThule.getRevenue() != null && ultimaThule.getRevenue() > 0) {
            Double marginePC = ((ultimaThule.getRevenue() - ultimaThule.getCommission()) / ultimaThule.getRevenue() * 100);
            ultimaThule.setMarginePC(DoubleRounder.round(marginePC, 2));
        } else ultimaThule.setMarginePC(0d);
        if (ultimaThule.getImpressionNumber() != null && ultimaThule.getImpressionNumber() > 0) {
            Double ecpm = ultimaThule.getCommission() / ultimaThule.getImpressionNumber().doubleValue() * 1000;
            ultimaThule.setEcpm(DoubleRounder.round(ecpm, 2) + "");
        }
        if (ultimaThule.getClickNumber() != null && ultimaThule.getClickNumber() > 0) {
            Double ecpc = ultimaThule.getCommission() / ultimaThule.getClickNumber().doubleValue();
            ultimaThule.setEcpc(DoubleRounder.round(ecpc, 2) + "");
        }
        if (ultimaThule.getLeadNumber() != null && ultimaThule.getLeadNumber() > 0) {
            Double ecpl = ultimaThule.getCommission() / ultimaThule.getLeadNumber().doubleValue();
            ultimaThule.setEcpl(DoubleRounder.round(ecpl, 2) + "");
        }
        //ultima riga calcolata
        lista.add(ultimaThule);
        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), lista.size());
        return new PageImpl<>(lista.stream().distinct().collect(Collectors.toList()).subList((int) pageableRequest.getOffset(), end), pageableRequest, lista.size());
    }

    /**
     * ============================================================================================================
     **/

    /**
     * Searches for daily reports based on the specified filter criteria.
     *
     * @param request         the filter criteria
     * @param pageableRequest the pagination information
     * @return a page of daily reports that match the specified criteria
     */
    public Page<ReportDailyDTO> searchReportDaily(TopFilter request, Pageable pageableRequest) {
        request = prepareRequest(request);
        if (jwtUserDetailsService.isAffiliate()) {
            request.setAffiliateid(jwtUserDetailsService.getAffiliateId());
        } else if (jwtUserDetailsService.isAdvertiser()) {
            request.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
        }
        Long dictId = null;
        if (!request.getDictionaryIds().isEmpty()) {
            dictId = request.getDictionaryIds().get(0);
        }
        if (request.campaignActive != null && request.campaignActive && request.getDateTimeFrom() != null) {
            List<Long> cids = campaignBusiness.getCampaignsinDateRange(request.getDateTimeFrom(), request.getDateTimeTo()).stream().mapToLong(CampaignDTO::getId).boxed().collect(Collectors.toList());
            request.setCampaignIds(cids);
        }
        List<ReportDailyDTO> lista = reportRepository.searchReportDaily(request.getDateTimeFrom().atStartOfDay(), request.getDateTimeTo().atTime(23, 59, 59, 99999), request.getStatusId(), null, request.getAffiliateid(), null, request.getCampaignId(), request.getAdvertiserId(), dictId, request.getStatusIds(), request.getCampaignIds());
        ReportDailyDTO report = new ReportDailyDTO();
        report.setGiorno("Total : ");
        report.setClickNumber(lista.stream().mapToLong(ReportDailyDTO::getClickNumber).sum());
        report.setClickNumberRigettato(lista.stream().mapToLong(ReportDailyDTO::getClickNumberRigettato).sum());
        report.setLeadNumber(lista.stream().mapToLong(ReportDailyDTO::getLeadNumber).sum());
        report.setLeadNumberRigettato(lista.stream().mapToLong(ReportDailyDTO::getLeadNumberRigettato).sum());
        report.setImpressionNumber(lista.stream().mapToLong(ReportDailyDTO::getImpressionNumber).sum());
        if (report.getImpressionNumber() != null && report.getImpressionNumber() > 0) {
            double ctr = (report.getClickNumber().doubleValue() / report.getImpressionNumber().doubleValue()) * 100;
            report.setCtr("" + DoubleRounder.round(ctr, 2));
        }
        if (report.getClickNumber() != null && report.getClickNumber() > 0) {
            double lr = (report.getLeadNumber().doubleValue() / report.getClickNumber().doubleValue()) * 100;
            report.setLr("" + DoubleRounder.round(lr, 2));
        }
        report.setCommission(lista.stream().mapToDouble(ReportDailyDTO::getCommission).sum());
        report.setCommissionRigettato(lista.stream().mapToDouble(ReportDailyDTO::getCommissionRigettato).sum());
        report.setRevenue(lista.stream().mapToDouble(ReportDailyDTO::getRevenue).sum());
        report.setRevenueRigettato(lista.stream().mapToDouble(ReportDailyDTO::getRevenueRigettato).sum());
        report.setMargine(report.getRevenue() - report.getCommission());
        if (report.getRevenue() != null && report.getRevenue() > 0) {
            Double marginePC = ((report.getRevenue() - report.getCommission()) / report.getRevenue() * 100);
            report.setMarginePC(DoubleRounder.round(marginePC, 2));
        } else report.setMarginePC(0d);
        if (report.getImpressionNumber() != null && report.getImpressionNumber() > 0) {
            Double ecpm = report.getCommission() / report.getImpressionNumber().doubleValue() * 1000;
            report.setEcpm(DoubleRounder.round(ecpm, 2) + "");
        }
        if (report.getClickNumber() != null && report.getClickNumber() > 0) {
            Double ecpc = report.getCommission() / report.getClickNumber().doubleValue();
            report.setEcpc(DoubleRounder.round(ecpc, 2) + "");
        }
        if (report.getLeadNumber() != null && report.getLeadNumber() > 0) {
            Double ecpl = report.getCommission() / report.getLeadNumber().doubleValue();
            report.setEcpl(DoubleRounder.round(ecpl, 2) + "");
        }
        //ultima riga calcolata
        lista.add(report);
        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), lista.size());
        return new PageImpl<>(lista.stream().distinct().collect(Collectors.toList()).subList((int) pageableRequest.getOffset(), end), pageableRequest, lista.size());
    }

    /**
     * ============================================================================================================
     **/

    /**
     * Searches for daily reports based on the specified filter criteria.
     *
     * @param request         the filter criteria
     * @param pageableRequest the pagination information
     * @return a page of daily reports that match the specified criteria
     */
    public Page<ReportAffiliatesDTO> searchReportAffiliate(TopFilter request, Pageable pageableRequest) {
        request = prepareRequest(request);
        if (jwtUserDetailsService.isAffiliate()) {
            request.setAffiliateid(jwtUserDetailsService.getAffiliateId());
        } else if (jwtUserDetailsService.isAdvertiser()) {
            request.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
        }
        Long dictId = null;
        if (!request.getDictionaryIds().isEmpty()) {
            dictId = request.getDictionaryIds().get(0);
        }
        if (request.campaignActive != null && request.campaignActive && request.getDateTimeFrom() != null) {
            List<Long> cids = campaignBusiness.getCampaignsinDateRange(request.getDateTimeFrom(), request.getDateTimeTo()).stream().mapToLong(CampaignDTO::getId).boxed().collect(Collectors.toList());
            request.setCampaignIds(cids);
        }
        List<ReportAffiliatesDTO> lista = reportRepository.searchReportAffiliate(request.getDateTimeFrom().atStartOfDay(), request.getDateTimeTo().atTime(23, 59, 59, 99999), request.getStatusId(), null, request.getAffiliateid(), null, request.getCampaignId(), request.getAdvertiserId(), dictId, request.getStatusIds(), request.getCampaignIds());
        ReportAffiliatesDTO report = new ReportAffiliatesDTO();
        report.setClickNumber(lista.stream().mapToLong(ReportAffiliatesDTO::getClickNumber).sum());
        report.setClickNumberRigettato(lista.stream().mapToLong(ReportAffiliatesDTO::getClickNumberRigettato).sum());
        report.setLeadNumber(lista.stream().mapToLong(ReportAffiliatesDTO::getLeadNumber).sum());
        report.setLeadNumberRigettato(lista.stream().mapToLong(ReportAffiliatesDTO::getLeadNumberRigettato).sum());
        report.setImpressionNumber(lista.stream().mapToLong(ReportAffiliatesDTO::getImpressionNumber).sum());
        if (report.getImpressionNumber() != null && report.getImpressionNumber() > 0) {
            double ctr = (report.getClickNumber().doubleValue() / report.getImpressionNumber().doubleValue()) * 100;
            report.setCtr("" + DoubleRounder.round(ctr, 2));
        }
        if (report.getClickNumber() != null && report.getClickNumber() > 0) {
            double lr = (report.getLeadNumber().doubleValue() / report.getClickNumber().doubleValue()) * 100;
            report.setLr("" + DoubleRounder.round(lr, 2));
        }
        report.setCommission(lista.stream().mapToDouble(ReportAffiliatesDTO::getCommission).sum());
        report.setCommissionRigettato(lista.stream().mapToDouble(ReportAffiliatesDTO::getCommissionRigettato).sum());
        report.setRevenue(lista.stream().mapToDouble(ReportAffiliatesDTO::getRevenue).sum());
        report.setRevenueRigettato(lista.stream().mapToDouble(ReportAffiliatesDTO::getRevenueRigettato).sum());
        report.setMargine(report.getRevenue() - report.getCommission());
        if (report.getRevenue() != null && report.getRevenue() > 0) {
            Double marginePC = ((report.getRevenue() - report.getCommission()) / report.getRevenue() * 100);
            report.setMarginePC(DoubleRounder.round(marginePC, 2));
        } else report.setMarginePC(0d);
        if (report.getImpressionNumber() != null && report.getImpressionNumber() > 0) {
            Double ecpm = report.getCommission() / report.getImpressionNumber().doubleValue() * 1000;
            report.setEcpm(DoubleRounder.round(ecpm, 2) + "");
        }
        if (report.getClickNumber() != null && report.getClickNumber() > 0) {
            Double ecpc = report.getCommission() / report.getClickNumber().doubleValue();
            report.setEcpc(DoubleRounder.round(ecpc, 2) + "");
        }
        if (report.getLeadNumber() != null && report.getLeadNumber() > 0) {
            Double ecpl = report.getCommission() / report.getLeadNumber().doubleValue();
            report.setEcpl(DoubleRounder.round(ecpl, 2) + "");
        }
        //ultima riga calcolata
        lista.add(report);
        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), lista.size());
        return new PageImpl<>(lista.stream().distinct().collect(Collectors.toList()).subList((int) pageableRequest.getOffset(), end), pageableRequest, lista.size());
    }

    /**
     * ============================================================================================================
     **/

    /**
     * Searches for daily reports based on the specified filter criteria.
     *
     * @param request         the filter criteria
     * @param pageableRequest the pagination information
     * @return a page of daily reports that match the specified criteria
     */
    public Page<ReportAffiliatesChannelDTO> searchReportAffiliateChannel(TopFilter request, Pageable pageableRequest) {
        request = prepareRequest(request);
        if (jwtUserDetailsService.isAffiliate()) {
            request.setAffiliateid(jwtUserDetailsService.getAffiliateId());
        } else if (jwtUserDetailsService.isAdvertiser()) {
            request.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
        }
        Long dictId = null;
        if (!request.getDictionaryIds().isEmpty()) {
            dictId = request.getDictionaryIds().get(0);
}
        if (request.campaignActive != null && request.campaignActive && request.getDateTimeFrom() != null) {
            List<Long> cids = campaignBusiness.getCampaignsinDateRange(request.getDateTimeFrom(), request.getDateTimeTo()).stream().mapToLong(CampaignDTO::getId).boxed().collect(Collectors.toList());
            request.setCampaignIds(cids);
        }
        List<ReportAffiliatesChannelDTO> lista = reportRepository.searchReportAffiliateChannel(request.getDateTimeFrom().atStartOfDay(), request.getDateTimeTo().atTime(23, 59, 59, 99999), request.getStatusId(), null, request.getAffiliateid(), null, request.getCampaignId(), request.getAdvertiserId(), dictId, request.getStatusIds(), request.getCampaignIds());
        ReportAffiliatesChannelDTO report = new ReportAffiliatesChannelDTO();
        report.setClickNumber(lista.stream().mapToLong(ReportAffiliatesChannelDTO::getClickNumber).sum());
        report.setClickNumberRigettato(lista.stream().mapToLong(ReportAffiliatesChannelDTO::getClickNumberRigettato).sum());
        report.setLeadNumber(lista.stream().mapToLong(ReportAffiliatesChannelDTO::getLeadNumber).sum());
        report.setLeadNumberRigettato(lista.stream().mapToLong(ReportAffiliatesChannelDTO::getLeadNumberRigettato).sum());
        report.setImpressionNumber(lista.stream().mapToLong(ReportAffiliatesChannelDTO::getImpressionNumber).sum());
        if (report.getImpressionNumber() != null && report.getImpressionNumber() > 0) {
            double ctr = (report.getClickNumber().doubleValue() / report.getImpressionNumber().doubleValue()) * 100;
            report.setCtr("" + DoubleRounder.round(ctr, 2));
        }
        if (report.getClickNumber() != null && report.getClickNumber() > 0) {
            double lr = (report.getLeadNumber().doubleValue() / report.getClickNumber().doubleValue()) * 100;
            report.setLr("" + DoubleRounder.round(lr, 2));
        }
        report.setCommission(lista.stream().mapToDouble(ReportAffiliatesChannelDTO::getCommission).sum());
        report.setCommissionRigettato(lista.stream().mapToDouble(ReportAffiliatesChannelDTO::getCommissionRigettato).sum());
        report.setRevenue(lista.stream().mapToDouble(ReportAffiliatesChannelDTO::getRevenue).sum());
        report.setRevenueRigettato(lista.stream().mapToDouble(ReportAffiliatesChannelDTO::getRevenueRigettato).sum());
        report.setMargine(report.getRevenue() - report.getCommission());
        if (report.getRevenue() != null && report.getRevenue() > 0) {
            Double marginePC = ((report.getRevenue() - report.getCommission()) / report.getRevenue() * 100);
            report.setMarginePC(DoubleRounder.round(marginePC, 2));
        } else report.setMarginePC(0d);
        if (report.getImpressionNumber() != null && report.getImpressionNumber() > 0) {
            Double ecpm = report.getCommission() / report.getImpressionNumber().doubleValue() * 1000;
            report.setEcpm(DoubleRounder.round(ecpm, 2) + "");
        }
        if (report.getClickNumber() != null && report.getClickNumber() > 0) {
            Double ecpc = report.getCommission() / report.getClickNumber().doubleValue();
            report.setEcpc(DoubleRounder.round(ecpc, 2) + "");
        }
        if (report.getLeadNumber() != null && report.getLeadNumber() > 0) {
            Double ecpl = report.getCommission() / report.getLeadNumber().doubleValue();
            report.setEcpl(DoubleRounder.round(ecpl, 2) + "");
        }
        //ultima riga calcolata
        lista.add(report);
        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), lista.size());
        return new PageImpl<>(lista.stream().distinct().collect(Collectors.toList()).subList((int) pageableRequest.getOffset(), end), pageableRequest, lista.size());
    }


    /**
     * ============================================================================================================
     **/
    public Page<ReportAffiliatesChannelCampaignDTO> searchReportAffiliateChannelCampaign(TopFilter request, Pageable pageableRequest) {
        request = prepareRequest(request);
        if (jwtUserDetailsService.isAffiliate()) {
            request.setAffiliateid(jwtUserDetailsService.getAffiliateId());
        } else if (jwtUserDetailsService.isAdvertiser()) {
            request.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
        }
        Long dictId = null;
        if (!request.getDictionaryIds().isEmpty()) {
            dictId = request.getDictionaryIds().get(0);
        }
        if (request.campaignActive != null && request.campaignActive && request.getDateTimeFrom() != null) {
            List<Long> cids = campaignBusiness.getCampaignsinDateRange(request.getDateTimeFrom(), request.getDateTimeTo()).stream().mapToLong(CampaignDTO::getId).boxed().collect(Collectors.toList());
            request.setCampaignIds(cids);
        }
        List<ReportAffiliatesChannelCampaignDTO> lista = reportRepository.searchReportAffiliateChannelCampaign(request.getDateTimeFrom().atStartOfDay(), request.getDateTimeTo().atTime(23, 59, 59, 99999), request.getStatusId(), null, request.getAffiliateid(), null, request.getCampaignId(), request.getAdvertiserId(), dictId, request.getStatusIds(), request.getCampaignIds());
        ReportAffiliatesChannelCampaignDTO report = new ReportAffiliatesChannelCampaignDTO();
        report.setClickNumber(lista.stream().mapToLong(ReportAffiliatesChannelCampaignDTO::getClickNumber).sum());
        report.setClickNumberRigettato(lista.stream().mapToLong(ReportAffiliatesChannelCampaignDTO::getClickNumberRigettato).sum());
        report.setLeadNumber(lista.stream().mapToLong(ReportAffiliatesChannelCampaignDTO::getLeadNumber).sum());
        report.setLeadNumberRigettato(lista.stream().mapToLong(ReportAffiliatesChannelCampaignDTO::getLeadNumberRigettato).sum());
        report.setImpressionNumber(lista.stream().mapToLong(ReportAffiliatesChannelCampaignDTO::getImpressionNumber).sum());
        if (report.getImpressionNumber() != null && report.getImpressionNumber() > 0) {
            double ctr = (report.getClickNumber().doubleValue() / report.getImpressionNumber().doubleValue()) * 100;
            report.setCtr("" + DoubleRounder.round(ctr, 2));
        }
        if (report.getClickNumber() != null && report.getClickNumber() > 0) {
            double lr = (report.getLeadNumber().doubleValue() / report.getClickNumber().doubleValue()) * 100;
            report.setLr("" + DoubleRounder.round(lr, 2));
        }
        report.setCommission(lista.stream().mapToDouble(ReportAffiliatesChannelCampaignDTO::getCommission).sum());
        report.setCommissionRigettato(lista.stream().mapToDouble(ReportAffiliatesChannelCampaignDTO::getCommissionRigettato).sum());
        report.setRevenue(lista.stream().mapToDouble(ReportAffiliatesChannelCampaignDTO::getRevenue).sum());
        report.setRevenueRigettato(lista.stream().mapToDouble(ReportAffiliatesChannelCampaignDTO::getRevenueRigettato).sum());
        report.setMargine(report.getRevenue() - report.getCommission());
        if (report.getRevenue() != null && report.getRevenue() > 0) {
            Double marginePC = ((report.getRevenue() - report.getCommission()) / report.getRevenue() * 100);
            report.setMarginePC(DoubleRounder.round(marginePC, 2));
        } else report.setMarginePC(0d);
        if (report.getImpressionNumber() != null && report.getImpressionNumber() > 0) {
            Double ecpm = report.getCommission() / report.getImpressionNumber().doubleValue() * 1000;
            report.setEcpm(DoubleRounder.round(ecpm, 2) + "");
        }
        if (report.getClickNumber() != null && report.getClickNumber() > 0) {
            Double ecpc = report.getCommission() / report.getClickNumber().doubleValue();
            report.setEcpc(DoubleRounder.round(ecpc, 2) + "");
        }
        if (report.getLeadNumber() != null && report.getLeadNumber() > 0) {
            Double ecpl = report.getCommission() / report.getLeadNumber().doubleValue();
            report.setEcpl(DoubleRounder.round(ecpl, 2) + "");
        }
        //ultima riga calcolata
        lista.add(report);
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
        private List<Long> statusIds = new ArrayList<>();
        private List<Long> dictionaryIds = new ArrayList<>();
        private List<Long> campaignIds = new ArrayList<>();
        private Long affiliateid = null;
        private Long campaignId = null;
        private Long advertiserId = null;
        private Long statusId;
        private Long channelId;
        private Boolean campaignActive;
    }

}