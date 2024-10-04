package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.*;
import it.cleverad.engine.persistence.repository.service.*;
import it.cleverad.engine.web.dto.DictionaryDTO;
import it.cleverad.engine.web.dto.TransactionCPCDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class TransactionCPCBusiness {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private TransactionCPCRepository cpcRepository;
    @Autowired
    private Mapper mapper;
    @Autowired
    private RevenueFactorBusiness revenueFactorBusiness;
    @Autowired
    private DictionaryBusiness dictionaryBusiness;
    @Autowired
    private AffiliateRepository affiliateRepository;
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private CommissionRepository commissionRepository;
    @Autowired
    private MediaRepository mediaRepository;
    @Autowired
    private DictionaryRepository dictionaryRepository;

    /**
     * == CREATE =========================================================================================================
     **/

    // CREATE
    public TransactionCPCDTO createCpc(BaseCreateRequest request) {
        TransactionCPC newCpcTransaction = mapper.map(request, TransactionCPC.class);

        newCpcTransaction.setInitialValue(DoubleRounder.round(request.getValue(), 2));
        newCpcTransaction.setPayoutPresent(false);
        newCpcTransaction.setApproved(true);

        if (request.getManualDate() != null) {
            newCpcTransaction.setDateTime(request.getManualDate().atStartOfDay());
            request.setDictionaryId(68L);
            request.setStatusId(73L);
            BigDecimal dd = BigDecimal.valueOf(request.getValue() * request.getClickNumber());
            newCpcTransaction.setInitialValue(DoubleRounder.round(dd.doubleValue(), 2));
            newCpcTransaction.setValue(dd.doubleValue());
        }

        // trovo revenue
        RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(request.getCampaignId(), 10L);
        if (rf != null) {
            newCpcTransaction.setRevenueId(rf.getId());
        } else {
            newCpcTransaction.setRevenueId(2L);
        }

        newCpcTransaction.setCampaign(campaignRepository.findById(request.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", request.campaignId)));

        if (request.commissionId != null) try {
            newCpcTransaction.setCommission(commissionRepository.findById(request.commissionId).orElseThrow(() -> new ElementCleveradException("Commission", request.commissionId)));
        } catch (Exception ex) {
            log.error("ECCEZIONE commissionId  - " + ex.getMessage(), ex);
        }
        if (request.channelId != null) try {
            newCpcTransaction.setChannel(channelRepository.findById(request.channelId).orElseThrow(() -> new ElementCleveradException("Channel", request.channelId)));
        } catch (Exception ex) {
            log.error("ECCEZIONE commissionId  - " + ex.getMessage(), ex);
        }
        if (request.dictionaryId != null) try {
            newCpcTransaction.setDictionary(dictionaryRepository.findById(request.dictionaryId).orElseThrow(() -> new ElementCleveradException("Dictionay", request.dictionaryId)));
        } catch (Exception ex) {
            log.error("ECCEZIONE dictionaryId  - " + ex.getMessage(), ex);
        }
        if (request.statusId != null) try {
            newCpcTransaction.setDictionaryStatus(dictionaryRepository.findById(request.statusId).orElseThrow(() -> new ElementCleveradException("Status", request.statusId)));
        } catch (Exception ex) {
            log.error("ECCEZIONE statusId  - " + ex.getMessage(), ex);
        }
        if (request.mediaId != null) try {
            Media media = mediaRepository.findById(request.mediaId).orElse(null);
            if (media != null)
                newCpcTransaction.setMedia(media);
            else
                log.warn("ATTENZIONE !!! Media con id {} non trovato!!", request.mediaId);
        } catch (Exception ex) {
            log.error("ECCEZIONE MEDIA  - " + ex.getMessage(), ex);
        }

        Affiliate aa = null;
        if (request.affiliateId != null)
            aa = affiliateRepository.findById(request.affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliate", request.affiliateId));
        newCpcTransaction.setAffiliate(aa);

        Wallet ww = null;
        if (request.walletId != null) {
            ww = walletRepository.findById(request.walletId).orElseThrow(() -> new ElementCleveradException("Wallet", request.walletId));
        } else if (aa != null) {
            ww = aa.getWallets().stream().findFirst().get();
        }
        newCpcTransaction.setWallet(ww);

        return TransactionCPCDTO.from(cpcRepository.save(newCpcTransaction));
    }

    /**
     * == UPDATE =========================================================================================================
     **/

    public TransactionCPCDTO updateCPCValue(Double value, Long id) {
        TransactionCPC cpc = cpcRepository.findById(id).get();
        cpc.setValue(value);
        return TransactionCPCDTO.from(cpcRepository.save(cpc));
    }

    public TransactionCPCDTO settaScaduto(Long id) {
        TransactionCPC cpc = cpcRepository.findById(id).get();
        cpc.setValue(0D);
        cpc.setDictionaryStatus(dictionaryRepository.findById(127L).get());
        return TransactionCPCDTO.from(cpcRepository.save(cpc));
    }

    //  quando campbio stato devo ricalcolare i budget affilitato e campagna
    //  nuovi tre stati  : pending, approvato e rifutato
    public void updateStatus(Long id, Long dictionaryId, Boolean approved, Long statusId) {

        TransactionCPC cpc = cpcRepository.findById(id).get();

        if (statusId == null && cpc.getDictionaryStatus() != null) {
            statusId = cpc.getDictionaryStatus().getId();
        }

        if (dictionaryId == null && cpc.getDictionary() != null) {
            dictionaryId = cpc.getDictionary().getId();
        }

        if (dictionaryId == 40L || statusId == 74L) {
            // setto revenue e commission a 0
            cpc.setRevenueId(1L);
            cpc.setCommission(commissionRepository.findById(1L).orElseThrow(() -> new ElementCleveradException("Commission", 1L)));
            cpc.setValue(0D);
        }

        if (dictionaryId != null) {
            Long finalDictionaryId = dictionaryId;
            cpc.setDictionary(dictionaryRepository.findById(dictionaryId).orElseThrow(() -> new ElementCleveradException("Dictionay", finalDictionaryId)));
        }

        if (statusId != null) {
            Long finalStatusId = statusId;
            cpc.setDictionaryStatus(dictionaryRepository.findById(statusId).orElseThrow(() -> new ElementCleveradException("Status", finalStatusId)));
        }

        if (approved != null) cpc.setApproved(approved);

        cpc.setLastModificationDate(LocalDateTime.now());
        cpcRepository.save(cpc);
    }

    /**
     * == DELETE =========================================================================================================
     **/

    // DELETE BY ID
    public void deleteInterno(Long id) {
        try {
            cpcRepository.deleteById(id);
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    public void delete(Long id) {
        try {

            // aggiorno budge e CAP affiliato
            // aggiorno budget affiliato in modo schedulato
            // aggiorno budget campagna in modo schedualto
            // aggiorno wallet in modo schedulato
            // aggiorno campaign buget in modo schedualto

            //cancello
            cpcRepository.delete(cpcRepository.findById(id).get());
        } catch (Exception ee) {
            log.error("Errore in cancell transazione CPC :: " + ee.getMessage());
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    /**
     * == SEARCH =========================================================================================================
     **/

    // GET BY ID CPC
    public TransactionCPCDTO findByIdCPC(Long id) {
        TransactionCPC  transaction = cpcRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction", id));
        return TransactionCPCDTO.from(transaction);
    }

    // SEARCH PAGINATED
    public Page<TransactionCPCDTO> searchCpc(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<TransactionCPC> page = cpcRepository.findAll(getSpecificationCPC(request), pageable);
        return page.map(TransactionCPCDTO::from);
    }

    //SEARCH BY AFFILIATE ID
    public Page<TransactionCPCDTO> searchByAffiliateCpc(Filter request, Long id, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        if (!jwtUserDetailsService.getRole().equals("Admin")) {
            request.setAffiliateId(jwtUserDetailsService.getAffiliateId());
        }
        Page<TransactionCPC> page = cpcRepository.findAll(getSpecificationCPC(request), pageable);
        return page.map(TransactionCPCDTO::from);
    }

    public List<TransactionCPC> searchByCampaignMese(Long id) {
        TransactionCPCBusiness.Filter request = new TransactionCPCBusiness.Filter();
        request.setCampaignId(id);
        LocalDate now = LocalDate.now();
        request.setDateTimeFrom(now.withDayOfMonth(1).atStartOfDay());
        request.setDateTimeTo(LocalDateTime.of(now.withDayOfMonth(now.lengthOfMonth()), LocalTime.MAX));
        request.setValueNotZero(true);
        ArrayList not = new ArrayList<>();
        not.add(74L); // RIGETTATO
        request.setNotInStatusId(not);
        return cpcRepository.findAll(getSpecificationCPC(request), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id")))).stream().collect(Collectors.toList());
    }

    public List<TransactionCPC> searchForCampaignBudget(Long id, LocalDate from, LocalDate to) {
        TransactionCPCBusiness.Filter request = new TransactionCPCBusiness.Filter();
        request.setCampaignId(id);
        request.setDateTimeFrom(from.atStartOfDay());
        request.setDateTimeTo(LocalDateTime.of((to), LocalTime.MAX));
        request.setValueNotZero(true);
        ArrayList not = new ArrayList<>();
        not.add(74L); // RIGETTATO
        request.setNotInStatusId(not);
        return cpcRepository.findAll(getSpecificationCPC(request), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id")))).stream().collect(Collectors.toList());
    }

    public List<TransactionCPC> searchForCampaignAffiliateBudget(Long campaignId, Long affiliateId, LocalDate start, LocalDate end) {
        TransactionCPCBusiness.Filter request = new TransactionCPCBusiness.Filter();
        request.setCampaignId(campaignId);
        request.setAffiliateId(affiliateId);
        request.setDateTimeFrom(start.atStartOfDay());
        request.setDateTimeTo(LocalDateTime.of((end), LocalTime.MAX));
        request.setValueNotZero(true);
        ArrayList<Long> not = new ArrayList<>();
        not.add(74L); // RIGETTATO
        request.setNotInStatusId(not);
        return cpcRepository.findAll(getSpecificationCPC(request), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id")))).stream().collect(Collectors.toList());
    }

    public Page<DictionaryDTO> getTypes() {
        return dictionaryBusiness.getTransactionTypes();
    }

    //    >>>>>>>>> RICERCE PER RIGENERAZIONE
    public List<TransactionCPC> searchStatusIdAndDateNotManual(Long statusId, LocalDate dataDaGestireStart, LocalDate dataDaGestireEnd, Long affiliateId, Long campaignId) {
        TransactionCPCBusiness.Filter request = new TransactionCPCBusiness.Filter();
        request.setDateTimeFrom(dataDaGestireStart.atStartOfDay());
        request.setDateTimeTo(LocalDateTime.of(dataDaGestireEnd, LocalTime.MAX));
        request.setStatusId(statusId);
        List<Long> not = new ArrayList<>();
        not.add(68L);
        request.setNotInId(not);
        request.setAffiliateId(affiliateId);
        request.setCampaignId(campaignId);
        return cpcRepository.findAll(getSpecificationCPC(request), Pageable.ofSize(Integer.MAX_VALUE)).stream().collect(Collectors.toList());
    }

    public List<TransactionCPC> searchStatusIdAndDicIdAndDate(Long statusId, Long dicId, LocalDate dataDaGestireStart, LocalDate dataDaGestireEnd, Long affiliateId, Long campaignId) {
        TransactionCPCBusiness.Filter request = new TransactionCPCBusiness.Filter();
        request.setDateTimeFrom(dataDaGestireStart.atStartOfDay());
        request.setDateTimeTo(LocalDateTime.of(dataDaGestireEnd, LocalTime.MAX));
        request.setStatusId(statusId);
        request.setDictionaryId(dicId);
        request.setAffiliateId(affiliateId);
        request.setCampaignId(campaignId);
        return cpcRepository.findAll(getSpecificationCPC(request), Pageable.ofSize(Integer.MAX_VALUE)).stream().collect(Collectors.toList());
    }

    //    -- ---- ---- ---- ---- RIGENERA WALLET
    public List<TransactionCPC> searchPayout(Long affiliateId, Boolean payoutPresent) {
        TransactionCPCBusiness.Filter request = new TransactionCPCBusiness.Filter();
        request.setAffiliateId(affiliateId);
        request.setPayoutPresent(payoutPresent);
        request.setValueNotZero(true);
        return cpcRepository.findAll(getSpecificationCPC(request), Pageable.ofSize(Integer.MAX_VALUE)).stream().collect(Collectors.toList());
    }

    //    -- ---- ---- ---- ---- RIGENERA AFFILIATE BUDGET
    public List<TransactionCPC> searchLastModified() {
        TransactionCPCBusiness.Filter request = new TransactionCPCBusiness.Filter();
        request.setValueNotZero(true);
        ArrayList<Long> not = new ArrayList<>();
        not.add(74L); // RIGETTATO
        request.setNotInStatusId(not);
        request.setLastModificationDateTimeFrom(LocalDateTime.now().minusHours(24));
        return cpcRepository.findAll(getSpecificationCPC(request), Pageable.ofSize(Integer.MAX_VALUE)).stream().collect(Collectors.toList());
    }

    /**
     * ============================================================================================================
     **/

    private Specification<TransactionCPC> getSpecificationCPC(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getApproved() != null) {
                predicates.add(cb.equal(root.get("approved"), request.getApproved()));
            }
            if (request.getIp() != null) {
                predicates.add(cb.equal(root.get("ip"), request.getIp()));
            }
            if (request.getAgent() != null) {
                predicates.add(cb.equal(root.get("agent"), request.getAgent()));
            }
            if (request.getAffiliateId() != null) {
                predicates.add(cb.equal(root.get("affiliate").get("id"), request.getAffiliateId()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaign").get("id"), request.getCampaignId()));
            }
            if (request.getCommissionId() != null) {
                predicates.add(cb.equal(root.get("commission").get("id"), request.getCommissionId()));
            }
            if (request.getChannelId() != null) {
                predicates.add(cb.equal(root.get("channel").get("id"), request.getChannelId()));
            }
            if (request.getWalletId() != null) {
                predicates.add(cb.equal(root.get("wallet").get("id"), request.getWalletId()));
            }
            if (request.getDateTimeFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateTime"), request.getDateTimeFrom()));
            }
            if (request.getDateTimeTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateTime"), request.getDateTimeTo()));
            }
            if (request.getDictionaryId() != null) {
                predicates.add(cb.equal(root.get("dictionary").get("id"), request.getDictionaryId()));
            }
            if (request.getNotInId() != null) {
                CriteriaBuilder.In<Long> inClauseNot = cb.in(root.get("dictionary").get("id"));
                for (Long id : request.getNotInId()) {
                    inClauseNot.value(id);
                }
                predicates.add(inClauseNot.not());
            }
            if (request.getValueNotZero() != null && request.getValueNotZero()) {
                predicates.add(cb.notEqual(root.get("value"), "0"));
            }
            if (request.getBlacklisted() != null) {
                predicates.add(cb.equal(root.get("blacklisted"), request.getBlacklisted()));
            }
            if (request.getPayoutPresent() != null) {
                predicates.add(cb.equal(root.get("payoutPresent"), request.getPayoutPresent()));
            }
            if (request.getLastModificationDateTimeFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("lastModificationDate"), request.getLastModificationDateTimeFrom()));
            }
            if (request.getLastModificationDateTimeTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("lastModificationDate"), request.getLastModificationDateTimeTo()));
            }

            if (request.getNotInStatusId() != null) {
                CriteriaBuilder.In<Long> inClauseNot = cb.in(root.get("dictionaryStatus").get("id"));
                for (Long id : request.getNotInStatusId()) {
                    inClauseNot.value(id);
                }
                predicates.add(inClauseNot.not());
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
        private Long affiliateId;
        private Long campaignId;
        private Long commissionId;
        private Long channelId;
        private Long walletId;
        private Long mediaId;
        private LocalDateTime dateTime;
        private Double value;
        private Boolean approved;
        private String ip;
        private String agent;
        private String refferal;
        private String data;
        private String payoutId;
        private String note;
        private Long dictionaryId;
        private Long total;
        private Long impressionNumber;
        private Long leadNumber;
        private Long clickNumber;
        private Long revenueId;
        private Boolean payoutPresent;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate manualDate;
        private Long statusId;
        private Double initialValue;
        private Long cpcId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Filter {
        private List<Long> notInId;
        private List<Long> statusIdIn;
        private List<Long> dictionaryIdIn;
        private List<Long> notInStatusId;
        private Long id;
        private Long affiliateId;
        private Long campaignId;
        private Long commissionId;
        private Long channelId;
        private Long walletId;
        private String ip;
        private String agent;
        private LocalDateTime dateTime;
        private Double value;
        private Boolean approved;
        private String payoutId;
        private String note;
        private Long dictionaryId;
        private Long impressionNumber;
        private Long leadNumber;
        private Long clickNumber;
        private Long revenueId;
        private Long statusId;
        private Double initialValue;
        private Boolean blacklisted;
        private Boolean valueNotZero;
        private Boolean payoutPresent;
        private Boolean phoneVerifiedNull;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime dateTimeFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime dateTimeTo;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime lastModificationDateTimeFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime lastModificationDateTimeTo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class FilterUpdate {
        private Long id;
        private Long dictionaryId;
        private Boolean approved;
        private String tipo;
        private Long statusId;
    }

}