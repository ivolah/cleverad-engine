package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.Affiliate;
import it.cleverad.engine.persistence.model.service.RevenueFactor;
import it.cleverad.engine.persistence.model.service.TransactionCPM;
import it.cleverad.engine.persistence.model.service.Wallet;
import it.cleverad.engine.persistence.repository.service.*;
import it.cleverad.engine.web.dto.DictionaryDTO;
import it.cleverad.engine.web.dto.TransactionCPMDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class TransactionCPMBusiness {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private TransactionCPMRepository cpmRepository;
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
     * ============================================================================================================
     **/

    public TransactionCPMDTO createCpm(BaseCreateRequest request) {
        TransactionCPM map = mapper.map(request, TransactionCPM.class);
        if (request.getManualDate() != null) {
            map.setDateTime(request.getManualDate().atStartOfDay());
            request.setDictionaryId(68L);
            request.setStatusId(73L);
            request.setImpressionNumber(1L);
            // trovo revenue
            RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(request.getCampaignId(), 50L);
            if (rf != null) {
                map.setRevenueId(rf.getId());
            } else {
                map.setRevenueId(2L);
            }
        }

        map.setCampaign(campaignRepository.findById(request.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", request.campaignId)));

        Affiliate aa = null;
        if (request.affiliateId != null) {
            aa = affiliateRepository.findById(request.affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliate", request.affiliateId));
            map.setAffiliate(aa);
        }
        if (request.commissionId != null)
            map.setCommission(commissionRepository.findById(request.commissionId).orElseThrow(() -> new ElementCleveradException("Commission", request.commissionId)));
        if (request.channelId != null)
            map.setChannel(channelRepository.findById(request.channelId).orElseThrow(() -> new ElementCleveradException("Channel", request.channelId)));
        if (request.dictionaryId != null)
            map.setDictionary(dictionaryRepository.findById(request.dictionaryId).orElseThrow(() -> new ElementCleveradException("Dictionay", request.dictionaryId)));
        if (request.statusId != null)
            map.setDictionaryStatus(dictionaryRepository.findById(request.statusId).orElseThrow(() -> new ElementCleveradException("Status", request.statusId)));

        Wallet ww = null;
        if (request.walletId != null) {
            ww = walletRepository.findById(request.walletId).orElseThrow(() -> new ElementCleveradException("Wallet", request.walletId));
        } else if (aa != null) {
            ww = aa.getWallets().stream().findFirst().get();
        }
        map.setWallet(ww);
        if (request.mediaId != null)
            map.setMedia(mediaRepository.findById(request.mediaId).orElseThrow(() -> new ElementCleveradException("Media", request.mediaId)));
        map.setPayoutPresent(false);

        map.setNote(request.getData());

        map.setApproved(true);
        return TransactionCPMDTO.from(cpmRepository.save(map));
    }

    public TransactionCPMDTO updateCPMValue(Double value, Long id) {
        TransactionCPM cpm = cpmRepository.findById(id).get();
        cpm.setValue(value);
        return TransactionCPMDTO.from(cpmRepository.save(cpm));
    }

    //  quando campbio stato devo ricalcolare i budget affilitato e campagna
    //  nuovi tre stati  : pending, approvato e rifutato

    public void updateStatus(Long id, Long dictionaryId, Boolean approved, Long statusId) {
        TransactionCPM cpm = cpmRepository.findById(id).get();
        if (dictionaryId != null) {
            Long finalDictionaryId2 = dictionaryId;
            cpm.setDictionary(dictionaryRepository.findById(dictionaryId).orElseThrow(() -> new ElementCleveradException("Dictionay", finalDictionaryId2)));
        } else dictionaryId = 0L;
        if (statusId != null) {
            Long finalStatusId2 = statusId;
            cpm.setDictionaryStatus(dictionaryRepository.findById(statusId).orElseThrow(() -> new ElementCleveradException("Status", finalStatusId2)));
        } else statusId = 0L;
        if (approved != null) cpm.setApproved(approved);
        if (dictionaryId == 40L || statusId == 74L) {
            // setto revenue e commission a 0
            cpm.setRevenueId(1L);
            cpm.setCommission(commissionRepository.findById(1L).orElseThrow(() -> new ElementCleveradException("Commission", 1L)));
            cpm.setValue(0D);
        }
        cpm.setLastModificationDate(LocalDateTime.now());
        cpmRepository.save(cpm);
    }


    // GET BY ID CPM
    public TransactionCPMDTO findByIdCPM(Long id) {
        TransactionCPM transaction = null;
        if (jwtUserDetailsService.getRole().equals("Admin")) {
            transaction = cpmRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction", id));
        } else {
            CampaignBusiness.Filter request = new CampaignBusiness.Filter();
            request.setId(id);
            transaction = cpmRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction", id));
        }
        return TransactionCPMDTO.from(transaction);
    }

    // DELETE BY ID
    public void deleteInterno(Long id) {
        try {
            cpmRepository.deleteById(id);
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    public void delete(Long id) {
        try {

            // aggiorno budget affiliato in modo schedulato
            // aggiorno budget campagna in modo schedualto
            // aggiorno wallet in modo schedulato
            // aggiorno campaign buget in modo schedualto

            //cancello
            cpmRepository.deleteById(id);
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // SEARCH PAGINATED
    public Page<TransactionCPMDTO> searchCpm(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<TransactionCPM> page = cpmRepository.findAll(getSpecificationCPM(request), pageable);
        return page.map(TransactionCPMDTO::from);
    }

    public Page<TransactionCPMDTO> searchByAffiliateCpm(Filter request, Long id, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        if (jwtUserDetailsService.getRole().equals("Admin")) {
        } else {
            //    request.setApproved(true);
            request.setAffiliateId(jwtUserDetailsService.getAffiliateId());
        }
        Page<TransactionCPM> page = cpmRepository.findAll(getSpecificationCPM(request), pageable);
        return page.map(TransactionCPMDTO::from);
    }

    public Page<DictionaryDTO> getTypes() {
        return dictionaryBusiness.getTransactionTypes();
    }
    
    
    //    >>>>>>>>> RICERCE PER RIGENERAZIONE
    public List<TransactionCPM> searchStatusIdAndDateNotManual(Long statusId, LocalDate dataDaGestireStart, LocalDate dataDaGestireEnd, Long affiliateId, Long campaignId) {
        TransactionCPMBusiness.Filter request = new TransactionCPMBusiness.Filter();
        request.setDateTimeFrom(dataDaGestireStart.atStartOfDay());
        request.setDateTimeTo(LocalDateTime.of(dataDaGestireEnd, LocalTime.MAX));
        request.setStatusId(statusId);
        List<Long> not = new ArrayList<>();
        not.add(68L);
        request.setNotInId(not);
        request.setAffiliateId(affiliateId);
        request.setCampaignId(campaignId);
        return cpmRepository.findAll(getSpecificationCPM(request), Pageable.ofSize(Integer.MAX_VALUE)).stream().collect(Collectors.toList());
    }

    public List<TransactionCPM> searchStatusIdAndDicIdAndDate(Long statusId, Long dicId, LocalDate dataDaGestireStart, LocalDate dataDaGestireEnd, Long affiliateId, Long campaignId) {
        TransactionCPMBusiness.Filter request = new TransactionCPMBusiness.Filter();
        request.setDateTimeFrom(dataDaGestireStart.atStartOfDay());
        request.setDateTimeTo(LocalDateTime.of(dataDaGestireEnd, LocalTime.MAX));
        request.setStatusId(statusId);
        request.setDictionaryId(dicId);
        request.setAffiliateId(affiliateId);
        request.setCampaignId(campaignId);
        return cpmRepository.findAll(getSpecificationCPM(request), Pageable.ofSize(Integer.MAX_VALUE)).stream().collect(Collectors.toList());
    }

    public List<TransactionCPM> searchForCampaignAffiliateBudget(Long campaignId, Long affiliateId, LocalDate start, LocalDate end) {
        TransactionCPMBusiness.Filter request = new TransactionCPMBusiness.Filter();
        request.setCampaignId(campaignId);
        request.setAffiliateId(affiliateId);
        request.setDateTimeFrom(start.atStartOfDay());
        request.setDateTimeTo(LocalDateTime.of((end), LocalTime.MAX));
        return cpmRepository.findAll(getSpecificationCPM(request), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id")))).stream().collect(Collectors.toList());

    }
    /**
     * ============================================================================================================
     **/

    private Specification<TransactionCPM> getSpecificationCPM(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
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
        public List<Long> notInId;
        public List<Long> statusIdIn;
        public List<Long> dictionaryIdIn;
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
        private LocalDateTime dateTimeFrom;
        private LocalDateTime dateTimeTo;
        private Long statusId;
        private Double initialValue;
        private Boolean blacklisted;
        private Boolean valueNotZero;
        private Boolean phoneVerifiedNull;
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