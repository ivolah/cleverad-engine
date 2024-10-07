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
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    @Scheduled(cron = "4 */4 * * * ?")
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
            cplBusiness.getUnreadFromStartOfTheDay().stream().filter(cplDTO -> StringUtils.isNotBlank(cplDTO.getRefferal())).forEach(cplDTO -> {
                log.info("-->-->--> CPL {}", cplDTO.getId());

                String info = cplDTO.getInfo();
                String source = cplDTO.getSource();
                String rndTrs =null;
                if(!info.isEmpty()) {
                    Pattern rndTrsPattern = Pattern.compile("rndTrs: (\\d+)");
                    Matcher rndTrsMatcher = rndTrsPattern.matcher(info);
                    rndTrs = rndTrsMatcher.find() ? rndTrsMatcher.group(1).trim() : null;
                }
                if (source.equals("pixel")) {
                    Cpc cpc = null;

                    if (rndTrs != null) {
                        // Fetch CPC by rndTrs only if found
                        List<Cpc> cpcList = cpcBusiness.findByRndTrs(Long.parseLong(rndTrs));
                        if (!cpcList.isEmpty()) {
                            cpc = cpcList.get(0);
                            log.info(">>> TROVATO CPC {} CON RNDTRS {} e referral {} <<<", cpc.getId(), cpc.getRndTrs(), cpc.getRefferal());
                            cplDTO.setRefferal(cpc.getRefferal());
                            cplDTO.setCpcId(cpc.getId());
                        }
                    }

                    if (cplDTO.getCpcId() == null) {
                        // Search for CPC by IP in the last 2 hours
                        cpcBusiness.findByIp2HoursBefore(cplDTO.getIp(), cplDTO.getDate(), cplDTO.getRefferal()).stream()
                                .filter(cpcDTO -> StringUtils.isNotBlank(cpcDTO.getRefferal()))
                                .findFirst()
                                .ifPresent(cpcDTO -> {
                                    log.info("PIXEL IP-CPC: Found CPC ID {} with referral {}", cpcDTO.getId(), cpcDTO.getRefferal());
                                    cplDTO.setRefferal(cpcDTO.getRefferal());
                                    cplDTO.setCpcId(cpcDTO.getId());
                                });
                    }

                } else if (source.equals("s2s")) {
                    //S2S -  giro senza controllare IP address
                    log.info("S2S S2S S2S S2S S2S S2S");

                    Cpc cpc = null;
                    if (rndTrs != null) {
                        // Fetch CPC by rndTrs only if found
                        List<Cpc> cpcList = cpcBusiness.findByRndTrs(Long.parseLong(rndTrs));
                        if (!cpcList.isEmpty()) {
                            cpc = cpcList.get(0);
                            log.info(">>> TROVATO CPC {} CON RNDTRS {} e referral {} <<<", cpc.getId(), cpc.getRndTrs(), cpc.getRefferal());
                            cplDTO.setRefferal(cpc.getRefferal());
                            cplDTO.setCpcId(cpc.getId());
                        }
                    }

                    if (cpc == null) {
                        // Search CPC without checking IP for transactions 15 minutes before
                        List<Cpc> cpcListWithoutIp = cpcBusiness.findByIp15MinutesBeforeNoIp(cplDTO.getDate(), cplDTO.getRefferal())
                                .stream()
                                .filter(cpcDTO -> StringUtils.isNotBlank(cpcDTO.getRefferal()))
                                .collect(Collectors.toList());

                        if (!cpcListWithoutIp.isEmpty()) {
                            Cpc firstCpc = cpcListWithoutIp.get(0);
                            long transactionCount = transactionCPLBusiness.countByCpcId(firstCpc.getId());
                            log.info("NO-IP CPC {} : ORIG {} --> CPC {} - used {} times", firstCpc.getId(), cplDTO.getRefferal(), firstCpc.getRefferal(), transactionCount);
                            if (transactionCount == 0) {
                                cplDTO.setRefferal(firstCpc.getRefferal());
                                cplDTO.setCpcId(firstCpc.getId());
                            }
                        }
                    }
                }

                Long idCpc = cplDTO.getCpcId();
                cplBusiness.setCpcId(cplDTO.getId(), idCpc);
                // Decode referral
                Refferal referral = referralService.decodificaReferral(cplDTO.getRefferal());

                log.trace("Refferal :: {} -- ID CPC {}", cplDTO.getRefferal(), idCpc);
                log.trace(">>>> T-CPL :: {} :: {}", cplDTO, referral);

                if (referral != null) {
                    // Fetch CPL and check for presence
                    Cpl cpl = cplRepository.findById(cplDTO.getId())
                            .orElseThrow(() -> new ElementCleveradException("Cpl", cplDTO.getId()));

                    // Update CPL fields if necessary
                    boolean isUpdated = false;

                    // Safely update CPL fields by checking nulls from Refferal
                    if (!Objects.equals(referral.getMediaId(), cpl.getMediaId())) {
                        cpl.setMediaId(referral.getMediaId());
                        isUpdated = true;
                    }
                    if (!Objects.equals(referral.getCampaignId(), cpl.getCampaignId())) {
                        cpl.setCampaignId(referral.getCampaignId());
                        isUpdated = true;
                    }
                    if (!Objects.equals(referral.getAffiliateId(), cpl.getAffiliateId())) {
                        cpl.setAffiliateId(referral.getAffiliateId());
                        isUpdated = true;
                    }
                    if (!Objects.equals(referral.getChannelId(), cpl.getChannelId())) {
                        cpl.setChannelId(referral.getChannelId());
                        isUpdated = true;
                    }
                    if (!Objects.equals(referral.getTargetId(), cpl.getTargetId())) {
                        cpl.setTargetId(referral.getTargetId());
                        isUpdated = true;
                    }

                    // Replace placeholder data if necessary
                    if ("[REPLACE]".equals(cpl.getData())) {
                        cpl.setData("");
                        cplDTO.setData("");
                        isUpdated = true;
                    }

                    // Check and update `info` only if CPC ID is not null and `info` is blank
                    if (StringUtils.isBlank(cpl.getInfo()) && idCpc != null) {
                        String cpcInfo = cpcBusiness.findById(idCpc).getInfo();
                        cpl.setInfo(cpcInfo);
                        cplDTO.setInfo(cpcInfo);
                        isUpdated = true;
                    }

                    // Save CPL only if there are updates
                    if (isUpdated) {
                        cplRepository.save(cpl);
                    }
                }
                
                if (referral != null && referral.getAffiliateId() != null) {
                    // setta transazione
                    TransactionCPLBusiness.BaseCreateRequest transaction = new TransactionCPLBusiness.BaseCreateRequest();
                    transaction.setRefferal(cplDTO.getRefferal());
                    transaction.setAffiliateId(referral.getAffiliateId());
                    transaction.setCampaignId(referral.getCampaignId());
                    transaction.setChannelId(referral.getChannelId());
                    transaction.setMediaId(referral.getMediaId());
                    transaction.setDateTime(cplDTO.getDate());
                    transaction.setApproved(true);
                    transaction.setPayoutPresent(false);
                    transaction.setLeadNumber(1L);

                    if (StringUtils.isNotBlank(cplDTO.getAgent())) transaction.setAgent(cplDTO.getAgent());
                    else transaction.setAgent("");

                    transaction.setIp(cplDTO.getIp());
                    transaction.setData(cplDTO.getData().trim().replace("[REPLACE]", ""));
                    transaction.setMediaId(referral.getMediaId());

                    // controlla data scadneza camapgna
                    CampaignDTO campaignDTO = campaignBusiness.findByIdAdmin(referral.getCampaignId());
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

                    if (!campaignDTO.getStatus()) {
                        // setto a campagna scaduta
                        transaction.setDictionaryId(49L);
                        scaduta = true;
                    }

                    // associo a wallet
                    Long affiliateID = referral.getAffiliateId();
                    Long walletID;
                    if (affiliateID != null) {
                        walletID = walletRepository.findByAffiliateId(affiliateID).getId();
                        transaction.setWalletId(walletID);
                    }

//
//                    //                    HACK
                    if (!scaduta && cplDTO.getData() != null && cplDTO.getData().contains("%pord=!?")) {
                        scaduta = true;
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
                            RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(referral.getCampaignId(), 11L);
                            if (rf != null) {
                                transaction.setRevenueId(rf.getId());
                            } else {
                                log.warn("Non trovato revenue factor di tipo 11 per campagna {} , setto default", referral.getCampaignId());
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
                            req.setAffiliateId(referral.getAffiliateId());
                            req.setChannelId(referral.getChannelId());
                            req.setCampaignId(referral.getCampaignId());
                            req.setBlocked(false);
                            req.setCommissionDicId(11L);
                            AffiliateChannelCommissionCampaignDTO acccFirst = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);
                            if (acccFirst != null) {
                                commVal = acccFirst.getCommissionValue();
                                commissionId = acccFirst.getCommissionId();
                            } else
                                log.warn("No Commission CPL (campagna {} e affilitato {}), setto default ({})", referral.getCampaignId(), referral.getAffiliateId(), cplDTO.getRefferal());
                        }
                        transaction.setCommissionId(commissionId);

                        Double totale = commVal * 1;
                        transaction.setValue(DoubleRounder.round(totale, 2));

                        // incemento valore schedled

                        // decremento budget Affiliato
                        AffiliateBudgetDTO bb = affiliateBudgetBusiness.getByIdCampaignAndIdAffiliate(referral.getCampaignId(), referral.getAffiliateId()).stream().findFirst().orElse(null);
                        if (bb != null && bb.getBudget() != null && (bb.getBudget() - totale) < 0)
                            transaction.setDictionaryId(47L);

                        // Stato Budget Campagna
                        CampaignBudgetDTO campBudget = campaignBudgetBusiness.searchByCampaignAndDate(referral.getCampaignId(), transaction.getDateTime().toLocalDate()).stream().findFirst().orElse(null);
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
                    TransactionCPLDTO cplT = transactionCPLBusiness.createCpl(transaction);
                    log.info(">>> LEAD ({}-->{}) :::::: {}", cplDTO.getId(), cplT.getId(), cplDTO);


                    // verifico il Postback ed eventualemnte faccio chiamata
                    // solo se campagna attiva
                    if (transaction.getDictionaryId() != 49L) {
                        //not manuale
                        if (transaction.getManualDate() == null) {
                            List<CampaignAffiliateDTO> campaignAffiliateDTOS = campaignAffiliateBusiness.searchByAffiliateIdAndCampaignId(referral.getAffiliateId(), referral.getCampaignId()).toList();
                            campaignAffiliateDTOS.forEach(campaignAffiliateDTO -> {
                                // cerco global
                                String globalPixel = affiliateBusiness.getGlobalPixel(affiliateID);
                                // cerco folow through
                                String followT = campaignAffiliateDTO.getFollowThrough();
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
                                        log.error("Eccezione chianata Post Back : NON FACCIO NULLA :", e);
                                        //throw new RuntimeException(e);
                                    }
                                }
                            });
                        }
                    } else {
                        log.warn("Campagna scaduta non faccio postback :: {}", cplT.getId());
                    }

                }// creo solo se ho affiliate
                else if (referral != null && !referral.getSuccess()) {
                    log.warn("Errore decodifica referral :: {}", cplDTO.getRefferal());
                    cplBusiness.setRead(cplDTO.getId());
                }

                // setto a gestito
                cplBusiness.setRead(cplDTO.getId());

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
                    List<CpcDTO> ips = cpcBusiness.findByIp2HoursBefore(cplDTO.getIp(), cplDTO.getDate(), cplDTO.getRefferal()).stream().collect(Collectors.toList());
                    // prendo ultimo ip
                    for (CpcDTO dto : ips)
                        if (StringUtils.isNotBlank(dto.getRefferal())) cplDTO.setRefferal(dto.getRefferal());
                }

                // prendo reffereal e lo leggo
                Refferal referral = referralService.decodificaReferral(cplDTO.getRefferal());
                if (referral != null && referral.getAffiliateId() != null) {
                    log.debug(">>>> BLACLISTED CPL :: {} :: {}", cplDTO, referral);

                    //aggiorno dati CPL
                    Cpl cccpl = cplRepository.findById(cplDTO.getId()).orElseThrow(() -> new ElementCleveradException("Cpl", cplDTO.getId()));
                    cccpl.setMediaId(referral.getMediaId());
                    cccpl.setCampaignId(referral.getCampaignId());
                    cccpl.setAffiliateId(referral.getAffiliateId());
                    cccpl.setChannelId(referral.getChannelId());
                    cccpl.setTargetId(referral.getTargetId());
                    cplRepository.save(cccpl);

                    // setta transazione
                    TransactionCPLBusiness.BaseCreateRequest transaction = new TransactionCPLBusiness.BaseCreateRequest();
                    transaction.setAffiliateId(referral.getAffiliateId());
                    transaction.setCampaignId(referral.getCampaignId());
                    transaction.setChannelId(referral.getChannelId());
                    transaction.setMediaId(referral.getMediaId());
                    transaction.setDateTime(cplDTO.getDate());
                    transaction.setApproved(false);
                    transaction.setPayoutPresent(false);

                    if (StringUtils.isNotBlank(cplDTO.getAgent())) transaction.setAgent(cplDTO.getAgent());
                    else transaction.setAgent("");

                    transaction.setIp(cplDTO.getIp());
                    transaction.setData(cplDTO.getData().trim().replace("[REPLACE]", ""));
                    transaction.setMediaId(referral.getMediaId());

                    try {
                        // associo a wallet
                        Long affiliateID = referral.getAffiliateId();
                        if (affiliateID != null) {
                            transaction.setWalletId(walletRepository.findByAffiliateId(affiliateID).getId());
                        }

                        // trovo revenue
                        RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(referral.getCampaignId(), 11L);
                        if (rf != null) {
                            transaction.setRevenueId(rf.getId());
                        } else {
                            log.warn("Non trovato revenue factor di tipo 11 per campagna {} , setto default", referral.getCampaignId());
                            transaction.setRevenueId(2L);
                        }

                        // gesione commisione
                        Double commVal = 0D;
                        AffiliateChannelCommissionCampaignBusiness.Filter req = new AffiliateChannelCommissionCampaignBusiness.Filter();
                        req.setAffiliateId(referral.getAffiliateId());
                        req.setChannelId(referral.getChannelId());
                        req.setCampaignId(referral.getCampaignId());
                        req.setCommissionDicId(11L);
                        AffiliateChannelCommissionCampaignDTO acccFirst = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);
                        if (acccFirst != null) {
                            commVal = acccFirst.getCommissionValue();
                            transaction.setCommissionId(acccFirst.getCommissionId());
                        } else {
                            log.warn("Non trovato Commission di tipo 10 per campagna {}, setto default", referral.getCampaignId());
                            transaction.setCommissionId(0L);
                        }

                        transaction.setValue(commVal * 1);
                        transaction.setLeadNumber(1L);

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
                else if (referral != null && !referral.getSuccess()) {
                    log.warn("Errore decodifica referral :: {}", cplDTO.getRefferal());
                    cplBusiness.setRead(cplDTO.getId());
                }

            });
        } catch (Exception e) {
            log.error("Eccezione Scheduler CPL --  {}", e.getMessage(), e);
        }
    }//gestisciBlacklisted

}