package it.cleverad.engine.scheduled;

import it.cleverad.engine.business.*;
import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.CampaignBudget;
import it.cleverad.engine.persistence.model.service.Commission;
import it.cleverad.engine.persistence.model.service.RevenueFactor;
import it.cleverad.engine.persistence.model.tracking.Cpl;
import it.cleverad.engine.persistence.repository.service.CommissionRepository;
import it.cleverad.engine.persistence.repository.service.WalletRepository;
import it.cleverad.engine.persistence.repository.tracking.CplRepository;
import it.cleverad.engine.service.ReferralService;
import it.cleverad.engine.web.dto.AffiliateChannelCommissionCampaignDTO;
import it.cleverad.engine.web.dto.BudgetDTO;
import it.cleverad.engine.web.dto.CampaignDTO;
import it.cleverad.engine.web.dto.TransactionCPLDTO;
import it.cleverad.engine.web.dto.tracking.CpcDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ManageCPL {
    @Autowired
    CampaignBudgetBusiness campaignBudgetBusiness;
    @Autowired
    private CommissionRepository commissionRepository;
    @Autowired
    private CplBusiness cplBusiness;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private WalletBusiness walletBusiness;
    @Autowired
    private BudgetBusiness budgetBusiness;
    @Autowired
    private CampaignBusiness campaignBusiness;
    @Autowired
    private RevenueFactorBusiness revenueFactorBusiness;
    @Autowired
    private AffiliateChannelCommissionCampaignBusiness affiliateChannelCommissionCampaignBusiness;
    @Autowired
    private ReferralService referralService;
    @Autowired
    private CpcBusiness cpcBusiness;
    @Autowired
    private CplRepository cplRepository;
    @Autowired
    private TransactionCPLBusiness transactionCPLBusiness;

    /**
     * ============================================================================================================
     **/

    @Scheduled(cron = "3 */3 * * * ?")
    @Async
    public void gestisciTransazioni() {
        trasformaTrackingCPL();
        gestisciBlacklisted();
    }

    /**
     * ============================================================================================================
     **/

    @Async
    public void trasformaTrackingCPL() {
        try {
            cplBusiness.getUnreadOneHourBefore().stream().filter(cplDTO -> StringUtils.isNotBlank(cplDTO.getRefferal())).forEach(cplDTO -> {

                // leggo sempre i cpc precedenti per trovare il click riferito alla lead
                cpcBusiness.findByIp24HoursBefore(cplDTO.getIp(), cplDTO.getDate(), cplDTO.getRefferal())
                        .stream()
                        .filter(cpcDTO -> StringUtils.isNotBlank(cpcDTO.getRefferal()))
                        .forEach(cpcDTO -> {
                            log.trace("R ORIG {} --> R CPC {}", cplDTO.getRefferal(), cpcDTO.getRefferal());
                            cplDTO.setRefferal(cpcDTO.getRefferal());
                            cplDTO.setCpcId(cpcDTO.getId());
                        });
                Long idCpc = cplDTO.getCpcId();
                log.trace("Refferal :: {} con ID CPC {}", cplDTO.getRefferal(),idCpc);
                cplBusiness.setCpcId(cplDTO.getId(), idCpc);

                // prendo reffereal e lo leggo
                Refferal refferal = referralService.decodificaReferral(cplDTO.getRefferal());

                if (refferal != null && refferal.getAffiliateId() != null) {
                    log.trace(">>>> T-CPL :: {} :: ", cplDTO, refferal);

                    //aggiorno dati CPL
                    Cpl cccpl = cplRepository.findById(cplDTO.getId()).orElseThrow(() -> new ElementCleveradException("Cpl", cplDTO.getId()));
                    cccpl.setMediaId(refferal.getMediaId());
                    cccpl.setCampaignId(refferal.getCampaignId());
                    cccpl.setAffiliateId(refferal.getAffiliateId());
                    cccpl.setChannelId(refferal.getChannelId());
                    cccpl.setTargetId(refferal.getTargetId());
                    cplRepository.save(cccpl);

                    // setta transazione
                    TransactionCPLBusiness.BaseCreateRequest transaction = new TransactionCPLBusiness.BaseCreateRequest();
                    transaction.setAffiliateId(refferal.getAffiliateId());
                    transaction.setCampaignId(refferal.getCampaignId());
                    transaction.setChannelId(refferal.getChannelId());
                    transaction.setMediaId(refferal.getMediaId());
                    transaction.setDateTime(cplDTO.getDate());
                    transaction.setApproved(true);
                    transaction.setPayoutPresent(false);

                    if (StringUtils.isNotBlank(cplDTO.getAgent())) transaction.setAgent(cplDTO.getAgent());
                    else transaction.setAgent("");

                    transaction.setIp(cplDTO.getIp());
                    transaction.setData(cplDTO.getData());
                    transaction.setMediaId(refferal.getMediaId());

                    // controlla data scadneza camapgna
                    try {
                        CampaignDTO campaignDTO = campaignBusiness.findByIdAdmin(refferal.getCampaignId());
                        LocalDate endDate = campaignDTO.getEndDate();
                        if (endDate.isBefore(LocalDate.now())) {
                            // setto a campagna scaduta
                            transaction.setDictionaryId(49L);
                        } else {
                            //setto pending
                            transaction.setDictionaryId(42L);
                        }

                        // associo a wallet
                        Long affiliateID = refferal.getAffiliateId();

                        Long walletID = null;
                        if (affiliateID != null) {
                            walletID = walletRepository.findByAffiliateId(affiliateID).getId();
                            transaction.setWalletId(walletID);
                        }

                        // trovo revenue
                        RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(refferal.getCampaignId(), 11L);
                        if (rf != null) {
                            transaction.setRevenueId(rf.getId());
                        } else {
                            log.warn("Non trovato revenue factor di tipo 11 per campagna {} , setto default", refferal.getCampaignId());
                            transaction.setRevenueId(2L);
                        }

                        // gesione commisione
                        Double commVal = 0D;
                        Long commissionId = 0L;
                        AffiliateChannelCommissionCampaignBusiness.Filter req = new AffiliateChannelCommissionCampaignBusiness.Filter();
                        if (StringUtils.isNotBlank(cplDTO.getActionId())) {
                            // con action Id settanto in cpl vado a cercare la commissione associata
                            req.setAffiliateId(refferal.getAffiliateId());
                            req.setChannelId(refferal.getChannelId());
                            req.setCampaignId(refferal.getCampaignId());
                            req.setActionId(cplDTO.getActionId().trim());
                            AffiliateChannelCommissionCampaignDTO accc = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);
                            Commission cm = commissionRepository.findById(accc.getCommissionId()).get();
                            commVal = cm.getValue();
                            commissionId = cm.getId();
                            log.warn("Commissione >{}< trovata da action ID >{}<", cm.getId(), cplDTO.getActionId());
                        } else {
                            // non è settato l'actionId allora faccio il solito giro
                            req.setAffiliateId(refferal.getAffiliateId());
                            req.setChannelId(refferal.getChannelId());
                            req.setCampaignId(refferal.getCampaignId());
                            req.setCommissionDicId(11L);
                            AffiliateChannelCommissionCampaignDTO acccFirst = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);
                            if (acccFirst != null) {
                                commVal = acccFirst.getCommissionValue();
                                commissionId = acccFirst.getCommissionId();
                            } else
                                log.warn("Non trovato Commission di tipo 10 per campagna {}, setto default", refferal.getCampaignId());
                        }
                        transaction.setCommissionId(commissionId);

                        Double totale = commVal * 1;
                        transaction.setValue(DoubleRounder.round(totale, 2));
                        transaction.setLeadNumber(Long.valueOf(1));

                        // incemento valore
                        if (walletID != null && totale > 0D) walletBusiness.incement(walletID, totale);

                        // decremento budget Affiliato
                        BudgetDTO bb = budgetBusiness.getByIdCampaignAndIdAffiliate(refferal.getCampaignId(), refferal.getAffiliateId()).stream().findFirst().orElse(null);
                        if (bb != null && bb.getBudget() != null) {
                            Double totBudgetDecrementato = bb.getBudget() - totale;
                            budgetBusiness.updateBudget(bb.getId(), totBudgetDecrementato);

                            // decremento cap affiliato
                            Integer cap = bb.getCap() - 1;
                            budgetBusiness.updateCap(bb.getId(), cap);

                            // setto stato transazione a ovebudget editore se totale < 0
                            if (totBudgetDecrementato < 0) {
                                transaction.setDictionaryId(47L);
                            }
                        }

                        // decremento budget Campagna
                        if (campaignDTO != null) {

                            Double budgetCampagna = campaignDTO.getBudget() - totale;
                            campaignBusiness.updateBudget(campaignDTO.getId(), budgetCampagna);

                            // setto stato transazione a ovebudget editore se totale < 0
                            if (budgetCampagna < 0) {
                                transaction.setDictionaryId(48L);
                            }
                        }

//                        if (totale > 0) {
//                            // trovo CampaignBudget
//                            CampaignBudget cb = campaignBudgetBusiness.findByCampaignIdAndDate(campaignDTO.getId(), LocalDateTime.now());
//                            if (cb != null) {
//                                //incremento budget erogato
//                                campaignBudgetBusiness.incrementoBudgetErogato(cb.getId(), totale);
//                                // incremento cap
//                                campaignBudgetBusiness.incrementoCapErogato(cb.getId(), 1);
//                            }
//                        }

                        //setto pending
                        transaction.setStatusId(72L);

                        // setto id CPC
                        transaction.setCpcId(idCpc);

                        // creo la transazione
                        TransactionCPLDTO cpl = transactionCPLBusiness.createCpl(transaction);
                        log.info(">>> CREATO TRANSAZIONE :::: CPL :::: {} ", cpl.getId());

                        // setto a gestito
                        cplBusiness.setRead(cplDTO.getId());
                    } catch (Exception ecc) {
                        log.error("ECCEZIONE CPL :> ", ecc);
                    }
                }// creo solo se ho affiliate
            });
        } catch (Exception e) {
            log.error("Eccezione Scheduler CPL --  {}", e.getMessage(), e);
        }
    }//trasformaTrackingCPL

    /**
     * ============================================================================================================
     **/

    public void gestisciBlacklisted() {
        try {

            cplBusiness.getUnreadBlacklisted().stream().filter(cplDTO -> StringUtils.isNotBlank(cplDTO.getRefferal())).forEach(cplDTO -> {

                if (cplDTO.getRefferal().length() < 6) {
                    // cerco da cpc
                    List<CpcDTO> ips = cpcBusiness.findByIp24HoursBefore(cplDTO.getIp(), cplDTO.getDate(), cplDTO.getRefferal()).stream().collect(Collectors.toList());
                    // prendo ultimo ip
                    for (CpcDTO dto : ips)
                        if (StringUtils.isNotBlank(dto.getRefferal())) cplDTO.setRefferal(dto.getRefferal());
                }

                // prendo reffereal e lo leggo
                Refferal refferal = referralService.decodificaReferral(cplDTO.getRefferal());
                if (refferal != null && refferal.getAffiliateId() != null) {
                    log.debug(">>>> BLACLISTED CPL :: {} :: {}", cplDTO, refferal);

                    //aggiorno dati CPL
                    Cpl cccpl = cplRepository.findById(cplDTO.getId()).orElseThrow(() -> new ElementCleveradException("Cpl", cplDTO.getId()));
                    cccpl.setMediaId(refferal.getMediaId());
                    cccpl.setCampaignId(refferal.getCampaignId());
                    cccpl.setAffiliateId(refferal.getAffiliateId());
                    cccpl.setChannelId(refferal.getChannelId());
                    cccpl.setTargetId(refferal.getTargetId());
                    cplRepository.save(cccpl);

                    // setta transazione
                    TransactionCPLBusiness.BaseCreateRequest transaction = new TransactionCPLBusiness.BaseCreateRequest();
                    transaction.setAffiliateId(refferal.getAffiliateId());
                    transaction.setCampaignId(refferal.getCampaignId());
                    transaction.setChannelId(refferal.getChannelId());
                    transaction.setMediaId(refferal.getMediaId());
                    transaction.setDateTime(cplDTO.getDate());
                    transaction.setApproved(false);
                    transaction.setPayoutPresent(false);

                    if (StringUtils.isNotBlank(cplDTO.getAgent())) transaction.setAgent(cplDTO.getAgent());
                    else transaction.setAgent("");

                    transaction.setIp(cplDTO.getIp());
                    transaction.setData(cplDTO.getData());
                    transaction.setMediaId(refferal.getMediaId());

                    // controlla data scadneza camapgna
                    try {
                        CampaignDTO campaignDTO = campaignBusiness.findByIdAdmin(refferal.getCampaignId());

                        // associo a wallet
                        Long affiliateID = refferal.getAffiliateId();
                        if (affiliateID != null) {
                            transaction.setWalletId(walletRepository.findByAffiliateId(affiliateID).getId());
                        }

                        // trovo revenue
                        RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(refferal.getCampaignId(), 11L);
                        if (rf != null) {
                            transaction.setRevenueId(rf.getId());
                        } else {
                            log.warn("Non trovato revenue factor di tipo 11 per campagna {} , setto default", refferal.getCampaignId());
                            transaction.setRevenueId(2L);
                        }

                        // gesione commisione
                        Double commVal = 0D;
                        AffiliateChannelCommissionCampaignBusiness.Filter req = new AffiliateChannelCommissionCampaignBusiness.Filter();
                        req.setAffiliateId(refferal.getAffiliateId());
                        req.setChannelId(refferal.getChannelId());
                        req.setCampaignId(refferal.getCampaignId());
                        req.setCommissionDicId(11L);
                        AffiliateChannelCommissionCampaignDTO acccFirst = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);
                        if (acccFirst != null) {
                            commVal = acccFirst.getCommissionValue();
                            transaction.setCommissionId(acccFirst.getCommissionId());
                        } else {
                            log.warn("Non trovato Commission di tipo 10 per campagna {}, setto default", refferal.getCampaignId());
                            transaction.setCommissionId(0L);
                        }

                        transaction.setValue(commVal * 1);
                        transaction.setLeadNumber(Long.valueOf(1));

                        //setto rifiutato
                        transaction.setStatusId(74L);
                        // setto blacklisted
                        transaction.setDictionaryId(70L);

                        // creo la transazione
                        TransactionCPLDTO cpl = transactionCPLBusiness.createCpl(transaction);
                        log.info(">>>BLACKLIST :::: CPL :::: {} ", cpl.getId());

                        // setto a gestito
                        cplBusiness.setRead(cplDTO.getId());
                    } catch (Exception ecc) {
                        log.error("BLACKLIS ECCEZIONE CPL :> ", ecc);
                    }
                }// creo solo se ho affiliate
            });
        } catch (Exception e) {
            log.error("Eccezione Scheduler CPL --  {}", e.getMessage(), e);
        }
    }//gestisciBlacklisted

}