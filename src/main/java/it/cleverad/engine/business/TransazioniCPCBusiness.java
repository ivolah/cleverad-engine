package it.cleverad.engine.business;

import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.Campaign;
import it.cleverad.engine.persistence.model.service.ClickMultipli;
import it.cleverad.engine.persistence.model.service.RevenueFactor;
import it.cleverad.engine.persistence.model.tracking.Cpc;
import it.cleverad.engine.persistence.repository.service.CampaignRepository;
import it.cleverad.engine.persistence.repository.tracking.CpcRepository;
import it.cleverad.engine.service.ReferralService;
import it.cleverad.engine.web.dto.*;
import it.cleverad.engine.web.dto.tracking.CpcDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class TransazioniCPCBusiness {

    @Autowired
    private CpcBusiness cpcBusiness;
    @Autowired
    private CpcRepository repository;

    @Autowired
    private TransactionBusiness transactionBusiness;
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
    private CampaignRepository campaignRepository;
    @Autowired
    private TransactionAllBusiness transactionAllBusiness;

    public void rigenera(Integer anno, Integer mese, Integer giorno) {
        LocalDate dataDaGestire = LocalDate.of(anno, mese, giorno);
        log.info(anno + "-" + mese + "-" + giorno + " >> " + dataDaGestire);

        // ==========================================================================================================================================
        // ==========================================================================================================================================
        // SETTAGGIO CLICK MULTIPLI
        // ==========================================================================================================================================
        // ==========================================================================================================================================

        // NEL CASO NON SIA GIA' VERIFICO I CLICK MULTIPLI
        List<ClickMultipli> listaDaDisabilitare = cpcBusiness.getListaClickMultipliDaDisabilitare(dataDaGestire);
        log.info("DA DISABILIATRE :: {}", listaDaDisabilitare.size());

        // giro settaggio click multipli
        listaDaDisabilitare.stream().forEach(clickMultipli -> {
            //  log.info("Disabilito {} :: {}", clickMultipli.getId(), clickMultipli.getTotale());
            Cpc cccp = repository.findById(clickMultipli.getId()).orElseThrow(() -> new ElementCleveradException("Cpc", clickMultipli.getId()));
            cccp.setRead(true);
            cccp.setBlacklisted(true);
            repository.save(cccp);
        });

        // ==========================================================================================================================================
        // ==========================================================================================================================================
        // CANCELLO LE TRANSAZIONI NON BLACKLISTED

        // GESTISCO LE TRANSAZIONI --->>> RIGETTATE E NOT MANUALILISTED
        Page<TransactionStatusDTO> L74 = transactionAllBusiness.searchStatusIdAndDate(74L, dataDaGestire, "CPC");
        log.info("RIGETTATE --> 74 >> " + L74.getTotalElements());
        L74.forEach(ttt -> transactionBusiness.delete(ttt.getId(), "CPC"));

        // GESTISCO LE TRANSAZIONI --->>> APPROVATE E NOT MANUALILISTED
        Page<TransactionStatusDTO> L73 = transactionAllBusiness.searchStatusIdAndDate(73L, dataDaGestire, "CPC");
        log.info("APPROVATE --> 73 >> " + L73.getTotalElements());
        L73.forEach(ttt -> transactionBusiness.delete(ttt.getId(), "CPC"));

        // GESTISCO LE TRANSAZIONI --->>> PENDING E NOT MANUALILISTED
        Page<TransactionStatusDTO> L72 = transactionAllBusiness.searchStatusIdAndDate(72L, dataDaGestire, "CPC");
        log.info("PENDING --> 72 >> " + L72.getTotalElements());
        L72.forEach(ttt -> transactionBusiness.delete(ttt.getId(), "CPC"));

        // RIPASSO TUTTE LE CPC PENDING
        this.gestisci(72L, dataDaGestire, false);

        // ==========================================================================================================================================
        // ==========================================================================================================================================
        // GESTISCO BLACKLISTED A PARTE

        // GESTISCO LE TRANSAZIONI --->>> BLACKLSTED
        Page<TransactionStatusDTO> black = transactionAllBusiness.searchStatusIdAndDicIdAndDate(74L, 70L, dataDaGestire, "CPC");
        log.info("BLACKLISTED >> " + black.getTotalElements());
        black.forEach(transactionStatusDTO -> transactionBusiness.delete(transactionStatusDTO.getId(), "CPC"));

        // RIPASSO TUTTE LE CPC BLACKLISTED
        this.gestisci(74L, dataDaGestire, true);

    }

    public void gestisci(Long statusID, LocalDate dataDaGestire, Boolean blacklisted) {
        try {
            //RIGENERO
            Map<String, Integer> mappa = new HashMap<>();
            Page<CpcDTO> day = cpcBusiness.getAllByDay(dataDaGestire, blacklisted);
            log.info(">>> RIGENERO :: " + day.getTotalElements() + " >>> con status ::" + statusID);

            // trovo tutti i refferal
            List<Triple> triples = new ArrayList<>();
            day.stream().filter(dto -> dto.getRefferal() != null).forEach(cpcDTO -> {

                // gestisco i refferal troppo corti
                if (cpcDTO.getRefferal().length() < 5) {
                    // cerco da cpc
                    List<CpcDTO> ips = cpcBusiness.findByIp24HoursBefore(cpcDTO.getIp(), cpcDTO.getDate(), cpcDTO.getRefferal()).stream().collect(Collectors.toList());
                    // prendo ultimo ipp
                    for (CpcDTO ccc : ips)
                        if (StringUtils.isNotBlank(ccc.getRefferal())) cpcDTO.setRefferal(ccc.getRefferal());
                }

                //gestisco i casi dove i dati non sono tutti valorizzati
                if (StringUtils.isNotBlank(cpcDTO.getRefferal()) && cpcDTO.getCampaignId() == null) {
                    Cpc cccp = repository.findById(cpcDTO.getId()).get();
                    Refferal refferal = referralService.decodificaReferral(cpcDTO.getRefferal());
                    if (refferal != null) {
                        if (refferal.getMediaId() != null) cccp.setMediaId(refferal.getMediaId());
                        if (refferal.getCampaignId() != null) cccp.setCampaignId(refferal.getCampaignId());
                        if (refferal.getAffiliateId() != null) cccp.setAffiliateId(refferal.getAffiliateId());
                        if (refferal.getChannelId() != null) cccp.setChannelId(refferal.getChannelId());
                        if (refferal.getTargetId() != null) cccp.setTargetId(refferal.getTargetId());
                    }
                    repository.save(cccp);
                }

                Triple<Long, Long, Long> triple = new ImmutableTriple<>(cpcDTO.getCampaignId(), cpcDTO.getAffiliateId(), cpcDTO.getChannelId());
                triples.add(triple);
            });

            triples.stream().distinct().collect(Collectors.toList()).stream().forEach(triple -> {

                Long campaignId = (Long) triple.getLeft();
                Long affiliateId = (Long) triple.getMiddle();
                Long channelID = (Long) triple.getRight();

                if (campaignId != null) {

                    CpcBusiness.Filter rq = new CpcBusiness.Filter();
                    rq.setDateFrom(dataDaGestire);
                    rq.setDateTo(dataDaGestire);
                    rq.setBlacklisted(blacklisted);
                    rq.setCampaignid(campaignId);
                    rq.setAffiliateid(affiliateId);
                    rq.setChannelId(channelID);
                    Page<CpcDTO> cpcs = cpcBusiness.search(rq, Pageable.ofSize(Integer.MAX_VALUE));

                    Long totaleClick = 0L;
                    Long mediaId = null;
                    for (CpcDTO tcpc : cpcs) {
                        totaleClick += 1;
                        mediaId = tcpc.getMediaId();
                    }
                    // log.info("TRI {}-{}-{} :: {}", campaignId, affiliateId, channelID, totaleClick);

                    if (totaleClick > 0) {
                        TransactionBusiness.BaseCreateRequest transaction = new TransactionBusiness.BaseCreateRequest();

                        transaction.setClickNumber(totaleClick);

                        transaction.setCampaignId(campaignId);
                        transaction.setAffiliateId(affiliateId);
                        transaction.setChannelId(channelID);
                        transaction.setMediaId(mediaId);

                        transaction.setApproved(true);
                        transaction.setPayoutPresent(false);

                        transaction.setDateTime(dataDaGestire.atTime(3, 0, 0));

                        transaction.setAgent("");

                        transaction.setStatusId(statusID);


                        Campaign campaign = campaignRepository.findById(campaignId).orElse(null);
                        if (campaign != null) {
                            if (campaign.getEndDate().isBefore(dataDaGestire)) {
                                // setto a campagna scaduta
                                transaction.setDictionaryId(49L);
                            } else {
                                transaction.setDictionaryId(42L);
                            }

                            // associo a wallet
                            Long walletID = null;
                            if (affiliateId != null) {
                                walletID = walletBusiness.findByIdAffilaite(affiliateId).stream().findFirst().get().getId();
                                transaction.setWalletId(walletID);
                            }

                            // trovo revenue
                            RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(campaignId, 10L);
                            if (rf != null && rf.getId() != null) {
                                transaction.setRevenueId(rf.getId());
                            } else {
                                transaction.setRevenueId(1L);
                            }

                            // gesione commisione
                            Double commVal = 0D;

                            AffiliateChannelCommissionCampaignBusiness.Filter req = new AffiliateChannelCommissionCampaignBusiness.Filter();
                            req.setAffiliateId(affiliateId);
                            req.setChannelId(channelID);
                            req.setCampaignId(campaignId);
                            req.setCommissionDicId(10L);

                            AffiliateChannelCommissionCampaignDTO accc = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);
                            if (accc != null) {
                                // log.info(accc.getCommissionId() + " " + accc.getCommissionValue());
                                commVal = accc.getCommissionValue();
                                transaction.setCommissionId(accc.getCommissionId());
                            } else {
                                transaction.setCommissionId(0L);
                            }

                            // calcolo valore
                            Double totale = commVal * totaleClick;
                            transaction.setValue(totale);
                            transaction.setClickNumber(Long.valueOf(totaleClick));

                            // incemento valore
                            if (walletID != null && totale > 0D) walletBusiness.incement(walletID, totale);

                            // decremento budget Affiliato
                            BudgetDTO bb = budgetBusiness.getByIdCampaignAndIdAffiliate(campaignId, affiliateId).stream().findFirst().orElse(null);
                            if (bb != null && bb.getBudget() != null) {
                                Double totBudgetDecrementato = bb.getBudget() - totale;
                                budgetBusiness.updateBudget(bb.getId(), totBudgetDecrementato);

                                // setto stato transazione a ovebudget editore se totale < 0
                                if (totBudgetDecrementato < 0) {
                                    transaction.setDictionaryId(47L);
                                }
                            }

                            // decremento budget Campagna
                            Double budgetCampagna = campaign.getBudget() - totale;
                            campaignBusiness.updateBudget(campaign.getId(), budgetCampagna);

                            // setto stato transazione a ovebudget editore se totale < 0
                            if (budgetCampagna < 0) {
                                transaction.setDictionaryId(48L);
                            }

                            // commissione scaduta
                            if (accc != null && accc.getCommissionDueDate() != null && accc.getCommissionDueDate().isBefore(dataDaGestire)) {
                                transaction.setDictionaryId(49L);
                            }

                            if (blacklisted) {
                                transaction.setDictionaryId(70L);
                            }

                        }

                        // creo la transazione
                        TransactionCPCDTO tcpc = transactionBusiness.createCpc(transaction);
                        log.info(">>>RI-CLICK :: {} - {}-{} = {}", tcpc.getId(), campaignId, affiliateId, transaction.getClickNumber());

                    }// if totale click > 0
                    else {
//                    log.warn("Totale click == 0 - verifica problema {}-{} : {} : {} : {}", dataDaGestire.getMonthValue(), dataDaGestire.getDayOfMonth(), campaignId, affiliateId, channelID);
                    }

                } else {
                    log.warn("Campaign id null {}-{} : {} : {} : {}", dataDaGestire.getMonthValue(), dataDaGestire.getDayOfMonth(), campaignId, affiliateId, channelID);
                }
            });


        } catch (Exception e) {
            log.error("CUSTOM Eccezione Scheduler CPC --  {}", e.getMessage(), e);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class FilterUpdate {
        private String year;
        private String month;
        private String day;
    }

}