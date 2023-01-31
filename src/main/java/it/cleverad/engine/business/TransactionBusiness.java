package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.*;
import it.cleverad.engine.persistence.repository.*;
import it.cleverad.engine.service.JwtUserDetailsService;
import it.cleverad.engine.web.dto.TransactionCPCDTO;
import it.cleverad.engine.web.dto.TransactionCPLDTO;
import it.cleverad.engine.web.dto.TransactionCPMDTO;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class TransactionBusiness {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private TransactionCPCRepository cpcRepository;
    @Autowired
    private TransactionCPLRepository cplRepository;
    @Autowired
    private TransactionCPMRepository cpmRepository;

    @Autowired
    private Mapper mapper;

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

    /**
     * ============================================================================================================
     **/

    // CREATE
    public TransactionCPCDTO createCpc(BaseCreateRequest request) {
        TransactionCPC map = mapper.map(request, TransactionCPC.class);
        Affiliate aa = affiliateRepository.findById(request.affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliate", request.affiliateId));
        map.setAffiliate(aa);
        map.setCampaign(campaignRepository.findById(request.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", request.campaignId)));
        map.setChannel(channelRepository.findById(request.channelId).orElseThrow(() -> new ElementCleveradException("Channel", request.channelId)));
        map.setCommission(commissionRepository.findById(request.commissionId).orElseThrow(() -> new ElementCleveradException("Commission", request.commissionId)));
        Wallet ww;
        if (request.walletId != null) {
            ww = walletRepository.findById(request.walletId).orElseThrow(() -> new ElementCleveradException("Wallet", request.walletId));
        } else {
            ww = aa.getWallets().stream().findFirst().get();
        }
        map.setWallet(ww);
        map.setMedia(mediaRepository.findById(request.mediaId).orElseThrow(() -> new ElementCleveradException("Media", request.mediaId)));

        return TransactionCPCDTO.from(cpcRepository.save(map));
    }

    public TransactionCPLDTO createCpl(BaseCreateRequest request) {
        TransactionCPL map = mapper.map(request, TransactionCPL.class);
        Affiliate aa = affiliateRepository.findById(request.affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliate", request.affiliateId));
        map.setAffiliate(aa);
        map.setAffiliate(affiliateRepository.findById(request.affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliate", request.affiliateId)));
        map.setCampaign(campaignRepository.findById(request.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", request.campaignId)));
        map.setChannel(channelRepository.findById(request.channelId).orElseThrow(() -> new ElementCleveradException("Channel", request.channelId)));
        map.setCommission(commissionRepository.findById(request.commissionId).orElseThrow(() -> new ElementCleveradException("Commission", request.commissionId)));
        Wallet ww;
        if (request.walletId != null) {
            ww = walletRepository.findById(request.walletId).orElseThrow(() -> new ElementCleveradException("Wallet", request.walletId));
        } else {
            ww = aa.getWallets().stream().findFirst().get();
        }
        map.setWallet(ww);

        //map.setMedia(mediaRepository.findById(request.mediaId).orElseThrow(() -> new ElementCleveradException("Media", request.mediaId)));

        return TransactionCPLDTO.from(cplRepository.save(map));
    }

    public TransactionCPMDTO createCpm(BaseCreateRequest request) {
        TransactionCPM map = mapper.map(request, TransactionCPM.class);

        Affiliate aa = affiliateRepository.findById(request.affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliate", request.affiliateId));
        map.setAffiliate(aa);
        map.setCampaign(campaignRepository.findById(request.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", request.campaignId)));
        map.setChannel(channelRepository.findById(request.channelId).orElseThrow(() -> new ElementCleveradException("Channel", request.channelId)));
        map.setCommission(commissionRepository.findById(request.commissionId).orElseThrow(() -> new ElementCleveradException("Commission", request.commissionId)));
        Wallet ww;
        if (request.walletId != null) {
            ww = walletRepository.findById(request.walletId).orElseThrow(() -> new ElementCleveradException("Wallet", request.walletId));
        } else {
            ww = aa.getWallets().stream().findFirst().get();
        }
        map.setWallet(ww);
        map.setMedia(mediaRepository.findById(request.mediaId).orElseThrow(() -> new ElementCleveradException("Media", request.mediaId)));

        return TransactionCPMDTO.from(cpmRepository.save(map));
    }


    // GET BY ID CPC
    public TransactionCPCDTO findByIdCPC(Long id) {
        TransactionCPC transaction = null;
        if (jwtUserDetailsService.getRole().equals("Admin")) {
            transaction = cpcRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction", id));
        } else {
            CampaignBusiness.Filter request = new CampaignBusiness.Filter();
            request.setId(id);
            // TODO logica per seach di quelli assegnati
            transaction = cpcRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction", id));
        }
        return TransactionCPCDTO.from(transaction);
    }

    // GET BY ID CPL
    public TransactionCPLDTO findByIdCPL(Long id) {
        TransactionCPL transaction = null;
        if (jwtUserDetailsService.getRole().equals("Admin")) {
            transaction = cplRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction", id));
        } else {
            CampaignBusiness.Filter request = new CampaignBusiness.Filter();
            request.setId(id);
            // TODO logica per seach di quelli assegnati
            transaction = cplRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction", id));
        }
        return TransactionCPLDTO.from(transaction);
    }

    // GET BY ID CPM
    public TransactionCPMDTO findByIdCPM(Long id) {
        TransactionCPM transaction = null;
        if (jwtUserDetailsService.getRole().equals("Admin")) {
            transaction = cpmRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction", id));
        } else {
            CampaignBusiness.Filter request = new CampaignBusiness.Filter();
            request.setId(id);
            // TODO logica per seach di quelli assegnati
            transaction = cpmRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction", id));
        }
        return TransactionCPMDTO.from(transaction);
    }

    // DELETE BY ID
    public void delete(Long id, String type) {
        try {
            if (type.equals("CPC")) {
                cpcRepository.deleteById(id);
            } else if (type.equals("CPL")) {
                cplRepository.deleteById(id);
            } else if (type.equals("CPM")) {
                cpmRepository.deleteById(id);
            }
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // SEARCH PAGINATED

    public Page<TransactionCPCDTO> searchCpc(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<TransactionCPC> page = cpcRepository.findAll(getSpecificationCPC(request), pageable);
        return page.map(TransactionCPCDTO::from);
    }

    public Page<TransactionCPLDTO> searchCpl(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<TransactionCPL> page = cplRepository.findAll(getSpecificationCPL(request), pageable);
        return page.map(TransactionCPLDTO::from);
    }

    public Page<TransactionCPMDTO> searchCpm(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<TransactionCPM> page = cpmRepository.findAll(getSpecificationCPM(request), pageable);
        return page.map(TransactionCPMDTO::from);
    }

    //SEARCH BY AFFILIATE ID
    public Page<TransactionCPCDTO> searchByAffiliateCpc(Filter request, Long id, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        if (jwtUserDetailsService.getRole().equals("Admin")) {
        } else {
            //  request.setApproved(true);
            request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        }
        Page<TransactionCPC> page = cpcRepository.findAll(getSpecificationCPC(request), pageable);
        return page.map(TransactionCPCDTO::from);
    }

    public Page<TransactionCPLDTO> searchByAffiliateCpl(Filter request, Long id, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        if (jwtUserDetailsService.getRole().equals("Admin")) {
        } else {
            //      request.setApproved(true);
            request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        }
        Page<TransactionCPL> page = cplRepository.findAll(getSpecificationCPL(request), pageable);
        return page.map(TransactionCPLDTO::from);
    }

    public Page<TransactionCPMDTO> searchByAffiliateCpm(Filter request, Long id, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        if (jwtUserDetailsService.getRole().equals("Admin")) {
        } else {
            //    request.setApproved(true);
            request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        }
        Page<TransactionCPM> page = cpmRepository.findAll(getSpecificationCPM(request), pageable);
        return page.map(TransactionCPMDTO::from);
    }

    // UPDATE
    //    public TransactionDTO update(Long id, Filter filter) {
    //        Transaction channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction", id));
    //        TransactionDTO campaignDTOfrom = TransactionDTO.from(channel);
    //
    //        mapper.map(filter, campaignDTOfrom);
    //
    //        Transaction mappedEntity = mapper.map(channel, Transaction.class);
    //        mapper.map(campaignDTOfrom, mappedEntity);
    //
    //        return TransactionDTO.from(repository.save(mappedEntity));
    //    }


    /**
     * ============================================================================================================
     **/

    private Specification<TransactionCPC> getSpecificationCPC(Filter request) {
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

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
            return completePredicate;
        };
    }

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

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
            return completePredicate;
        };
    }

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
    public static class BaseCreateRequest {

        private Long affiliateId;
        private Long campaignId;
        private Long commissionId;
        private Long channelId;
        private Long walletId;
        private Long mediaId;

        //private LocalDateTime dateTime;
        private Double value;
        private Boolean approved;

        private String ip;
        private String agent;
        private String refferal;
        private String data;
        private Long clickNumber;
        private String payoutId;
        private String note;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
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
    }

}
