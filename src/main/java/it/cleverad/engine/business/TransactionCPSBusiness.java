package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.Affiliate;
import it.cleverad.engine.persistence.model.service.TransactionCPS;
import it.cleverad.engine.persistence.model.service.Wallet;
import it.cleverad.engine.persistence.repository.service.*;
import it.cleverad.engine.web.dto.DictionaryDTO;
import it.cleverad.engine.web.dto.TransactionCPSDTO;
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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class TransactionCPSBusiness {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private TransactionCPSRepository cpsRepository;
    @Autowired
    private Mapper mapper;
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


    public TransactionCPSDTO createCps(BaseCreateRequest request) {
        TransactionCPS map = mapper.map(request, TransactionCPS.class);

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

        return TransactionCPSDTO.from(cpsRepository.save(map));
    }

    //  quando campbio stato devo ricalcolare i budget affilitato e campagna
    //  nuovi tre stati  : pending, approvato e rifutato

    public void delete(Long id) {
        try {
            // aggiorno budget affiliato in modo schedualto
            // aggiorno budget campagna in modo schedualto
            // aggiorno wallet in modo schedulato
            // aggiorno campaign buget in modo schedualto

            //cancello
            cpsRepository.deleteById(id);
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    public TransactionCPSDTO settaScaduto(Long id) {
        TransactionCPS cpc = cpsRepository.findById(id).get();
        cpc.setValue(0D);
        cpc.setDictionaryStatus(dictionaryRepository.findById(127L).get());
        return TransactionCPSDTO.from(cpsRepository.save(cpc));
    }


    public Page<TransactionCPSDTO> searchByAffiliateCps(Filter request, Long id, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        if (jwtUserDetailsService.getRole().equals("Admin")) {
        } else {
            request.setAffiliateId(jwtUserDetailsService.getAffiliateId());
        }
        Page<TransactionCPS> page = cpsRepository.findAll(getSpecificationCPS(request), pageable);
        return page.map(TransactionCPSDTO::from);
    }

    public Page<DictionaryDTO> getTypes() {
        return dictionaryBusiness.getTransactionTypes();
    }

    /**
     * ============================================================================================================
     **/

    private Specification<TransactionCPS> getSpecificationCPS(Filter request) {
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
                predicates.add(cb.greaterThanOrEqualTo(root.get("date_time"), request.getDateTimeFrom()));
            }
            if (request.getDateTimeTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date_time"), request.getDateTimeTo()));
            }

            if (request.getDictionaryId() != null) {
                predicates.add(cb.equal(root.get("dictionary").get("id"), request.getDictionaryId()));
            }

            if (request.getBlacklisted() != null) {
                predicates.add(cb.equal(root.get("blacklisted"), request.getBlacklisted()));
            }

            if (request.getPhoneVerifiedNull() != null) {
                predicates.add(cb.isNull(root.get("phoneVerified")));
            }

            if (request.getDictionaryIdIn() != null) {
                CriteriaBuilder.In<Long> inClause = cb.in(root.get("dictionaryId"));
                for (Long id : request.getDictionaryIdIn()) {
                    inClause.value(id);
                }
                predicates.add(inClause);
            }

            if (request.getStatusIdIn() != null) {
                CriteriaBuilder.In<Long> inClause = cb.in(root.get("statusId"));
                for (Long id : request.getStatusIdIn()) {
                    inClause.value(id);
                }
                predicates.add(inClause);
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