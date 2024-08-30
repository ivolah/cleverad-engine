package it.cleverad.engine.scheduled.manage;

import it.cleverad.engine.business.*;
import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.Commission;
import it.cleverad.engine.persistence.model.service.RevenueFactor;
import it.cleverad.engine.persistence.model.tracking.Cpc;
import it.cleverad.engine.persistence.model.tracking.Cpl;
import it.cleverad.engine.persistence.repository.service.CommissionRepository;
import it.cleverad.engine.persistence.repository.service.RevenueFactorRepository;
import it.cleverad.engine.persistence.repository.service.WalletRepository;
import it.cleverad.engine.persistence.repository.tracking.CplRepository;
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

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
    private AffiliateBudgetBusiness affiliateBudgetBusiness;
    @Autowired
    private CampaignBusiness campaignBusiness;
    @Autowired
    private AffiliateBusiness affiliateBusiness;
    @Autowired
    private CampaignAffiliateBusiness campaignAffiliateBusiness;
    @Autowired
    private RevenueFactorBusiness revenueFactorBusiness;
    @Autowired
    private RevenueFactorRepository revenueFactorRepository;
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
                cpcBusiness.findByIp24HoursBefore(cplDTO.getIp(), cplDTO.getDate(), cplDTO.getRefferal()).stream().filter(cpcDTO -> StringUtils.isNotBlank(cpcDTO.getRefferal())).forEach(cpcDTO -> {
                    log.trace("R ORIG {} --> R CPC {}", cplDTO.getRefferal(), cpcDTO.getRefferal());
                    cplDTO.setRefferal(cpcDTO.getRefferal());
                    cplDTO.setCpcId(cpcDTO.getId());
                });

                // giro senza controllare IP address
                if (cplDTO.getCpcId() == null) {
                    List<Cpc> listaSenzaIp = cpcBusiness.findByIp1HoursBeforeNoIp(cplDTO.getDate(), cplDTO.getRefferal()).stream().filter(cpcDTO -> StringUtils.isNotBlank(cpcDTO.getRefferal())).collect(Collectors.toList());
                    if (listaSenzaIp.size() > 0) {
                        //check id cpc non usato in transazioni cpl come cpcid
                        long numerositatitudine = transactionCPLBusiness.countByCpcId(listaSenzaIp.get(0).getId());
                        log.trace("NO IP CPC {} Ref ORIG {} --> Ref CPC {} - CPCID USATO {}", listaSenzaIp.get(0).getId(), cplDTO.getRefferal(), listaSenzaIp.get(0).getRefferal(), numerositatitudine);
                        if (numerositatitudine == 0) {
                            cplDTO.setRefferal(listaSenzaIp.get(0).getRefferal());
                            cplDTO.setCpcId(listaSenzaIp.get(0).getId());
                        }
                    }
                }

                Long idCpc = cplDTO.getCpcId();
                log.trace("Refferal :: {} con ID CPC {}", cplDTO.getRefferal(), idCpc);
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
                    if (cccpl.getData().equals("[REPLACE]")) {
                        cccpl.setData("");
                        cplDTO.setData("");
                    }
                    if (StringUtils.isBlank(cccpl.getInfo()) && idCpc != null) {
                        CpcDTO cpc = cpcBusiness.findById(idCpc);
                        cccpl.setInfo(cpc.getInfo());
                        cplDTO.setInfo(cpc.getInfo());
                    }
                    cplRepository.save(cccpl);

                    // setta transazione
                    TransactionCPLBusiness.BaseCreateRequest transaction = new TransactionCPLBusiness.BaseCreateRequest();
                    transaction.setRefferal(cplDTO.getRefferal());
                    transaction.setAffiliateId(refferal.getAffiliateId());
                    transaction.setCampaignId(refferal.getCampaignId());
                    transaction.setChannelId(refferal.getChannelId());
                    transaction.setMediaId(refferal.getMediaId());
                    transaction.setDateTime(cplDTO.getDate());
                    transaction.setApproved(true);
                    transaction.setPayoutPresent(false);
                    transaction.setLeadNumber(1L);

                    if (StringUtils.isNotBlank(cplDTO.getAgent())) transaction.setAgent(cplDTO.getAgent());
                    else transaction.setAgent("");

                    transaction.setIp(cplDTO.getIp());
                    transaction.setData(cplDTO.getData().trim().replace("[REPLACE]", ""));
                    transaction.setMediaId(refferal.getMediaId());

                    // controlla data scadneza camapgna
                    CampaignDTO campaignDTO = campaignBusiness.findByIdAdmin(refferal.getCampaignId());
                    Boolean scaduta = false;
                    LocalDate endDate = campaignDTO.getEndDate();
                    if (endDate.isBefore(LocalDate.now())) {
                        // setto a campagna scaduta
                        transaction.setDictionaryId(49L);
                        scaduta = true;
                    } else {
                        //setto pending
                        transaction.setDictionaryId(42L);
                    }

                    if (campaignDTO.getStatus() == false) {
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

                        // check Action ID
                        boolean checkAction = false;
                        if (StringUtils.isNotBlank(cplDTO.getActionId()) && (cplDTO.getActionId().equals("0"))) {
                            log.info(">>>>>>>>>> TROVATO ACTION : {}", cplDTO.getActionId());
                            checkAction = true;

                        }

                        // trovo revenue
                        if (checkAction) {
                            RevenueFactor rf = revenueFactorRepository.findFirstByActionAndStatus(cplDTO.getActionId(), true);
                            if (rf != null) {
                                transaction.setRevenueId(rf.getId());
                                log.warn("Revenue >{}< trovata da action ID >{}<", rf.getId(), cplDTO.getActionId());
                            } else {
                                checkAction = false;
                            }
                        }
                        if (!checkAction) {
                            // GIRO STANDARD
                            RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(refferal.getCampaignId(), 11L);
                            if (rf != null) {
                                transaction.setRevenueId(rf.getId());
                            } else {
                                log.warn("Non trovato revenue factor di tipo 11 per campagna {} , setto default", refferal.getCampaignId());
                                transaction.setRevenueId(2L);
                            }
                        }

                        // gesione commisione
                        Double commVal = 0D;
                        Long commissionId = 0L;
                        AffiliateChannelCommissionCampaignBusiness.Filter req = new AffiliateChannelCommissionCampaignBusiness.Filter();
                        if (checkAction) {
                            // TODO SE ACTION ID IDENTICI??? COSA FACCIAMO
                            Commission cm = commissionRepository.findFirstByActionAndStatus(cplDTO.getActionId(), true);
                            if (cm != null) {
                                commissionId = cm.getId();
                                commVal = cm.getValue();
                                log.warn("Commissione >{}< trovata da action ID >{}<", cm.getId(), cplDTO.getActionId());
                            } else {
                                checkAction = false;
                            }
                        }
                        if (!checkAction) {
                            // non è settato l'actionId allora faccio il solito giro
                            req.setAffiliateId(refferal.getAffiliateId());
                            req.setChannelId(refferal.getChannelId());
                            req.setCampaignId(refferal.getCampaignId());
                            req.setBlocked(false);
                            req.setCommissionDicId(11L);
                            AffiliateChannelCommissionCampaignDTO acccFirst = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);
                            if (acccFirst != null) {
                                commVal = acccFirst.getCommissionValue();
                                commissionId = acccFirst.getCommissionId();
                            } else
                                log.warn("No Commission CPL (campagna {} e affilitato {}), setto default ({})", refferal.getCampaignId(), refferal.getAffiliateId(), cplDTO.getRefferal());
                        }
                        transaction.setCommissionId(commissionId);

                        Double totale = commVal * 1;
                        transaction.setValue(DoubleRounder.round(totale, 2));

                        // incemento valore schedled

                        // decremento budget Affiliato
                        AffiliateBudgetDTO bb = affiliateBudgetBusiness.getByIdCampaignAndIdAffiliate(refferal.getCampaignId(), refferal.getAffiliateId()).stream().findFirst().orElse(null);
                        if (bb != null && bb.getBudget() != null && (bb.getBudget() - totale) < 0)
                            transaction.setDictionaryId(47L);

                        // Stato Budget Campagna
                        CampaignBudgetDTO campBudget = campaignBudgetBusiness.searchByCampaignAndDate(refferal.getCampaignId(), transaction.getDateTime().toLocalDate()).stream().findFirst().orElse(null);
                        if (campBudget != null && campBudget.getBudgetErogato() != null) {
                            Double budgetCampagna = campBudget.getBudgetErogato() - totale;
                            // setto stato transazione a ovebudget editore se totale < 0
                            if (budgetCampagna < 0) {
                                transaction.setDictionaryId(48L);
                            }
                        }

                        //setto pending
                        transaction.setStatusId(72L);
                    }

                    // setto id CPC
                    transaction.setCpcId(idCpc);
                    // setto id CPL
                    transaction.setCplId(cplDTO.getId());

                    // creo la transazione
                    TransactionCPLDTO cpl = transactionCPLBusiness.createCpl(transaction);
                    log.info(">>> CREATO TRANSAZIONE :::: CPL :::: {} ", cpl.getId());

                    // setto a gestito
                    cplBusiness.setRead(cplDTO.getId());

                    // verifico il Postback ed eventualemnte faccio chiamata
                    // solo se campagna attiva
                    if (transaction.getDictionaryId() != 49L) {
                        //not manuale
                        if (transaction.getManualDate() == null) {
                            List<CampaignAffiliateDTO> campaignAffiliateDTOS = campaignAffiliateBusiness.searchByAffiliateIdAndCampaignId(refferal.getAffiliateId(), refferal.getCampaignId()).toList();
                            campaignAffiliateDTOS.stream().forEach(campaignAffiliateDTO -> {
                                // cerco global
                                String globalPixel = affiliateBusiness.getGlobalPixel(affiliateID);
                                // cerco folow through
                                String followT = campaignAffiliateDTO.getFollowThrough();
                                String info = cplDTO.getInfo();
                                String data = cplDTO.getData();
                                String url = "";
                                log.trace("POST BACK ::: " + followT + " :: " + globalPixel + " :: " + info + " :: " + data);
                                if (StringUtils.isNotBlank(globalPixel)) {
                                    url = globalPixel;
                                } else if (StringUtils.isNotBlank(followT)) {
                                    url = followT;
                                }
                                if (StringUtils.isNotBlank(url)) {
                                    // trovo tutte le chiavi
                                    Map<String, String> keyValueMap = referralService.estrazioneInfo(info);
                                    // aggiungo order_id / transaction_id
                                    keyValueMap.put("orderid", data);
                                    keyValueMap.put("order_id", data);
                                    url = referralService.replacePlaceholders(url, keyValueMap);
                                    log.trace("URL :: " + url);
                                    try {
                                        URL urlGet = new URL(url);
                                        HttpURLConnection con = (HttpURLConnection) urlGet.openConnection();
                                        con.setRequestMethod("GET");
                                        con.getInputStream();
                                        log.info("Post Back (" + con.getResponseCode() + ") : " + url + " :::: GP (" + globalPixel + ") :: INFO (" + info + ") :: DATA (" + data + "");
                                    } catch (Exception e) {
                                        log.error("Eccezione chianata Post Back", e);
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                        }
                    } else {
                        log.warn("Campagna scaduta non faccio postback :: {}", cpl.getId());
                    }

                }// creo solo se ho affiliate
                else if (refferal != null && refferal.getSuccess() == false) {
                    log.warn("Errore decodifica refferal :: {}", cplDTO.getRefferal());
                    cplBusiness.setRead(cplDTO.getId());
                }

            });
        } catch (Exception e) {
            log.error("MANAGE CPL EXCEPTION --  {}", e.getMessage(), e);
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
                    transaction.setData(cplDTO.getData().trim().replace("[REPLACE]", ""));
                    transaction.setMediaId(refferal.getMediaId());

                    try {
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
                else if (refferal != null && refferal.getSuccess() == false) {
                    log.warn("Errore decodifica refferal :: {}", cplDTO.getRefferal());
                    cplBusiness.setRead(cplDTO.getId());
                }

            });
        } catch (Exception e) {
            log.error("Eccezione Scheduler CPL --  {}", e.getMessage(), e);
        }
    }//gestisciBlacklisted

}