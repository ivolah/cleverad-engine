package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.Affiliate;
import it.cleverad.engine.persistence.model.service.RevenueFactor;
import it.cleverad.engine.persistence.model.service.TransactionCPL;
import it.cleverad.engine.persistence.model.service.Wallet;
import it.cleverad.engine.persistence.repository.service.*;
import it.cleverad.engine.web.dto.AffiliateBudgetDTO;
import it.cleverad.engine.web.dto.DictionaryDTO;
import it.cleverad.engine.web.dto.TransactionCPLDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

import javax.persistence.criteria.CriteriaBuilder;
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
public class TransactionCPLBusiness {

    @Autowired
    CampaignBudgetBusiness campaignBudgetBusiness;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private TransactionCPLRepository cplRepository;
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
    private WalletBusiness walletBusiness;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private CommissionRepository commissionRepository;
    @Autowired
    private MediaRepository mediaRepository;
    @Autowired
    private DictionaryRepository dictionaryRepository;
    @Autowired
    private AffiliateBudgetBusiness affiliateBudgetBusiness;
    @Autowired
    private CampaignBusiness campaignBusiness;

    /**
     * == CREATE =========================================================================================================
     **/

    public TransactionCPLDTO createCpl(BaseCreateRequest request) {
        TransactionCPL newCplTransaction = mapper.map(request, TransactionCPL.class);

        newCplTransaction.setPayoutPresent(false);
        newCplTransaction.setApproved(true);
        newCplTransaction.setPhoneVerified(false);
        newCplTransaction.setInitialValue(request.getValue());

        if (request.getManualDate() != null) {
            newCplTransaction.setDateTime(request.getManualDate().atStartOfDay());
            newCplTransaction.setLeadNumber(1L);
            request.setDictionaryId(68L);
            request.setStatusId(73L);
            newCplTransaction.setData(request.getData().trim());

            // trovo revenue
            RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(request.getCampaignId(), 11L);
            if (rf != null) {
                newCplTransaction.setRevenueId(rf.getId());
            } else {
                newCplTransaction.setRevenueId(2L);
            }
        }

        newCplTransaction.setCampaign(campaignRepository.findById(request.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", request.campaignId)));

        if (request.commissionId != null)
            newCplTransaction.setCommission(commissionRepository.findById(request.commissionId).orElseThrow(() -> new ElementCleveradException("Commission", request.commissionId)));
        if (request.channelId != null)
            newCplTransaction.setChannel(channelRepository.findById(request.channelId).orElseThrow(() -> new ElementCleveradException("Channel", request.channelId)));
        if (request.dictionaryId != null)
            newCplTransaction.setDictionary(dictionaryRepository.findById(request.dictionaryId).orElseThrow(() -> new ElementCleveradException("Dictionay", request.dictionaryId)));
        if (request.statusId != null)
            newCplTransaction.setDictionaryStatus(dictionaryRepository.findById(request.statusId).orElseThrow(() -> new ElementCleveradException("Status", request.statusId)));
        if (request.mediaId != null)
            newCplTransaction.setMedia(mediaRepository.findById(request.mediaId).orElseThrow(() -> new ElementCleveradException("Media", request.mediaId)));

        Affiliate aa = null;
        if (request.affiliateId != null) {
            aa = affiliateRepository.findById(request.affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliate", request.affiliateId));
        }
        newCplTransaction.setAffiliate(aa);

        Wallet ww = null;
        if (request.walletId != null) {
            ww = walletRepository.findById(request.walletId).orElseThrow(() -> new ElementCleveradException("Wallet", request.walletId));
        } else if (aa != null) {
            ww = aa.getWallets().stream().findFirst().get();
        }
        newCplTransaction.setWallet(ww);

        return TransactionCPLDTO.from(cplRepository.save(newCplTransaction));
    }

    /**
     * == UPDATE =========================================================================================================
     **/

    public TransactionCPLDTO updateCPLValue(Double value, Long id) {
        TransactionCPL cpl = cplRepository.findById(id).get();
        cpl.setValue(value);
        return TransactionCPLDTO.from(cplRepository.save(cpl));
    }

    //  quando campbio stato devo ricalcolare i budget affilitato e campagna
    //  nuovi tre stati  : pending, approvato e rifutato
    public void updateStatus(Long id, Long dictionaryId, Boolean approved, Long statusId) {

        TransactionCPL cpl = cplRepository.findById(id).get();
        if (statusId == null && cpl.getDictionaryStatus() != null) statusId = cpl.getDictionaryStatus().getId();
        if (dictionaryId == null && cpl.getDictionary() != null) dictionaryId = cpl.getDictionary().getId();

        if (statusId == 74L || dictionaryId == 40L) {

            // aggiorno budget affiliato
            AffiliateBudgetDTO budgetAff = affiliateBudgetBusiness.getByIdCampaignAndIdAffiliate(cpl.getCampaign().getId(), cpl.getAffiliate().getId()).stream().findFirst().orElse(null);
            if (budgetAff != null) {
                affiliateBudgetBusiness.updateBudget(budgetAff.getId(), budgetAff.getBudget() + cpl.getValue());
                affiliateBudgetBusiness.updateCap(budgetAff.getId(), budgetAff.getCap() + 1);
            }

            // aggiorno budget campagna
            campaignBusiness.updateBudget(cpl.getCampaign().getId(), campaignBusiness.findById(cpl.getCampaign().getId()).getBudget() + cpl.getValue());

            // aggiorno wallet
            Long walletID = null;
            if (cpl.getAffiliate().getId() != null) {
                walletID = walletRepository.findByAffiliateId(cpl.getAffiliate().getId()).getId();
                walletBusiness.decrement(walletID, cpl.getValue());
            }

            //aggiorno Camapign Budget
//                if (cpl.getValue() > 0D) {
//                    CampaignBudget cb = campaignBudgetBusiness.findByCampaignIdAndDate(cpl.getCampaign().getId(), cpl.getDateTime());
//                    if (cb != null) {
//                        campaignBudgetBusiness.decreaseCapErogatoOnDeleteTransaction(cb.getId(), Math.toIntExact(cb.getCapErogato() - 1));
//                        campaignBudgetBusiness.decreaseBudgetErogatoOnDeleteTransaction(cb.getId(), cb.getBudgetErogato() - cpl.getValue());
//                    }
//                }

        } else if (dictionaryId == 40L || statusId == 74L) {
            // setto revenue e commission a 0
            cpl.setRevenueId(1L);
            cpl.setCommission(commissionRepository.findById(1L).orElseThrow(() -> new ElementCleveradException("Commission", 1L)));
            cpl.setValue(0D);
        }
        if (dictionaryId != null) {
            Long finalDictionaryId1 = dictionaryId;
            cpl.setDictionary(dictionaryRepository.findById(dictionaryId).orElseThrow(() -> new ElementCleveradException("Dictionay", finalDictionaryId1)));
        } else dictionaryId = 0L;
        if (statusId != null) {
            Long finalStatusId1 = statusId;
            log.info("SETTO STATUS :: {}", statusId);
            cpl.setDictionaryStatus(dictionaryRepository.findById(statusId).orElseThrow(() -> new ElementCleveradException("Status", finalStatusId1)));
        } else statusId = 0L;
        if (approved != null) cpl.setApproved(approved);

        cplRepository.save(cpl);
    }

    public void updatePhoneStatus(Long id, String number, Boolean verified) {

        TransactionCPL cpl = cplRepository.findById(id).get();
        cpl.setPhoneVerified(verified);
        cpl.setPhoneNumber(number);
        cplRepository.save(cpl);

        // Setto a rigettato  se stato false e numero non nullo
        if (!verified && StringUtils.isNotBlank(number)) {

            // aggiorno budget affiliato
            AffiliateBudgetDTO budgetAff = affiliateBudgetBusiness.getByIdCampaignAndIdAffiliate(cpl.getCampaign().getId(), cpl.getAffiliate().getId()).stream().findFirst().orElse(null);
            if (budgetAff != null) {
                affiliateBudgetBusiness.updateBudget(budgetAff.getId(), budgetAff.getBudget() + cpl.getValue());
                affiliateBudgetBusiness.updateCap(budgetAff.getId(), budgetAff.getCap() + 1);
            }

            // aggiorno budget campagna
            campaignBusiness.updateBudget(cpl.getCampaign().getId(), campaignBusiness.findById(cpl.getCampaign().getId()).getBudget() + cpl.getValue());

            // aggiorno wallet
            Long walletID = null;
            if (cpl.getAffiliate().getId() != null) {
                walletID = walletRepository.findByAffiliateId(cpl.getAffiliate().getId()).getId();
                walletBusiness.decrement(walletID, cpl.getValue());
            }

            //aggiorno Camapign Budget
//            if (cpl.getValue() > 0D) {
//                CampaignBudget cb = campaignBudgetBusiness.findByCampaignIdAndDate(cpl.getCampaign().getId(), cpl.getDateTime());
//                if (cb != null) {
//                    campaignBudgetBusiness.decreaseCapErogatoOnDeleteTransaction(cb.getId(), Math.toIntExact(cb.getCapErogato() - 1));
//                    campaignBudgetBusiness.decreaseBudgetErogatoOnDeleteTransaction(cb.getId(), cb.getBudgetErogato() - cpl.getValue());
//                }
//            }
        }

        cplRepository.save(cpl);
    }

    public TransactionCPLDTO updateCPLPhoneNumber(String number, Long id) {
        TransactionCPL cpl = cplRepository.findById(id).get();
        cpl.setPhoneNumber(number);
        return TransactionCPLDTO.from(cplRepository.save(cpl));
    }

    public TransactionCPLDTO updateCPLPhoneStatus(Boolean status, Long id) {
        TransactionCPL cpl = cplRepository.findById(id).get();
        cpl.setPhoneVerified(status);
        return TransactionCPLDTO.from(cplRepository.save(cpl));
    }

    // GET BY ID CPL
    public TransactionCPLDTO findByIdCPL(Long id) {
        TransactionCPL transaction = null;
        if (jwtUserDetailsService.getRole().equals("Admin")) {
            transaction = cplRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction", id));
        } else {
            CampaignBusiness.Filter request = new CampaignBusiness.Filter();
            request.setId(id);
            transaction = cplRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction", id));
        }
        return TransactionCPLDTO.from(transaction);
    }

    /**
     * == DELETE =========================================================================================================
     **/

    // DELETE BY ID
    public void deleteInterno(Long id) {
        try {
            cplRepository.deleteById(id);
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    public void delete(Long id) {
        try {
            TransactionCPLDTO dto = this.findByIdCPL(id);

            // aggiorno budget affiliato
            AffiliateBudgetDTO budgetAff = affiliateBudgetBusiness.getByIdCampaignAndIdAffiliate(dto.getCampaignId(), dto.getAffiliateId()).stream().findFirst().orElse(null);
            if (budgetAff != null) {
                affiliateBudgetBusiness.updateBudget(budgetAff.getId(), budgetAff.getBudget() + dto.getValue());
                affiliateBudgetBusiness.updateCap(budgetAff.getId(), budgetAff.getCap() + 1);
            }

            // aggiorno budget campagna
            // - non serve più  abbiamo campagin budget :
            //campaignBusiness.updateBudget(dto.getCampaignId(), campaignBusiness.findById(dto.getCampaignId()).getBudget() + dto.getValue());

            // aggiorno wallet in modo schedulato
            // aggiorno campaign buget in modo schedualto

            cplRepository.delete(cplRepository.findById(id).get());

        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    /**
     * == SEARCH =========================================================================================================
     **/

    // SEARCH PAGINATED
    public Page<TransactionCPLDTO> searchCpl(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        return cplRepository.findAll(getSpecificationCPL(request), pageable).map(TransactionCPLDTO::from);
    }

    //SEARCH BY AFFILIATE ID
    public Page<TransactionCPLDTO> searchByAffiliateCpl(Filter request, Long id, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        if (!jwtUserDetailsService.getRole().equals("Admin")) {
            //      request.setApproved(true);
            request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        }
        Page<TransactionCPL> page = cplRepository.findAll(getSpecificationCPL(request), pageable);
        return page.map(TransactionCPLDTO::from);
    }

    public Page<TransactionCPLDTO> searchByCampaign(Long id, Pageable pageableRequest) {
        TransactionCPLBusiness.Filter request = new TransactionCPLBusiness.Filter();
        request.setCampaignId(id);
        if (!jwtUserDetailsService.getRole().equals("Admin")) {
            request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        }
        Page<TransactionCPL> page = cplRepository.findAll(getSpecificationCPL(request), PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id"))));
        return page.map(TransactionCPLDTO::from);
    }

    public List<TransactionCPL> searchByCampaignMese(Long id) {
        TransactionCPLBusiness.Filter request = new TransactionCPLBusiness.Filter();
        request.setCampaignId(id);
        LocalDate now = LocalDate.now();
        request.setDateTimeFrom(now.withDayOfMonth(1).atStartOfDay());
        request.setDateTimeTo(LocalDateTime.of(now.withDayOfMonth(now.lengthOfMonth()), LocalTime.MAX));
        return cplRepository.findAll(getSpecificationCPL(request), Pageable.ofSize(Integer.MAX_VALUE)).stream().collect(Collectors.toList());
    }

    //    -- ---- ---- ---- ---- RIGENERA WALLET
    public List<TransactionCPL> searchPayout(Long affiliateId, Boolean payoutPresent) {
        TransactionCPLBusiness.Filter request = new TransactionCPLBusiness.Filter();
        request.setAffiliateId(affiliateId);
        request.setPayoutPresent(payoutPresent);
        request.setValueNotZero(true);
        return cplRepository.findAll(getSpecificationCPL(request), Pageable.ofSize(Integer.MAX_VALUE)).stream().collect(Collectors.toList());
    }

    /**
     * ============================================================================================================
     **/

    public Page<DictionaryDTO> getTypes() {
        return dictionaryBusiness.getTransactionTypes();
    }

    /**
     * ============================================================================================================
     **/

    private Specification<TransactionCPL> getSpecificationCPL(Filter request) {
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
            if (request.getNotInId() != null) {
                CriteriaBuilder.In<Long> inClauseNot = cb.in(root.get("dictionary").get("id"));
                for (Long id : request.getNotInId()) {
                    inClauseNot.value(id);
                }
                predicates.add(inClauseNot.not());
            }
            if (request.getPayoutPresent() != null && request.getPayoutPresent()) {
                predicates.add(cb.equal(root.get("payoutPresent"), request.getPayoutPresent()));
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
        private Long statusId;
        private Double initialValue;
        private Boolean blacklisted;
        private Boolean valueNotZero;
        private Boolean phoneVerifiedNull;
        private Boolean payoutPresent;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime dateTimeFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime dateTimeTo;
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