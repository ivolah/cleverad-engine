package it.cleverad.engine.scheduled.manage;

import it.cleverad.engine.business.*;
import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.RevenueFactor;
import it.cleverad.engine.persistence.model.tracking.Cps;
import it.cleverad.engine.persistence.repository.service.WalletRepository;
import it.cleverad.engine.persistence.repository.tracking.CpsRepository;
import it.cleverad.engine.service.ReferralService;
import it.cleverad.engine.web.dto.*;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ManageCPS {

    @Autowired
    CampaignBudgetBusiness campaignBudgetBusiness;
    @Autowired
    private CpsBusiness cpsBusiness;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private AffiliateBudgetBusiness affiliateBudgetBusiness;
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
    private CpsRepository cpsRepository;
    @Autowired
    private TransactionCPSBusiness transactionCPSBusiness;

    /**
     * ============================================================================================================
     **/

    @Scheduled(cron = "8 */1 * * * ?")
    @Async
    public void gestisciTransazioni() {
        this.trasformaTrackingCPS();
        this.gestisciBlacklisted();
    }

    /**
     * ============================================================================================================
     **/

    @Async
    public void trasformaTrackingCPS() {
        try {
            cpsBusiness.getUnreadOneHourBefore().stream().filter(cpsDTO -> StringUtils.isNotBlank(cpsDTO.getRefferal())).forEach(cpsDTO -> {

                // leggo sempre i cpc precedenti per trovare il click riferito alla lead
                cpcBusiness.findByIp24HoursBefore(cpsDTO.getIp(), cpsDTO.getDate(), cpsDTO.getRefferal()).stream().filter(cpcDTO -> StringUtils.isNotBlank(cpcDTO.getRefferal())).forEach(cpcDTO -> {
                    log.info("R ORIG {} --> R CPC {}", cpsDTO.getRefferal(), cpcDTO.getRefferal());
                    cpsDTO.setRefferal(cpcDTO.getRefferal());
                    cpsDTO.setCpcId(cpcDTO.getId());
                });
                Long idCpc = cpsDTO.getCpcId();
                log.info("Refferal :: {} con ID CPC {}", cpsDTO.getRefferal(), idCpc);
                cpsBusiness.setCpcId(cpsDTO.getId(), idCpc);
                // prendo reffereal e lo leggo
                Refferal refferal = referralService.decodificaReferral(cpsDTO.getRefferal());

                if (refferal != null && refferal.getAffiliateId() != null) {
                    log.info(">>>> T-CPS :: {} :: ", cpsDTO, refferal);

                    //aggiorno dati CPS
                    Cps cccps = cpsRepository.findById(cpsDTO.getId()).orElseThrow(() -> new ElementCleveradException("Cps", cpsDTO.getId()));
                    cccps.setMediaId(refferal.getMediaId());
                    cccps.setCampaignId(refferal.getCampaignId());
                    cccps.setAffiliateId(refferal.getAffiliateId());
                    cccps.setChannelId(refferal.getChannelId());
                    cccps.setTargetId(refferal.getTargetId());
                    if (cccps.getData().equals("[REPLACE]")) {
                        cccps.setData("");
                        cpsDTO.setData("");
                    }
                    if (StringUtils.isBlank(cccps.getInfo()) && idCpc != null) {
                        CpcDTO cpc = cpcBusiness.findById(idCpc);
                        cccps.setInfo(cpc.getInfo());
                        cpsDTO.setInfo(cpc.getInfo());
                    }
                    cpsRepository.save(cccps);

                    // setta transazione
                    TransactionCPSBusiness.BaseCreateRequest transaction = new TransactionCPSBusiness.BaseCreateRequest();
                    transaction.setRefferal(cpsDTO.getRefferal());
                    transaction.setAffiliateId(refferal.getAffiliateId());
                    transaction.setCampaignId(refferal.getCampaignId());
                    transaction.setChannelId(refferal.getChannelId());
                    transaction.setMediaId(refferal.getMediaId());
                    transaction.setDateTime(cpsDTO.getDate());
                    transaction.setApproved(true);
                    transaction.setPayoutPresent(false);
                    // setto id CPC
                    transaction.setCpcId(idCpc);

                    if (StringUtils.isNotBlank(cpsDTO.getAgent())) transaction.setAgent(cpsDTO.getAgent());
                    else transaction.setAgent("");

                    transaction.setIp(cpsDTO.getIp());
                    transaction.setData(cpsDTO.getData().trim().replace("[REPLACE]", ""));
                    transaction.setMediaId(refferal.getMediaId());

                    // controlla data scadneza camapgna
                    try {
                        CampaignDTO campaignDTO = campaignBusiness.findByIdAdmin(refferal.getCampaignId());
                        LocalDate endDate = campaignDTO.getEndDate();
                        Boolean scaduta = false;
                        if (endDate.isBefore(LocalDate.now())) {
                            // setto a campagna scaduta
                            transaction.setDictionaryId(49L);
                            scaduta =true;
                        } else {
                            //setto pending
                            transaction.setDictionaryId(42L);
                        }

                        if(!campaignDTO.getStatus()){
                            // setto a campagna scaduta
                            transaction.setDictionaryId(49L);
                            scaduta = true;
                        }

                        // associo a wallet
                        Long affiliateID = refferal.getAffiliateId();

                        Long walletID;
                        if (affiliateID != null) {
                            walletID = walletRepository.findByAffiliateId(affiliateID).getId();
                            transaction.setWalletId(walletID);
                        }

                        if (scaduta) {
                            log.debug("Campagna {} : {} scaduta", campaignDTO.getId(), campaignDTO.getName());
                            transaction.setRevenueId(1L);
                            transaction.setCommissionId(0L);
                            transaction.setStatusId(74L); // rigettato
                            transaction.setValue(0D);
                            transaction.setDictionaryId(49L);
                        } else {

                            // GIRO STANDARD
                            RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(refferal.getCampaignId(), 51L);
                            if (rf != null) {
                                transaction.setRevenueId(rf.getId());
                            } else {
                                log.warn("Non trovato revenue factor di tipo 51 per campagna {} , setto default", refferal.getCampaignId());
                                transaction.setRevenueId(2L);
                            }

                            // gesione commisione
                            Double sellValue = 0D;
                            Long commissionId = 0L;
                            AffiliateChannelCommissionCampaignBusiness.Filter req = new AffiliateChannelCommissionCampaignBusiness.Filter();
                            req.setAffiliateId(refferal.getAffiliateId());
                            req.setChannelId(refferal.getChannelId());
                            req.setCampaignId(refferal.getCampaignId());
                            req.setCommissionDicId(51L);
                            req.setBlocked(false);
                            AffiliateChannelCommissionCampaignDTO acccFirst = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);
                            if (acccFirst != null) {
                                // trovo in info di cps il ORDER VALUE
                                String info = cpsDTO.getData();
                                Map<String, String> infos = referralService.estrazioneInfo(info);
                                String orderValue = infos.get("orderValue");
                                log.info("OrderValue: " + orderValue);
                                if (StringUtils.isNotBlank(orderValue)) {
                                    Double valore = Double.valueOf(orderValue);
                                    Double percentuale = acccFirst.getSale();
                                    log.info("Valore {} - Percenutale {}", valore, percentuale);
                                    sellValue = (percentuale / 100.0) * valore;
                                    log.info("SELL VALUE :: {}", DoubleRounder.round(sellValue, 2));
                                }
                                commissionId = acccFirst.getCommissionId();
                            } else
                                log.warn("No Commission CPS C: {} e A: {}, setto default ({})", refferal.getCampaignId(), refferal.getAffiliateId(), cpsDTO.getRefferal());

                            transaction.setCommissionId(commissionId);
                            transaction.setValue(DoubleRounder.round(sellValue * 1, 2));

                            // decremento budget Affiliato
                            AffiliateBudgetDTO bb = affiliateBudgetBusiness.getByIdCampaignAndIdAffiliate(refferal.getCampaignId(), refferal.getAffiliateId()).stream().findFirst().orElse(null);
                            if (bb != null && bb.getBudget() != null && (bb.getBudget() - sellValue) < 0)
                                transaction.setDictionaryId(47L);

                            // Stato Budget Campagna
                            CampaignBudgetDTO campBudget = campaignBudgetBusiness.searchByCampaignAndDate(refferal.getCampaignId(), transaction.getDateTime().toLocalDate()).stream().findFirst().orElse(null);
                            if (campBudget != null && campBudget.getBudgetErogato() != null) {
                                Double budgetCampagna = campBudget.getBudgetErogato() - sellValue;
                                // setto stato transazione a ovebudget editore se totale < 0
                                if (budgetCampagna < 0) {
                                    transaction.setDictionaryId(48L);
                                }
                            }
                            //setto pending
                            transaction.setStatusId(72L);
                        }

                                   // creo la transazione
                        TransactionCPSDTO cps = transactionCPSBusiness.createCps(transaction);
                        log.info(">>> CREATO TRANSAZIONE :::: CPS :::: {} ", cps.getId());

                        // setto a gestito
                        cpsBusiness.setRead(cpsDTO.getId());

                    } catch (Exception ecc) {
                        log.error("\n\n\n >>>>>>>>>>>>> ECCEZIONE CPS : >>>>>>>>>>>>> ", ecc);
                    }
                }// creo solo se ho affiliate
            });
        } catch (Exception e) {
            log.error("Eccezione Scheduler CPS --  {}", e.getMessage(), e);
        }
    }//trasformaTrackingCPS

    /**
     * ============================================================================================================
     **/

    public void gestisciBlacklisted() {
        try {

            cpsBusiness.getUnreadBlacklisted().stream().filter(cpsDTO -> StringUtils.isNotBlank(cpsDTO.getRefferal())).forEach(cpsDTO -> {

                if (cpsDTO.getRefferal().length() < 6) {
                    // cerco da cpc
                    List<CpcDTO> ips = cpcBusiness.findByIp24HoursBefore(cpsDTO.getIp(), cpsDTO.getDate(), cpsDTO.getRefferal()).stream().collect(Collectors.toList());
                    // prendo ultimo ip
                    for (CpcDTO dto : ips)
                        if (StringUtils.isNotBlank(dto.getRefferal())) cpsDTO.setRefferal(dto.getRefferal());
                }

                // prendo reffereal e lo leggo
                Refferal refferal = referralService.decodificaReferral(cpsDTO.getRefferal());
                if (refferal != null && refferal.getAffiliateId() != null) {
                    log.debug(">>>> BLACLISTED CPS :: {} :: {}", cpsDTO, refferal);

                    //aggiorno dati CPS
                    Cps cccps = cpsRepository.findById(cpsDTO.getId()).orElseThrow(() -> new ElementCleveradException("Cps", cpsDTO.getId()));
                    cccps.setMediaId(refferal.getMediaId());
                    cccps.setCampaignId(refferal.getCampaignId());
                    cccps.setAffiliateId(refferal.getAffiliateId());
                    cccps.setChannelId(refferal.getChannelId());
                    cccps.setTargetId(refferal.getTargetId());
                    cpsRepository.save(cccps);

                    // setta transazione
                    TransactionCPSBusiness.BaseCreateRequest transaction = new TransactionCPSBusiness.BaseCreateRequest();
                    transaction.setAffiliateId(refferal.getAffiliateId());
                    transaction.setCampaignId(refferal.getCampaignId());
                    transaction.setChannelId(refferal.getChannelId());
                    transaction.setMediaId(refferal.getMediaId());
                    transaction.setDateTime(cpsDTO.getDate());
                    transaction.setApproved(false);
                    transaction.setPayoutPresent(false);

                    if (StringUtils.isNotBlank(cpsDTO.getAgent())) transaction.setAgent(cpsDTO.getAgent());
                    else transaction.setAgent("");

                    transaction.setIp(cpsDTO.getIp());
                    transaction.setData(cpsDTO.getData().trim().replace("[REPLACE]", ""));
                    transaction.setMediaId(refferal.getMediaId());

                    // controlla data scadneza camapgna
                    try {
                        // associo a wallet
                        Long affiliateID = refferal.getAffiliateId();
                        if (affiliateID != null) {
                            transaction.setWalletId(walletRepository.findByAffiliateId(affiliateID).getId());
                        }

                        // trovo revenue
                        RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(refferal.getCampaignId(), 51L);
                        if (rf != null) {
                            transaction.setRevenueId(rf.getId());
                        } else {
                            log.warn("Non trovato revenue factor di tipo 51 per campagna {} , setto default", refferal.getCampaignId());
                            transaction.setRevenueId(2L);
                        }

                        // gesione commisione
                        Double commVal = 0D;
                        AffiliateChannelCommissionCampaignBusiness.Filter req = new AffiliateChannelCommissionCampaignBusiness.Filter();
                        req.setAffiliateId(refferal.getAffiliateId());
                        req.setChannelId(refferal.getChannelId());
                        req.setCampaignId(refferal.getCampaignId());
                        req.setCommissionDicId(51L);
                        AffiliateChannelCommissionCampaignDTO acccFirst = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);
                        if (acccFirst != null) {
                            commVal = acccFirst.getCommissionValue();
                            transaction.setCommissionId(acccFirst.getCommissionId());
                        } else {
                            log.warn("Non trovato Commission di tipo 10 per campagna {}, setto default", refferal.getCampaignId());
                            transaction.setCommissionId(0L);
                        }

                        transaction.setValue(commVal * 1);
                        transaction.setLeadNumber(1L);

                        //setto rifiutato
                        transaction.setStatusId(74L);
                        // setto blacklisted
                        transaction.setDictionaryId(70L);

                        // creo la transazione
                        TransactionCPSDTO cps = transactionCPSBusiness.createCps(transaction);
                        log.info(">>>BLACKLIST :::: CPS :::: {} ", cps.getId());

                        // setto a gestito
                        cpsBusiness.setRead(cpsDTO.getId());
                    } catch (Exception ecc) {
                        log.error("BLACKLIS ECCEZIONE CPS :> ", ecc);
                    }
                }// creo solo se ho affiliate
            });
        } catch (Exception e) {
            log.error("Eccezione Scheduler CPS --  {}", e.getMessage(), e);
        }
    }//gestisciBlacklisted

}