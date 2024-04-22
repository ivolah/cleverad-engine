package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.*;
import it.cleverad.engine.persistence.repository.service.QueryRepository;
import it.cleverad.engine.persistence.repository.service.ReportRepository;
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
    private QueryRepository queryRepository;
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

    public Page<QueryTopElenco> searchCampaginsOrderedStatWidget(TopFilter request, Pageable pageableRequest) {

        if (jwtUserDetailsService.isAffiliate()) {
            request.setAffiliateid(jwtUserDetailsService.getAffiliateId());
        } else if (jwtUserDetailsService.isAdvertiser()) {
            request.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
        }

        log.info(request.toString());
        List<QueryTopElenco> listaCampaigns = queryRepository.listaTopCampagneSortate(request.getDateTimeFrom(), request.getDateTimeTo(), request.getAffiliateid(), request.getCampaignId(), request.getAdvertiserId());
        listaCampaigns.stream().forEach(reportTopCampaings -> log.info("ListaCampaign " + reportTopCampaings.getCampaignId() + " " + reportTopCampaings.getCampaignName()));
        return new PageImpl<>(listaCampaigns.stream().collect(Collectors.toList()));
    }

    /**
     * ============================================================================================================
     **/

//    public Page<ReportTopCampaings> searchTopCampaigns(TopFilter request, Pageable pageableRequest) {
//        request = prepareRequest(request);
//        List<ReportTopCampaings> listaCampaigns = null;
//
//        if (jwtUserDetailsService.isAdmin()) {
//            listaCampaigns = reportRepository.searchTopCampaigns(request.getDateTimeFrom(), request.getDateTimeTo(), request.getAffiliateid(), request.getCampaignId(), request.getDictionaryIds(), request.getStatusIds());
//        } else if (jwtUserDetailsService.isAffiliate()) {
//            listaCampaigns = reportRepository.searchTopCampaigns(request.getDateTimeFrom(), request.getDateTimeTo(), jwtUserDetailsService.getAffiliateId(), request.getCampaignId(), request.getDictionaryIds(), request.getStatusIds());
//        } else if (jwtUserDetailsService.isAdvertiser()) {
//            //nulla
//        }
//
//        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), listaCampaigns.size());
//        return new PageImpl<>(listaCampaigns.stream().distinct().collect(Collectors.toList()).subList((int) pageableRequest.getOffset(), end), pageableRequest, listaCampaigns.size());
//    }


    public Page<ReportCampagneDTO> searchReportCampaign(TopFilter request, Pageable pageableRequest) {
        request = prepareRequest(request);
        if (jwtUserDetailsService.isAffiliate()) {
            request.setAffiliateid(jwtUserDetailsService.getAffiliateId());
        } else if (jwtUserDetailsService.isAdvertiser()) {
            request.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
        }
        log.info(request + "");
        Long dictId = null;
        if (request.getDictionaryIds().size() > 0) {
            dictId = request.getDictionaryIds().get(0);
        }
        List<ReportCampagneDTO> lista = reportRepository.searchReportCampaign(
                request.getDateTimeFrom().atStartOfDay(),
                request.getDateTimeTo().atTime(23, 59, 59, 99999),
                request.getStatusId(), null,
                request.getAffiliateid(), null,
                request.getCampaignId(),
                request.getAdvertiserId(),
                dictId,
                request.getStatusIds()
        );
        ReportCampagneDTO ultimaThule = new ReportCampagneDTO();
        ultimaThule.setClickNumber(lista.stream().mapToLong(reportDaily -> reportDaily.getClickNumber()).sum());
        ultimaThule.setClickNumberRigettato(lista.stream().mapToLong(reportDaily -> reportDaily.getClickNumberRigettato()).sum());
        ultimaThule.setLeadNumber(lista.stream().mapToLong(reportDaily -> reportDaily.getLeadNumber()).sum());
        ultimaThule.setLeadNumberRigettato(lista.stream().mapToLong(reportDaily -> reportDaily.getLeadNumberRigettato()).sum());
        ultimaThule.setImpressionNumber(lista.stream().mapToLong(reportDaily -> reportDaily.getImpressionNumber()).sum());
        if (ultimaThule.getImpressionNumber() != null && ultimaThule.getImpressionNumber() > 0) {
            double ctr = (ultimaThule.getClickNumber().doubleValue() / ultimaThule.getImpressionNumber().doubleValue()) * 100;
            ultimaThule.setCtr("" + ctr + "");
        }
        if (ultimaThule.getClickNumber() != null && ultimaThule.getClickNumber() > 0) {
            double lr = (ultimaThule.getLeadNumber().doubleValue() / ultimaThule.getClickNumber().doubleValue()) * 100;
            ultimaThule.setLr("" + lr);
        }
        ultimaThule.setCommission(lista.stream().mapToDouble(reportDaily -> reportDaily.getCommission()).sum());
        ultimaThule.setCommissionRigettato(lista.stream().mapToDouble(reportDaily -> reportDaily.getCommissionRigettato()).sum());
        ultimaThule.setRevenue(lista.stream().mapToDouble(reportDaily -> reportDaily.getRevenue()).sum());
        ultimaThule.setRevenueRigettato(lista.stream().mapToDouble(reportDaily -> reportDaily.getRevenueRigettato()).sum());
        ultimaThule.setMargine(ultimaThule.getRevenue() - ultimaThule.getCommission());
        if (ultimaThule.getRevenue() != null && ultimaThule.getRevenue() > 0) {
            Double marginePC = ((ultimaThule.getRevenue().doubleValue() - ultimaThule.getCommission().doubleValue()) / ultimaThule.getRevenue().doubleValue() * 100);
            ultimaThule.setMarginePC(DoubleRounder.round(marginePC, 2));
        } else
            ultimaThule.setMarginePC(0d);
        if (ultimaThule.getImpressionNumber() != null && ultimaThule.getImpressionNumber() > 0) {
            Double ecpm = ultimaThule.getCommission().doubleValue() / ultimaThule.getImpressionNumber().doubleValue() * 1000;
            ultimaThule.setEcpm(DoubleRounder.round(ecpm, 2) + "");
        }
        if (ultimaThule.getClickNumber() != null && ultimaThule.getClickNumber() > 0) {
            Double ecpc = ultimaThule.getCommission().doubleValue() / ultimaThule.getClickNumber().doubleValue();
            ultimaThule.setEcpc(DoubleRounder.round(ecpc, 2) + "");
        }
        if (ultimaThule.getLeadNumber() != null && ultimaThule.getLeadNumber() > 0) {
            Double ecpl = ultimaThule.getCommission().doubleValue() / ultimaThule.getLeadNumber().doubleValue();
            ultimaThule.setEcpl(DoubleRounder.round(ecpl, 2) + "");
        }
        //ultima riga calcolata
        lista.add(ultimaThule);
        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), lista.size());
        Page<ReportCampagneDTO> pp = new PageImpl<>(lista.stream().distinct().collect(Collectors.toList()).subList((int) pageableRequest.getOffset(), end), pageableRequest, lista.size());
        return pp;
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
        log.info(request + "");
        Long dictId = null;
        if (request.getDictionaryIds().size() > 0) {
            dictId = request.getDictionaryIds().get(0);
        }
        List<ReportDailyDTO> lista = reportRepository.searchReportDaily(
                request.getDateTimeFrom().atStartOfDay(),
                request.getDateTimeTo().atTime(23, 59, 59, 99999),
                request.getStatusId(), null,
                request.getAffiliateid(), null,
                request.getCampaignId(),
                request.getAdvertiserId(),
                dictId,
                request.getStatusIds()
        );
        ReportDailyDTO report = new ReportDailyDTO();
        report.setGiorno("Total : ");
        report.setClickNumber(lista.stream().mapToLong(reportDaily -> reportDaily.getClickNumber()).sum());
        report.setClickNumberRigettato(lista.stream().mapToLong(reportDaily -> reportDaily.getClickNumberRigettato()).sum());
        report.setLeadNumber(lista.stream().mapToLong(reportDaily -> reportDaily.getLeadNumber()).sum());
        report.setLeadNumberRigettato(lista.stream().mapToLong(reportDaily -> reportDaily.getLeadNumberRigettato()).sum());
        report.setImpressionNumber(lista.stream().mapToLong(reportDaily -> reportDaily.getImpressionNumber()).sum());
        if (report.getImpressionNumber() != null && report.getImpressionNumber() > 0) {
            double ctr = (report.getClickNumber().doubleValue() / report.getImpressionNumber().doubleValue()) * 100;
            report.setCtr("" + ctr + "");
        }
        if (report.getClickNumber() != null && report.getClickNumber() > 0) {
            double lr = (report.getLeadNumber().doubleValue() / report.getClickNumber().doubleValue()) * 100;
            report.setLr("" + lr);
        }
        report.setCommission(lista.stream().mapToDouble(reportDaily -> reportDaily.getCommission()).sum());
        report.setCommissionRigettato(lista.stream().mapToDouble(reportDaily -> reportDaily.getCommissionRigettato()).sum());
        report.setRevenue(lista.stream().mapToDouble(reportDaily -> reportDaily.getRevenue()).sum());
        report.setRevenueRigettato(lista.stream().mapToDouble(reportDaily -> reportDaily.getRevenueRigettato()).sum());
        report.setMargine(report.getRevenue() - report.getCommission());
        if (report.getRevenue() != null && report.getRevenue() > 0) {
            Double marginePC = ((report.getRevenue().doubleValue() - report.getCommission().doubleValue()) / report.getRevenue().doubleValue() * 100);
            report.setMarginePC(DoubleRounder.round(marginePC, 2));
        } else
            report.setMarginePC(0d);
        if (report.getImpressionNumber() != null && report.getImpressionNumber() > 0) {
            Double ecpm = report.getCommission().doubleValue() / report.getImpressionNumber().doubleValue() * 1000;
            report.setEcpm(DoubleRounder.round(ecpm, 2) + "");
        }
        if (report.getClickNumber() != null && report.getClickNumber() > 0) {
            Double ecpc = report.getCommission().doubleValue() / report.getClickNumber().doubleValue();
            report.setEcpc(DoubleRounder.round(ecpc, 2) + "");
        }
        if (report.getLeadNumber() != null && report.getLeadNumber() > 0) {
            Double ecpl = report.getCommission().doubleValue() / report.getLeadNumber().doubleValue();
            report.setEcpl(DoubleRounder.round(ecpl, 2) + "");
        }
        //ultima riga calcolata
        lista.add(report);
        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), lista.size());
        Page<ReportDailyDTO> pp = new PageImpl<>(lista.stream().distinct().collect(Collectors.toList()).subList((int) pageableRequest.getOffset(), end), pageableRequest, lista.size());
        return pp;
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
        log.info(request + "");
        Long dictId = null;
        if (request.getDictionaryIds().size() > 0) {
            dictId = request.getDictionaryIds().get(0);
        }
        List<ReportAffiliatesDTO> lista = reportRepository.searchReportAffiliate(
                request.getDateTimeFrom().atStartOfDay(),
                request.getDateTimeTo().atTime(23, 59, 59, 99999),
                request.getStatusId(), null,
                request.getAffiliateid(), null,
                request.getCampaignId(),
                request.getAdvertiserId(),
                dictId,
                request.getStatusIds()
        );
        ReportAffiliatesDTO report = new ReportAffiliatesDTO();
        report.setClickNumber(lista.stream().mapToLong(reportDaily -> reportDaily.getClickNumber()).sum());
        report.setClickNumberRigettato(lista.stream().mapToLong(reportDaily -> reportDaily.getClickNumberRigettato()).sum());
        report.setLeadNumber(lista.stream().mapToLong(reportDaily -> reportDaily.getLeadNumber()).sum());
        report.setLeadNumberRigettato(lista.stream().mapToLong(reportDaily -> reportDaily.getLeadNumberRigettato()).sum());
        report.setImpressionNumber(lista.stream().mapToLong(reportDaily -> reportDaily.getImpressionNumber()).sum());
        if (report.getImpressionNumber() != null && report.getImpressionNumber() > 0) {
            double ctr = (report.getClickNumber().doubleValue() / report.getImpressionNumber().doubleValue()) * 100;
            report.setCtr("" + ctr + "");
        }
        if (report.getClickNumber() != null && report.getClickNumber() > 0) {
            double lr = (report.getLeadNumber().doubleValue() / report.getClickNumber().doubleValue()) * 100;
            report.setLr("" + lr);
        }
        report.setCommission(lista.stream().mapToDouble(reportDaily -> reportDaily.getCommission()).sum());
        report.setCommissionRigettato(lista.stream().mapToDouble(reportDaily -> reportDaily.getCommissionRigettato()).sum());
        report.setRevenue(lista.stream().mapToDouble(reportDaily -> reportDaily.getRevenue()).sum());
        report.setRevenueRigettato(lista.stream().mapToDouble(reportDaily -> reportDaily.getRevenueRigettato()).sum());
        report.setMargine(report.getRevenue() - report.getCommission());
        if (report.getRevenue() != null && report.getRevenue() > 0) {
            Double marginePC = ((report.getRevenue().doubleValue() - report.getCommission().doubleValue()) / report.getRevenue().doubleValue() * 100);
            report.setMarginePC(DoubleRounder.round(marginePC, 2));
        } else
            report.setMarginePC(0d);
        if (report.getImpressionNumber() != null && report.getImpressionNumber() > 0) {
            Double ecpm = report.getCommission().doubleValue() / report.getImpressionNumber().doubleValue() * 1000;
            report.setEcpm(DoubleRounder.round(ecpm, 2) + "");
        }
        if (report.getClickNumber() != null && report.getClickNumber() > 0) {
            Double ecpc = report.getCommission().doubleValue() / report.getClickNumber().doubleValue();
            report.setEcpc(DoubleRounder.round(ecpc, 2) + "");
        }
        if (report.getLeadNumber() != null && report.getLeadNumber() > 0) {
            Double ecpl = report.getCommission().doubleValue() / report.getLeadNumber().doubleValue();
            report.setEcpl(DoubleRounder.round(ecpl, 2) + "");
        }
        //ultima riga calcolata
        lista.add(report);
        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), lista.size());
        Page<ReportAffiliatesDTO> pp = new PageImpl<>(lista.stream().distinct().collect(Collectors.toList()).subList((int) pageableRequest.getOffset(), end), pageableRequest, lista.size());
        return pp;
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
        log.info(request + "");
        Long dictId = null;
        if (request.getDictionaryIds().size() > 0) {
            dictId = request.getDictionaryIds().get(0);
        }
        List<ReportAffiliatesChannelDTO> lista = reportRepository.searchReportAffiliateChannel(
                request.getDateTimeFrom().atStartOfDay(),
                request.getDateTimeTo().atTime(23, 59, 59, 99999),
                request.getStatusId(), null,
                request.getAffiliateid(), null,
                request.getCampaignId(),
                request.getAdvertiserId(),
                dictId,
                request.getStatusIds()
        );
        ReportAffiliatesChannelDTO report = new ReportAffiliatesChannelDTO();
        report.setClickNumber(lista.stream().mapToLong(reportDaily -> reportDaily.getClickNumber()).sum());
        report.setClickNumberRigettato(lista.stream().mapToLong(reportDaily -> reportDaily.getClickNumberRigettato()).sum());
        report.setLeadNumber(lista.stream().mapToLong(reportDaily -> reportDaily.getLeadNumber()).sum());
        report.setLeadNumberRigettato(lista.stream().mapToLong(reportDaily -> reportDaily.getLeadNumberRigettato()).sum());
        report.setImpressionNumber(lista.stream().mapToLong(reportDaily -> reportDaily.getImpressionNumber()).sum());
        if (report.getImpressionNumber() != null && report.getImpressionNumber() > 0) {
            double ctr = (report.getClickNumber().doubleValue() / report.getImpressionNumber().doubleValue()) * 100;
            report.setCtr("" + ctr + "");
        }
        if (report.getClickNumber() != null && report.getClickNumber() > 0) {
            double lr = (report.getLeadNumber().doubleValue() / report.getClickNumber().doubleValue()) * 100;
            report.setLr("" + lr);
        }
        report.setCommission(lista.stream().mapToDouble(reportDaily -> reportDaily.getCommission()).sum());
        report.setCommissionRigettato(lista.stream().mapToDouble(reportDaily -> reportDaily.getCommissionRigettato()).sum());
        report.setRevenue(lista.stream().mapToDouble(reportDaily -> reportDaily.getRevenue()).sum());
        report.setRevenueRigettato(lista.stream().mapToDouble(reportDaily -> reportDaily.getRevenueRigettato()).sum());
        report.setMargine(report.getRevenue() - report.getCommission());
        if (report.getRevenue() != null && report.getRevenue() > 0) {
            Double marginePC = ((report.getRevenue().doubleValue() - report.getCommission().doubleValue()) / report.getRevenue().doubleValue() * 100);
            report.setMarginePC(DoubleRounder.round(marginePC, 2));
        } else
            report.setMarginePC(0d);
        if (report.getImpressionNumber() != null && report.getImpressionNumber() > 0) {
            Double ecpm = report.getCommission().doubleValue() / report.getImpressionNumber().doubleValue() * 1000;
            report.setEcpm(DoubleRounder.round(ecpm, 2) + "");
        }
        if (report.getClickNumber() != null && report.getClickNumber() > 0) {
            Double ecpc = report.getCommission().doubleValue() / report.getClickNumber().doubleValue();
            report.setEcpc(DoubleRounder.round(ecpc, 2) + "");
        }
        if (report.getLeadNumber() != null && report.getLeadNumber() > 0) {
            Double ecpl = report.getCommission().doubleValue() / report.getLeadNumber().doubleValue();
            report.setEcpl(DoubleRounder.round(ecpl, 2) + "");
        }
        //ultima riga calcolata
        lista.add(report);
        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), lista.size());
        Page<ReportAffiliatesChannelDTO> pp = new PageImpl<>(lista.stream().distinct().collect(Collectors.toList()).subList((int) pageableRequest.getOffset(), end), pageableRequest, lista.size());
        return pp;
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
        log.info(request + "");
        Long dictId = null;
        if (request.getDictionaryIds().size() > 0) {
            dictId = request.getDictionaryIds().get(0);
        }
        List<ReportAffiliatesChannelCampaignDTO> lista = reportRepository.searchReportAffiliateChannelCampaign(
                request.getDateTimeFrom().atStartOfDay(),
                request.getDateTimeTo().atTime(23, 59, 59, 99999),
                request.getStatusId(), null,
                request.getAffiliateid(), null,
                request.getCampaignId(),
                request.getAdvertiserId(),
                dictId,
                request.getStatusIds()
        );
        ReportAffiliatesChannelCampaignDTO report = new ReportAffiliatesChannelCampaignDTO();
        report.setClickNumber(lista.stream().mapToLong(reportDaily -> reportDaily.getClickNumber()).sum());
        report.setClickNumberRigettato(lista.stream().mapToLong(reportDaily -> reportDaily.getClickNumberRigettato()).sum());
        report.setLeadNumber(lista.stream().mapToLong(reportDaily -> reportDaily.getLeadNumber()).sum());
        report.setLeadNumberRigettato(lista.stream().mapToLong(reportDaily -> reportDaily.getLeadNumberRigettato()).sum());
        report.setImpressionNumber(lista.stream().mapToLong(reportDaily -> reportDaily.getImpressionNumber()).sum());
        if (report.getImpressionNumber() != null && report.getImpressionNumber() > 0) {
            double ctr = (report.getClickNumber().doubleValue() / report.getImpressionNumber().doubleValue()) * 100;
            report.setCtr("" + ctr + "");
        }
        if (report.getClickNumber() != null && report.getClickNumber() > 0) {
            double lr = (report.getLeadNumber().doubleValue() / report.getClickNumber().doubleValue()) * 100;
            report.setLr("" + lr);
        }
        report.setCommission(lista.stream().mapToDouble(reportDaily -> reportDaily.getCommission()).sum());
        report.setCommissionRigettato(lista.stream().mapToDouble(reportDaily -> reportDaily.getCommissionRigettato()).sum());
        report.setRevenue(lista.stream().mapToDouble(reportDaily -> reportDaily.getRevenue()).sum());
        report.setRevenueRigettato(lista.stream().mapToDouble(reportDaily -> reportDaily.getRevenueRigettato()).sum());
        report.setMargine(report.getRevenue() - report.getCommission());
        if (report.getRevenue() != null && report.getRevenue() > 0) {
            Double marginePC = ((report.getRevenue().doubleValue() - report.getCommission().doubleValue()) / report.getRevenue().doubleValue() * 100);
            report.setMarginePC(DoubleRounder.round(marginePC, 2));
        } else
            report.setMarginePC(0d);
        if (report.getImpressionNumber() != null && report.getImpressionNumber() > 0) {
            Double ecpm = report.getCommission().doubleValue() / report.getImpressionNumber().doubleValue() * 1000;
            report.setEcpm(DoubleRounder.round(ecpm, 2) + "");
        }
        if (report.getClickNumber() != null && report.getClickNumber() > 0) {
            Double ecpc = report.getCommission().doubleValue() / report.getClickNumber().doubleValue();
            report.setEcpc(DoubleRounder.round(ecpc, 2) + "");
        }
        if (report.getLeadNumber() != null && report.getLeadNumber() > 0) {
            Double ecpl = report.getCommission().doubleValue() / report.getLeadNumber().doubleValue();
            report.setEcpl(DoubleRounder.round(ecpl, 2) + "");
        }
        //ultima riga calcolata
        lista.add(report);
        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), lista.size());
        Page<ReportAffiliatesChannelCampaignDTO> pp = new PageImpl<>(lista.stream().distinct().collect(Collectors.toList()).subList((int) pageableRequest.getOffset(), end), pageableRequest, lista.size());
        return pp;
    }


    /**
     * ============================================================================================================
     **/


