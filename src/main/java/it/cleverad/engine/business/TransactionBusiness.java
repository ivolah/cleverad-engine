package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.*;
import it.cleverad.engine.persistence.repository.service.*;
import it.cleverad.engine.web.dto.*;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class TransactionBusiness {

    @Autowired
    CampaignBudgetBusiness campaignBudgetBusiness;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private TransactionCPCRepository cpcRepository;
    @Autowired
    private TransactionCPLRepository cplRepository;
    @Autowired
    private TransactionCPMRepository cpmRepository;
    @Autowired
    private TransactionCPSRepository cpsRepository;
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
    private BudgetBusiness budgetBusiness;
    @Autowired
    private CampaignBusiness campaignBusiness;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public TransactionCPCDTO createCpc(BaseCreateRequest request) {
        TransactionCPC newCpcTransaction = mapper.map(request, TransactionCPC.class);

        newCpcTransaction.setInitialValue(request.getValue());
        newCpcTransaction.setPayoutPresent(false);
        newCpcTransaction.setApproved(true);

        if (request.getManualDate() != null) {
            newCpcTransaction.setDateTime(request.getManualDate().atStartOfDay());
            request.setDictionaryId(68L);
            request.setStatusId(73L);
            BigDecimal dd = BigDecimal.valueOf(request.getValue() * request.getClickNumber());
            newCpcTransaction.setInitialValue(dd.doubleValue());
            newCpcTransaction.setValue(dd.doubleValue());

            // trovo revenue
            RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(request.getCampaignId(), 11L);
            if (rf != null) {
                newCpcTransaction.setRevenueId(rf.getId());
            } else {
                newCpcTransaction.setRevenueId(2L);
            }

        }

        newCpcTransaction.setCampaign(campaignRepository.findById(request.campaignId).orElseThrow(() -> new ElementCleveradException("Campaign", request.campaignId)));

        if (request.commissionId != null) try {
            newCpcTransaction.setCommission(commissionRepository.findById(request.commissionId).orElseThrow(() -> new ElementCleveradException("Commission", request.commissionId)));
        } catch (Exception ex) {
            log.error("ECCEZIONE commissionId  - " + ex.getMessage(), ex);
        }
        if (request.commissionId != null) try {
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
            newCpcTransaction.setMedia(mediaRepository.findById(request.mediaId).orElseThrow(() -> new ElementCleveradException("Media", request.mediaId)));
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

    public TransactionCPSDTO createCps(BaseCreateRequest request) {
        TransactionCPS map = mapper.map(request, TransactionCPS.class);
        //   request.setDictionaryId(42L);

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

    public TransactionCPCDTO updateCPCValue(Double value, Long id) {
        TransactionCPC cpc = cpcRepository.findById(id).get();
        cpc.setValue(value);
        return TransactionCPCDTO.from(cpcRepository.save(cpc));
    }

    public TransactionCPLDTO updateCPLValue(Double value, Long id) {
        TransactionCPL cpl = cplRepository.findById(id).get();
        cpl.setValue(value);
        return TransactionCPLDTO.from(cplRepository.save(cpl));
    }

    public TransactionCPSDTO updateCPSValue(Double value, Long id) {
        TransactionCPS cps = cpsRepository.findById(id).get();
        cps.setValue(value);
        return TransactionCPSDTO.from(cpsRepository.save(cps));
    }

    public TransactionCPMDTO updateCPMValue(Double value, Long id) {
        TransactionCPM cpm = cpmRepository.findById(id).get();
        cpm.setValue(value);
        return TransactionCPMDTO.from(cpmRepository.save(cpm));
    }

    //  quando campbio stato devo ricalcolare i budget affilitato e campagna
    //  nuovi tre stati  : pending, approvato e rifutato

    public void updateStatus(Long id, Long dictionaryId, String tipo, Boolean approved, Long statusId) {
        if (tipo.equals("CPC")) {
            TransactionCPC cpc = cpcRepository.findById(id).get();

            if (statusId == null && cpc.getDictionaryStatus() != null) statusId = cpc.getDictionaryStatus().getId();
            if (dictionaryId == null && cpc.getDictionary() != null) dictionaryId = cpc.getDictionary().getId();

            if (statusId == 74L || dictionaryId == 40L) {

                // aggiorno budge e CAP affiliato
                BudgetDTO budgetAff = budgetBusiness.getByIdCampaignAndIdAffiliate(cpc.getCampaign().getId(), cpc.getAffiliate().getId()).stream().findFirst().orElse(null);
                if (budgetAff != null) {
                    budgetBusiness.updateBudget(budgetAff.getId(), budgetAff.getBudget() + cpc.getValue());
                    budgetBusiness.updateCap(budgetAff.getId(), Math.toIntExact(budgetAff.getCap() + cpc.getClickNumber()));
                }

                // aggiorno budget campagna
                campaignBusiness.updateBudget(cpc.getCampaign().getId(), campaignBusiness.findById(cpc.getCampaign().getId()).getBudget() + cpc.getValue());

                // aggiorno wallet
                Long walletID = null;
                if (cpc.getAffiliate().getId() != null) {
                    walletID = walletRepository.findByAffiliateId(cpc.getAffiliate().getId()).getId();
                    walletBusiness.decrement(walletID, cpc.getValue());
                }

                //aggiorno Camapign Budget
                if (cpc.getValue() > 0D) {
                    CampaignBudget cb = campaignBudgetBusiness.findByCampaignIdAndDate(cpc.getCampaign().getId(), cpc.getDateTime());
                    if (cb != null) {
                        campaignBudgetBusiness.decreaseCapErogatoOnDeleteTransaction(cb.getId(), Math.toIntExact(cb.getCapErogato() - cpc.getClickNumber()));
                        campaignBudgetBusiness.decreaseBudgetErogatoOnDeleteTransaction(cb.getId(), cb.getBudgetErogato() - cpc.getValue());
                    }
                }

            } else if (dictionaryId == 40L || statusId == 74L) {
                // setto revenue e commission a 0
                cpc.setRevenueId(1L);
                cpc.setCommission(commissionRepository.findById(1L).orElseThrow(() -> new ElementCleveradException("Commission", 1L)));
                cpc.setValue(0D);
            }
            if (dictionaryId != null) {
                Long finalDictionaryId = dictionaryId;
                cpc.setDictionary(dictionaryRepository.findById(dictionaryId).orElseThrow(() -> new ElementCleveradException("Dictionay", finalDictionaryId)));
            } else dictionaryId = 0L;
            if (statusId != null) {
                Long finalStatusId = statusId;
                cpc.setDictionaryStatus(dictionaryRepository.findById(statusId).orElseThrow(() -> new ElementCleveradException("Status", finalStatusId)));
            } else statusId = 0L;
            if (approved != null) cpc.setApproved(approved);

            cpcRepository.save(cpc);
        } else if (tipo.equals("CPL")) {
            TransactionCPL cpl = cplRepository.findById(id).get();
            if (statusId == null && cpl.getDictionaryStatus() != null) statusId = cpl.getDictionaryStatus().getId();
            if (dictionaryId == null && cpl.getDictionary() != null) dictionaryId = cpl.getDictionary().getId();

            if (statusId == 74L || dictionaryId == 40L) {

                // aggiorno budget affiliato
                BudgetDTO budgetAff = budgetBusiness.getByIdCampaignAndIdAffiliate(cpl.getCampaign().getId(), cpl.getAffiliate().getId()).stream().findFirst().orElse(null);
                if (budgetAff != null) {
                    budgetBusiness.updateBudget(budgetAff.getId(), budgetAff.getBudget() + cpl.getValue());
                    budgetBusiness.updateCap(budgetAff.getId(), budgetAff.getCap() + 1);
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
                if (cpl.getValue() > 0D) {
                    CampaignBudget cb = campaignBudgetBusiness.findByCampaignIdAndDate(cpl.getCampaign().getId(), cpl.getDateTime());
                    if (cb != null) {
                        campaignBudgetBusiness.decreaseCapErogatoOnDeleteTransaction(cb.getId(), Math.toIntExact(cb.getCapErogato() - 1));
                        campaignBudgetBusiness.decreaseBudgetErogatoOnDeleteTransaction(cb.getId(), cb.getBudgetErogato() - cpl.getValue());
                    }
                }

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
        } else if (tipo.equals("CPM")) {
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
            cpmRepository.save(cpm);
        } else if (tipo.equals("CPS")) {
            TransactionCPS cps = cpsRepository.findById(id).get();
            if (dictionaryId != null) {
                Long finalDictionaryId3 = dictionaryId;
                cps.setDictionary(dictionaryRepository.findById(dictionaryId).orElseThrow(() -> new ElementCleveradException("Dictionay", finalDictionaryId3)));
            } else dictionaryId = 0L;
            if (statusId != null) {
                Long finalStatusId3 = statusId;
                cps.setDictionaryStatus(dictionaryRepository.findById(statusId).orElseThrow(() -> new ElementCleveradException("Status", finalStatusId3)));
            } else statusId = 0L;
            if (approved != null) cps.setApproved(approved);
            if (dictionaryId == 40L || statusId == 74L) {
                // setto revenue e commission a 0
                cps.setRevenueId(1L);
                cps.setCommission(commissionRepository.findById(1L).orElseThrow(() -> new ElementCleveradException("Commission", 1L)));
                cps.setValue(0D);
            }
            cpsRepository.save(cps);
        }
    }

    public void updatePhoneStatus(Long id, String number, Boolean verified) {

        TransactionCPL cpl = cplRepository.findById(id).get();
        cpl.setPhoneVerified(verified);
        cpl.setPhoneNumber(number);
        cplRepository.save(cpl);

        // Setto a rigettato  se stato false e numero non nullo
        if (!verified && StringUtils.isNotBlank(number)) {

            // aggiorno budget affiliato
            BudgetDTO budgetAff = budgetBusiness.getByIdCampaignAndIdAffiliate(cpl.getCampaign().getId(), cpl.getAffiliate().getId()).stream().findFirst().orElse(null);
            if (budgetAff != null) {
                budgetBusiness.updateBudget(budgetAff.getId(), budgetAff.getBudget() + cpl.getValue());
                budgetBusiness.updateCap(budgetAff.getId(), budgetAff.getCap() + 1);
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
            if (cpl.getValue() > 0D) {
                CampaignBudget cb = campaignBudgetBusiness.findByCampaignIdAndDate(cpl.getCampaign().getId(), cpl.getDateTime());
                if (cb != null) {
                    campaignBudgetBusiness.decreaseCapErogatoOnDeleteTransaction(cb.getId(), Math.toIntExact(cb.getCapErogato() - 1));
                    campaignBudgetBusiness.decreaseBudgetErogatoOnDeleteTransaction(cb.getId(), cb.getBudgetErogato() - cpl.getValue());
                }
            }
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

    // GET BY ID CPC
    public TransactionCPCDTO findByIdCPC(Long id) {
        TransactionCPC transaction = null;
        if (jwtUserDetailsService.getRole().equals("Admin")) {
            transaction = cpcRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction", id));
        } else {
            CampaignBusiness.Filter request = new CampaignBusiness.Filter();
            request.setId(id);
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
            transaction = cpmRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction", id));
        }
        return TransactionCPMDTO.from(transaction);
    }

    // GET BY ID CPs
    public TransactionCPSDTO findByIdCPS(Long id) {
        TransactionCPS transaction = null;
        if (jwtUserDetailsService.getRole().equals("Admin")) {
            transaction = cpsRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction", id));
        } else {
            CampaignBusiness.Filter request = new CampaignBusiness.Filter();
            request.setId(id);
            transaction = cpsRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction", id));
        }
        return TransactionCPSDTO.from(transaction);
    }

    // DELETE BY ID
    public void deleteInterno(Long id, String type) {
        try {
            if (type.equals("CPC")) {
                cpcRepository.deleteById(id);
            } else if (type.equals("CPM")) {
                cpmRepository.deleteById(id);
            } else if (type.equals("CPL")) {
                cplRepository.deleteById(id);
            } else if (type.equals("CPS")) {
                cpsRepository.deleteById(id);
            }
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    public void delete(Long id, String type) {
        try {
            if (type.equals("CPC")) {
                TransactionCPCDTO dto = this.findByIdCPC(id);

                // aggiorno budge e CAP affiliato
                BudgetDTO budgetAff = budgetBusiness.getByIdCampaignAndIdAffiliate(dto.getCampaignId(), dto.getAffiliateId()).stream().findFirst().orElse(null);
                if (budgetAff != null && budgetAff.getId() != null && budgetAff.getBudget() != null) {
                    budgetBusiness.updateBudget(budgetAff.getId(), budgetAff.getBudget() + dto.getValue());
                    budgetBusiness.updateCap(budgetAff.getId(), Math.toIntExact(budgetAff.getCap() + dto.getClickNumber()));
                }

                // aggiorno budget campagna
                campaignBusiness.updateBudget(dto.getCampaignId(), campaignBusiness.findById(dto.getCampaignId()).getBudget() + dto.getValue());

                // aggiorno wallet
                Long walletID = null;
                if (dto.getAffiliateId() != null) {
                    walletID = walletRepository.findByAffiliateId(dto.getAffiliateId()).getId();
                    walletBusiness.decrement(walletID, dto.getValue());
                } else {
                    log.warn("WALLET NON TROVATO :: {}", dto.getAffiliateId());
                }

                //aggiorno Camapign Budget
                if (dto.getValue() > 0D) {
                    CampaignBudget cb = campaignBudgetBusiness.findByCampaignIdAndDate(dto.getCampaignId(), dto.getDateTime());
                    if (cb != null) {
                        campaignBudgetBusiness.decreaseCapErogatoOnDeleteTransaction(cb.getId(), Math.toIntExact(cb.getCapErogato() - dto.getClickNumber()));
                        campaignBudgetBusiness.decreaseBudgetErogatoOnDeleteTransaction(cb.getId(), cb.getBudgetErogato() - dto.getValue());
                    }
                }

                //cancello
                cpcRepository.delete(cpcRepository.findById(id).get());
            } else if (type.equals("CPL")) {
                TransactionCPLDTO dto = this.findByIdCPL(id);

                // aggiorno budget affiliato
                BudgetDTO budgetAff = budgetBusiness.getByIdCampaignAndIdAffiliate(dto.getCampaignId(), dto.getAffiliateId()).stream().findFirst().orElse(null);
                if (budgetAff != null) {
                    budgetBusiness.updateBudget(budgetAff.getId(), budgetAff.getBudget() + dto.getValue());
                    budgetBusiness.updateCap(budgetAff.getId(), budgetAff.getCap() + 1);
                }

                // aggiorno budget campagna
                campaignBusiness.updateBudget(dto.getCampaignId(), campaignBusiness.findById(dto.getCampaignId()).getBudget() + dto.getValue());

                // aggiorno wallet
                Long walletID = null;
                if (dto.getAffiliateId() != null) {
                    walletID = walletRepository.findByAffiliateId(dto.getAffiliateId()).getId();
                    walletBusiness.decrement(walletID, dto.getValue());
                } else {
                    log.warn("WALLET NON TROVATO :: {}", dto.getAffiliateId());
                }

                //aggiorno Camapign Budget
                if (dto.getValue() > 0D) {
                    CampaignBudget cb = campaignBudgetBusiness.findByCampaignIdAndDate(dto.getCampaignId(), dto.getDateTime());
                    if (cb != null) {
                        campaignBudgetBusiness.decreaseCapErogatoOnDeleteTransaction(cb.getId(), Math.toIntExact(cb.getCapErogato() - 1));
                        campaignBudgetBusiness.decreaseBudgetErogatoOnDeleteTransaction(cb.getId(), cb.getBudgetErogato() - dto.getValue());
                    }
                }

                cplRepository.delete(cplRepository.findById(id).get());
            } else if (type.equals("CPM")) {
                TransactionCPMDTO dto = this.findByIdCPM(id);

                // aggiorno budget affiliato
                BudgetDTO budgetAff = budgetBusiness.getByIdCampaignAndIdAffiliate(dto.getCampaignId(), dto.getAffiliateId()).stream().findFirst().orElse(null);
                if (budgetAff != null) {
                    budgetBusiness.updateBudget(budgetAff.getId(), budgetAff.getBudget() + dto.getValue());
                    budgetBusiness.updateCap(budgetAff.getId(), Math.toIntExact(budgetAff.getCap() + dto.getImpressionNumber()));
                }

                // aggiorno budget campagna
                campaignBusiness.updateBudget(dto.getCampaignId(), campaignBusiness.findById(dto.getCampaignId()).getBudget() + dto.getValue());

                // aggiorno wallet
                Long wallerID = walletBusiness.findByIdAffilaite(dto.getAffiliateId()).stream().findFirst().get().getId();
                walletBusiness.decrement(wallerID, dto.getValue());

                if (dto.getValue() > 0D) {
                    CampaignBudget cb = campaignBudgetBusiness.findByCampaignIdAndDate(dto.getCampaignId(), dto.getDateTime());
                    if (cb != null) {
                        campaignBudgetBusiness.decreaseCapErogatoOnDeleteTransaction(cb.getId(), Math.toIntExact(cb.getCapErogato() - dto.getImpressionNumber()));
                        campaignBudgetBusiness.decreaseBudgetErogatoOnDeleteTransaction(cb.getId(), cb.getBudgetErogato() - dto.getValue());
                    }
                }

                //cancello
                cpmRepository.deleteById(id);
            } else if (type.equals("CPS")) {
                TransactionCPSDTO dto = this.findByIdCPS(id);

                // aggiorno budget affiliato
                BudgetDTO budgetAff = budgetBusiness.getByIdCampaignAndIdAffiliate(dto.getCampaignId(), dto.getAffiliateId()).stream().findFirst().orElse(null);
                if (budgetAff != null) {
                    budgetBusiness.updateBudget(budgetAff.getId(), budgetAff.getBudget() + dto.getValue());
                }

                // aggiorno budget campagna
                campaignBusiness.updateBudget(dto.getCampaignId(), campaignBusiness.findById(dto.getCampaignId()).getBudget() + dto.getValue());

                // aggiorno wallet
                Long wallerID = walletBusiness.findByIdAffilaite(dto.getAffiliateId()).stream().findFirst().get().getId();
                walletBusiness.decrement(wallerID, dto.getValue());

                if (dto.getValue() > 0D) {
                    CampaignBudget cb = campaignBudgetBusiness.findByCampaignIdAndDate(dto.getCampaignId(), dto.getDateTime());
                    if (cb != null) {
                        // campaignBudgetBusiness.decreaseCapErogatoOnDeleteTransaction(cb.getId(), Math.toIntExact(cb.getCapErogato() - dto.get));
                        campaignBudgetBusiness.decreaseBudgetErogatoOnDeleteTransaction(cb.getId(), cb.getBudgetErogato() - dto.getValue());
                    }
                }

                //cancello
                cpsRepository.deleteById(id);
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

    public Page<TransactionCPSDTO> searchCps(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<TransactionCPS> page = cpsRepository.findAll(getSpecificationCPS(request), pageable);
        return page.map(TransactionCPSDTO::from);
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

    public Page<TransactionCPSDTO> searchByAffiliateCps(Filter request, Long id, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        if (jwtUserDetailsService.getRole().equals("Admin")) {
        } else {
            //    request.setApproved(true);
            request.setAffiliateId(jwtUserDetailsService.getAffiliateID());
        }
        Page<TransactionCPS> page = cpsRepository.findAll(getSpecificationCPS(request), pageable);
        return page.map(TransactionCPSDTO::from);
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

    public Page<DictionaryDTO> getTypes() {
        return dictionaryBusiness.getTransactionTypes();
    }

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

    private Specification<TransactionCPS> getSpecificationCPS(Filter request) {
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


//            if (request.getStatusId() != null) {
//                predicates.add(cb.equal(root.get("dictionary").get("id"), request.getStatusId()));
//            }

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