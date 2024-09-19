package it.cleverad.engine.business;

import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.Commission;
import it.cleverad.engine.persistence.model.service.QueryTransaction;
import it.cleverad.engine.persistence.model.service.RevenueFactor;
import it.cleverad.engine.persistence.model.tracking.Cpc;
import it.cleverad.engine.persistence.model.tracking.Cpl;
import it.cleverad.engine.persistence.repository.service.CommissionRepository;
import it.cleverad.engine.persistence.repository.service.RevenueFactorRepository;
import it.cleverad.engine.persistence.repository.service.WalletRepository;
import it.cleverad.engine.persistence.repository.tracking.CplRepository;
import it.cleverad.engine.service.ReferralService;
import it.cleverad.engine.web.dto.*;
import it.cleverad.engine.web.exception.ElementCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class RigeneraCPLBusiness {

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
    @Autowired
    private TransactionStatusBusiness transactionStatusBusiness;
    @Autowired
    private WalletBusiness walletBusiness;
    @Autowired
    private RevenueFactorRepository revenueFactorRepositorye;

    public void rigenera(Integer anno, Integer mese, Integer giorno, Long affiliateId, Long camapignId, Boolean postback) {
        try {

            int start = (giorno == null) ? 1 : giorno;
            int end = (giorno == null) ? LocalDate.of(anno, mese, 1).lengthOfMonth() : giorno;
            LocalDate dataDaGestireStart = LocalDate.of(anno, mese, start);
            LocalDate dataDaGestireEnd = LocalDate.of(anno, mese, end);
            log.info(anno + "-" + mese + "-" + giorno + " >> " + dataDaGestireStart + " || " + dataDaGestireEnd + " per " + affiliateId + " e " + camapignId);

            // cancello le transazioni
            TransactionStatusBusiness.QueryFilter request = new TransactionStatusBusiness.QueryFilter();
            request.setCreationDateFrom(dataDaGestireStart);
            request.setCreationDateTo(dataDaGestireEnd);
            request.setTipo("CPL");
            if (affiliateId != null) request.setAffiliateId(affiliateId);
            if (camapignId != null) request.setCampaignId(camapignId);
            List<Long> not = new ArrayList<>();
            not.add(68L); // MANUALE
            request.setNotInDictionaryId(not);
            not = new ArrayList<>();
            not.add(74L); // RIGETTATO
            request.setNotInStausId(not);
            Page<QueryTransaction> ls = transactionStatusBusiness.searchPrefiltratoN(request, Pageable.ofSize(Integer.MAX_VALUE));
            log.info(">>> TOT :: " + ls.getTotalElements());
            for (QueryTransaction tcpl : ls) {
                log.info("CANCELLO PER RIGENERA CPL :: {} : {} :: {}", tcpl.getid(), tcpl.getValue(), tcpl.getDateTime());
                transactionCPLBusiness.delete(tcpl.getid());
                Thread.sleep(50L);
            }

            for (int gg = start; gg <= end; gg++) {
                log.info("RIGENERO GIORNO {}-{}-{}", anno, mese, gg);
                cplBusiness.getAllDay(anno, mese, gg, affiliateId, camapignId).stream().filter(cplDTO -> StringUtils.isNotBlank(cplDTO.getRefferal())).forEach(cplDTO -> {

                    log.info("ID {}", cplDTO.getId());

                    // leggo sempre i cpc precedenti per trovare il click riferito alla lead
                    cpcBusiness.findByIp24HoursBefore(cplDTO.getIp(), cplDTO.getDate(), cplDTO.getRefferal()).stream().filter(cpcDTO -> StringUtils.isNotBlank(cpcDTO.getRefferal())).forEach(cpcDTO -> {
                        cplDTO.setRefferal(cpcDTO.getRefferal());
                        cplDTO.setCpcId(cpcDTO.getId());
                    });

                    // giro senza controllare IP address
                    if (cplDTO.getCpcId() == null) {
                        List<Cpc> listaSenzaIp = cpcBusiness.findByIp1HoursBeforeNoIp(cplDTO.getDate(), cplDTO.getRefferal()).stream().filter(cpcDTO -> StringUtils.isNotBlank(cpcDTO.getRefferal())).collect(Collectors.toList());
                        if (!listaSenzaIp.isEmpty()) {
                            //check id cpc non usato in transazioni cpl come cpcid
                            long numerositatitudine = transactionCPLBusiness.countByCpcId(listaSenzaIp.get(0).getId());
                            log.info("NO-IP CPC {} : ORIG {} --> CPC {} - used: {}", listaSenzaIp.get(0).getId(), cplDTO.getRefferal(), listaSenzaIp.get(0).getRefferal(), numerositatitudine);
                            if (numerositatitudine == 0) {
                                cplDTO.setRefferal(listaSenzaIp.get(0).getRefferal());
                                cplDTO.setCpcId(listaSenzaIp.get(0).getId());
                            }
                        }
                    }

                    log.trace("Refferal :: {}  con ID CPC {}", cplDTO.getRefferal(), cplDTO.getCpcId());
                    cplBusiness.setCpcId(cplDTO.getId(), cplDTO.getCpcId());

                    // prendo reffereal e lo leggo
                    Refferal refferal = referralService.decodificaReferral(cplDTO.getRefferal());
                    log.info(">>>>RR T-CPL :: {} :: ", cplDTO, refferal);
                    //aggiorno dati CPL
                    Cpl cccpl = cplRepository.findById(cplDTO.getId()).orElseThrow(() -> new ElementCleveradException("Cpl", cplDTO.getId()));
                    cccpl.setMediaId(refferal.getMediaId());
                    cccpl.setCampaignId(refferal.getCampaignId());
                    cccpl.setAffiliateId(refferal.getAffiliateId());
                    cccpl.setChannelId(refferal.getChannelId());
                    cccpl.setTargetId(refferal.getTargetId());
                    cplRepository.save(cccpl);

                    if (refferal != null && refferal.getAffiliateId() != null) {
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
                        transaction.setIp(cplDTO.getIp());
                        transaction.setData(cplDTO.getData().trim().replace("[REPLACE]", ""));
                        transaction.setMediaId(refferal.getMediaId());
                        transaction.setCpcId(cplDTO.getCpcId());
                        transaction.setCplId(cplDTO.getId());

                        if (StringUtils.isNotBlank(cplDTO.getAgent())) transaction.setAgent(cplDTO.getAgent());
                        else transaction.setAgent("");

                        // controlla data scadneza camapgna
                        try {
                            CampaignDTO campaignDTO = campaignBusiness.findByIdAdmin(refferal.getCampaignId());
                            LocalDate endDate = campaignDTO.getEndDate();
                            Boolean scaduta = false;
                            if (endDate.isBefore(cplDTO.getDate().toLocalDate())) {
                                // setto a campagna scaduta
                                transaction.setDictionaryId(49L);
                                scaduta = true;
                            } else {
                                //setto pending
                                transaction.setDictionaryId(42L);
                            }

                            // associo a wallet
                            Long affiliateID = refferal.getAffiliateId();

                            if (affiliateID != null) {
                                Long walletID = walletBusiness.findByIdAffilaite(refferal.getAffiliateId()).stream().findFirst().get().getId();
                                transaction.setWalletId(walletID);
                            }

                            if (scaduta) {
                                log.debug("Campagna {} : {} scaduta", campaignDTO.getId(), campaignDTO.getName());
                                transaction.setRevenueId(1L);
                                transaction.setCommissionId(0L);
                                transaction.setStatusId(74L); // rigettato
                                transaction.setValue(0D);
                                transaction.setDictionaryId(49L);
                                transaction.setLeadNumber(1L);
                            } else {

                                // check Action ID
                                boolean checkAction = false;
                                if (StringUtils.isNotBlank(cplDTO.getActionId())) {
                                    log.info(">>>>>>>>>> TROVATO ACTION : {}", cplDTO.getActionId());
                                    checkAction = true;
                                }

                                // trovo revenue
                                if (checkAction) {
                                    RevenueFactor rf = revenueFactorRepositorye.findFirstByActionAndStatus(cplDTO.getActionId(), true);
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
                                    req.setCommissionDicId(11L);
                                    req.setBlocked(false);
                                    AffiliateChannelCommissionCampaignDTO acccFirst = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);
                                    if (acccFirst != null) {
                                        commVal = acccFirst.getCommissionValue();
                                        commissionId = acccFirst.getCommissionId();
                                    } else
                                        log.warn("Rignera - No Commission CPL (campagna {} e affilitato {}), setto default ({})", refferal.getCampaignId(), refferal.getAffiliateId(), cplDTO.getRefferal());
                                }
                                transaction.setCommissionId(commissionId);

                                Double totale = DoubleRounder.round(commVal * 1, 2);
                                transaction.setValue(totale);
                                transaction.setLeadNumber(1L);

                                // decremento budget Affiliato
                                AffiliateBudgetDTO bb = affiliateBudgetBusiness.getByIdCampaignAndIdAffiliate(refferal.getCampaignId(), refferal.getAffiliateId()).stream().findFirst().orElse(null);
                                if (bb != null && bb.getBudget() != null && ((bb.getBudget() - totale) < 0)) {
                                    transaction.setDictionaryId(47L);
                                }

                                // setto stato transazione a ovebudget editore se totale < 0
                                CampaignBudgetDTO campBudget = campaignBudgetBusiness.searchByCampaignAndDate(camapignId, cplDTO.getDate().toLocalDate()).stream().findFirst().orElse(null);
                                if (campBudget != null && campBudget.getBudgetErogato() != null && (campBudget.getBudgetErogato() - totale < 0)) {
                                    transaction.setDictionaryId(48L);
                                }

                                if (cccpl.getBlacklisted() != null && cccpl.getBlacklisted()) {
                                    transaction.setStatusId(74L);
                                    log.info("QUI=?");
                                } else {
                                    transaction.setStatusId(72L);
                                }

                            }

                            // creo la transazione
                            TransactionCPLDTO cpl = transactionCPLBusiness.createCpl(transaction);
                            log.info(">>>RIGENERATO LEAD :::: {} ", cpl.getId());

                            // verifico il Postback ed eventualemnte faccio chiamata solo se campagna attiva
                            if (postback) {
                                if (transaction.getDictionaryId() != 49L) {
                                    //not manuale
                                    if (transaction.getManualDate() == null) {
                                        List<CampaignAffiliateDTO> campaignAffiliateDTOS = campaignAffiliateBusiness.searchByAffiliateIdAndCampaignId(refferal.getAffiliateId(), refferal.getCampaignId()).toList();
                                        campaignAffiliateDTOS.forEach(campaignAffiliateDTO -> {
                                            // cerco global
                                            String globalPixel = affiliateBusiness.getGlobalPixel(affiliateID);
                                            // cerco folow through
                                            String followT = campaignAffiliateDTO.getFollowThrough();
                                            String info = cplDTO.getInfo();
                                            String data = cplDTO.getData();
                                            String url = "";
                                            log.trace("RCPLB POST BACK ::: " + followT + " :: " + globalPixel + " :: " + info + " :: " + data);
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
                                                log.trace("RCPLB URL :: " + url);
                                                try {
                                                    URL urlGet = new URL(url);
                                                    HttpURLConnection con = (HttpURLConnection) urlGet.openConnection();
                                                    con.setRequestMethod("GET");
                                                    con.getInputStream();
                                                    log.info("RCPLB Chiamo PB  :: " + con.getResponseCode() + " :: " + url + " :: GP:" + globalPixel + " :: INFO :" + info + " :: DATA:" + data);
                                                } catch (Exception e) {
                                                    log.error("RCPLB Eccezione chianata Post Back", e);
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    log.warn("Campagna scaduta non faccio postback :: {}", cpl.getId());
                                }
                            } else {
                                log.info("================================ no postback");
                            }
                        } catch (Exception ecc) {
                            log.error("ECCEZIONE CPL :> ", ecc);
                        }
                    }// creo solo se ho affiliate

                    // setto a gestito
                    cplBusiness.setRead(cplDTO.getId());
                });
            }// ciclo se prendo in considerazione tutto il mese

        } catch (Exception e) {
            log.error("CUSTOM Eccezione Scheduler CPL --  {} - " + anno + "-" + mese + "-" + giorno + " ::: " + affiliateId + " e " + camapignId, e.getMessage(), e);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class FilterUpdate {
        private Integer year;
        private Integer month;
        private Integer day;
        private Long affiliateId;
        private Long campaignId;
        private Boolean postback;
    }

}