//    public Page<ReportDaily> searchDaily(TopFilter request, Pageable pageableRequest) {
//        request = prepareRequest(request);
//        if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin()))
//            request.setAffiliateid(jwtUserDetailsService.getAffiliateId());
//        List<ReportDaily> lista = reportRepository.searchDaily(request.getDateTimeFrom(), request.getDateTimeTo().plusDays(1),
//        request.getAffiliateid(), request.getCampaignId(), request.getDictionaryIds(), request.getStatusIds());
//        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), lista.size());
//        return new PageImpl<>(lista.stream().distinct().collect(Collectors.toList()).subList((int) pageableRequest.getOffset(), end), pageableRequest, lista.size());
//    }
    //    public Page<ReportTopAffiliates> searchTopAffilaites(TopFilter request, Pageable pageableRequest) {
//        request = prepareRequest(request);
//        List<ReportTopAffiliates> listaAffiliates;
//        if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin())) {
//            listaAffiliates = reportRepository.searchTopAffilaites(request.getDateTimeFrom(), request.getDateTimeTo(), jwtUserDetailsService.getAffiliateId(), request.getCampaignId(), request.getDictionaryIds(), request.getStatusIds());
//        } else {
//            listaAffiliates = reportRepository.searchTopAffilaites(request.getDateTimeFrom(), request.getDateTimeTo(), request.getAffiliateid(), request.getCampaignId(), request.getDictionaryIds(), request.getStatusIds());
//        }
//        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), listaAffiliates.size());
//        return new PageImpl<>(listaAffiliates.stream().distinct().collect(Collectors.toList()).subList((int) pageableRequest.getOffset(), end), pageableRequest, listaAffiliates.size());
//    }
//    public Page<ReportTopCampaings> searchTopCampaignsChannel(TopFilter request, Pageable pageableRequest) {
//        request = prepareRequest(request);
//        List<ReportTopCampaings> listaCampaigns;
//        if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin())) {
//            listaCampaigns = reportRepository.searchTopCampaignsChannel(request.getDateTimeFrom(), request.getDateTimeTo().plusDays(1), jwtUserDetailsService.getAffiliateId(), request.getCampaignId(), request.getDictionaryIds(), request.getStatusIds());
//        } else {
//            listaCampaigns = reportRepository.searchTopCampaignsChannel(request.getDateTimeFrom(), request.getDateTimeTo().plusDays(1), request.getAffiliateid(), request.getCampaignId(), request.getDictionaryIds(), request.getStatusIds());
//        }
//        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), listaCampaigns.size());
//        return new PageImpl<>(listaCampaigns.stream().distinct().collect(Collectors.toList()).subList((int) pageableRequest.getOffset(), end), pageableRequest, listaCampaigns.size());
//    }
//    public Page<ReportTopAffiliates> searchTopAffilaitesChannel(TopFilter request, Pageable pageableRequest) {
//        request = prepareRequest(request);
//        List<ReportTopAffiliates> listaAffiliates;
//        if (Boolean.FALSE.equals(jwtUserDetailsService.isAdmin())) {
//            listaAffiliates = reportRepository.searchTopAffilaitesChannel(request.getDateTimeFrom(), request.getDateTimeTo(), jwtUserDetailsService.getAffiliateId(), request.getCampaignId(), request.getDictionaryIds(), request.getStatusIds());
//        } else {
//            listaAffiliates = reportRepository.searchTopAffilaitesChannel(request.getDateTimeFrom(), request.getDateTimeTo(), request.getAffiliateid(), request.getCampaignId(), request.getDictionaryIds(), request.getStatusIds());
//        }
//        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), listaAffiliates.size());
//        return new PageImpl<>(listaAffiliates.stream().distinct().collect(Collectors.toList()).subList((int) pageableRequest.getOffset(), end), pageableRequest, listaAffiliates.size());
//    }

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
        private Long affiliateid = null;
        private Long campaignId = null;
        private Long advertiserId = null;
        private Long statusId;
        private Long channelId;
    }

}