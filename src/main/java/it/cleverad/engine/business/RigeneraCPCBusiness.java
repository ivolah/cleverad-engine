package it.cleverad.engine.business;

import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.Campaign;
import it.cleverad.engine.persistence.model.service.ClickMultipli;
import it.cleverad.engine.persistence.model.service.RevenueFactor;
import it.cleverad.engine.persistence.model.service.TransactionCPC;
import it.cleverad.engine.persistence.model.tracking.Cpc;
import it.cleverad.engine.persistence.repository.service.CampaignRepository;
import it.cleverad.engine.persistence.repository.tracking.CpcRepository;
import it.cleverad.engine.service.ReferralService;
import it.cleverad.engine.web.dto.AffiliateBudgetDTO;
import it.cleverad.engine.web.dto.AffiliateChannelCommissionCampaignDTO;
import it.cleverad.engine.web.dto.CampaignBudgetDTO;
import it.cleverad.engine.web.dto.TransactionCPCDTO;
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
import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class RigeneraCPCBusiness {

    @Autowired
    private CpcBusiness cpcBusiness;
    @Autowired
    private CpcRepository repository;
    @Autowired
    private TransactionCPCBusiness transactionCPCBusiness;
    @Autowired
    private WalletBusiness walletBusiness;
    @Autowired
    private AffiliateBudgetBusiness affiliateBudgetBusiness;
    @Autowired
    private CampaignBudgetBusiness campaignBudgetBusiness;
    @Autowired
    private RevenueFactorBusiness revenueFactorBusiness;
    @Autowired
    private AffiliateChannelCommissionCampaignBusiness affiliateChannelCommissionCampaignBusiness;
    @Autowired
    private ReferralService referralService;
    @Autowired
    private CampaignRepository campaignRepository;

    private long TIME_THRESHOLD = 60000;
    private Map<String, Instant> ipTimestampMap = new HashMap<>();

    private boolean isSuspiciousRequest(String ipAddress) {
        Instant currentTime = Instant.now();
        return ipTimestampMap.compute(ipAddress, (key, lastTimestamp) -> {
            if (lastTimestamp != null && currentTime.toEpochMilli() - lastTimestamp.toEpochMilli() < TIME_THRESHOLD) {
                return lastTimestamp; // Suspicious request
            }
            return currentTime;
        }) != currentTime;
    }
    public void rigenera(Integer anno, Integer mese, Integer giorno, Long affiliateId, Long campaignId) {

        int start = (giorno == null) ? 1 : giorno;
        int end = (giorno == null) ? LocalDate.of(anno, mese, 1).lengthOfMonth() : giorno;

        LocalDate dataDaGestireStart = LocalDate.of(anno, mese, start);
        LocalDate dataDaGestireEnd = LocalDate.of(anno, mese, end);

        log.info(anno + "-" + mese + "-" + giorno + " >> " + dataDaGestireStart + " || " + dataDaGestireEnd + " per " + affiliateId);

        // ==========================================================================================================================================
        // ==========================================================================================================================================
        // SETTAGGIO CLICK MULTIPLI
        // ==========================================================================================================================================
        // ==========================================================================================================================================

        // NEL CASO NON SIA GIA' VERIFICO I CLICK MULTIPLI
        List<ClickMultipli> listaDaDisabilitare = new ArrayList<>();
        Integer numeroGiorniBetween = dataDaGestireEnd.getDayOfYear() - dataDaGestireStart.getDayOfYear();
        for (int i = 0; i < numeroGiorniBetween; i++) {
            List<ClickMultipli> ll = cpcBusiness.getListaClickMultipliDaDisabilitare(dataDaGestireStart.plusDays(i), dataDaGestireStart.plusDays(i + 1), affiliateId, campaignId);
            listaDaDisabilitare.addAll(ll);
            log.info("Data :: {} :: {}  disabilitati", dataDaGestireStart.plusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE), ll.size());
        }
        //
        //        List<ClickMultipli> listaDaDisabilitare = cpcBusiness.getListaClickMultipliDaDisabilitare(dataDaGestireStart, dataDaGestireEnd);
        //        log.info("DA DISABILIATRE :: {}", listaDaDisabilitare.size());

        // giro settaggio click multipli
        listaDaDisabilitare.forEach(clickMultipli -> {
            log.trace("Disabilito {} :: {}", clickMultipli.getId(), clickMultipli.getTotale());
            Cpc cccp = repository.findById(clickMultipli.getId()).orElseThrow(() -> new ElementCleveradException("Cpc", clickMultipli.getId()));
            cccp.setRead(true);
            cccp.setBlacklisted(true);
            cccp.setMultiple(true);
            repository.save(cccp);
        });

        // cerco IP vicini tra quelli non già blacklisted
        //       Page<CpcDTO> listaIpDaVerificare = cpcBusiness.getListaNotBlacklisted(dataDaGestireStart, dataDaGestireEnd);
//        List<String> ips = listaIpDaVerificare.stream().map(cpcDTO -> cpcDTO.getIp()).distinct().collect(Collectors.toList();
//        ips.stream().filter(this::isSuspiciousRequest).forEach(ipAddress -> {
//            // Additional logic for handling suspicious requests
//            log.info("Suspicious request from near IP address: " + ipAddress);
//            // You can add more actions here, such as blocking the IP or logging the request.
//        });
//
//        listaIpDaVerificare.stream().forEach(cpcDTO -> {
//            String ip = cpcDTO.getIp();
//
//
//        });


        // ==========================================================================================================================================
        // ==========================================================================================================================================
        // CANCELLO LE TRANSAZIONI NON BLACKLISTED

        // GESTISCO LE TRANSAZIONI --->>> RIGETTATE E NOT MANUALILISTED
        List<TransactionCPC> transazioni74 = transactionCPCBusiness.searchStatusIdAndDateNotManual(74L, dataDaGestireStart, dataDaGestireEnd, affiliateId, campaignId);
        log.info("RIGETTATE --> 74 >> " + transazioni74.size());
        transazioni74.forEach(ttt -> transactionCPCBusiness.delete(ttt.getId()));

        // GESTISCO LE TRANSAZIONI --->>> APPROVATE E NOT MANUALILISTED
        List<TransactionCPC> transazioni73 = transactionCPCBusiness.searchStatusIdAndDateNotManual(73L, dataDaGestireStart, dataDaGestireEnd, affiliateId, campaignId);
        log.info("APPROVATE --> 73 >> " + transazioni73.size());
        transazioni73.forEach(ttt -> transactionCPCBusiness.delete(ttt.getId()));

        // GESTISCO LE TRANSAZIONI --->>> PENDING E NOT MANUALILISTED
        List<TransactionCPC> transazioni72 = transactionCPCBusiness.searchStatusIdAndDateNotManual(72L, dataDaGestireStart, dataDaGestireEnd, affiliateId, campaignId);
        log.info("PENDING --> 72 >> " + transazioni72.size());
        transazioni72.forEach(ttt -> transactionCPCBusiness.delete(ttt.getId()));

        // RIPASSO TUTTE LE CPC PENDING
        this.gestisci(anno, mese, giorno, affiliateId, campaignId, 72L, false);

        // ==========================================================================================================================================
        // ==========================================================================================================================================
        // GESTISCO BLACKLISTED A PARTE

        // GESTISCO LE TRANSAZIONI --->>> BLACKLSTED
        List<TransactionCPC> black = transactionCPCBusiness.searchStatusIdAndDicIdAndDate(74L, 70L, dataDaGestireStart, dataDaGestireEnd, affiliateId, campaignId);
        log.info("BLACKLISTED >> " + black.size());
        black.forEach(transactionStatusDTO -> transactionCPCBusiness.delete(transactionStatusDTO.getId()));
        // RIPASSO TUTTE LE CPC BLACKLISTED
        this.gestisci(anno, mese, giorno, affiliateId, campaignId, 74L, true);

    }

    public void gestisci(Integer anno, Integer mese, Integer giorno, Long affId, Long campId, Long statusID, Boolean blacklisted) {
        try {

            int start = (giorno == null) ? 1 : giorno;
            int end = (giorno == null) ? LocalDate.of(anno, mese, 1).lengthOfMonth() : giorno;

            LocalDate dataDaGestireStart = LocalDate.of(anno, mese, start);
            LocalDate dataDaGestireEnd = LocalDate.of(anno, mese, end);

            //RIGENERO
            Page<CpcDTO> day = cpcBusiness.getAllByDay(dataDaGestireStart, dataDaGestireEnd, blacklisted, affId, campId);
            log.trace(">>> RIGENERO :: " + day.getTotalElements() + " >>> con status ::" + statusID);

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
                Cpc cccp = repository.findById(cpcDTO.getId()).get();
                if (cpcDTO.getRefferal().equals("{{refferalId}}")) {
                    cccp.setRefferal("");
                } else {
                    Refferal refferal = referralService.decodificaReferral(cpcDTO.getRefferal());
                    if (refferal != null) {
                        if (refferal.getMediaId() != null)
                            cccp.setMediaId(refferal.getMediaId());
                        if (refferal.getCampaignId() != null)
                            cccp.setCampaignId(refferal.getCampaignId());
                        if (refferal.getAffiliateId() != null)
                            cccp.setAffiliateId(refferal.getAffiliateId());
                        if (refferal.getChannelId() != null)
                            cccp.setChannelId(refferal.getChannelId());
                        if (refferal.getTargetId() != null)
                            cccp.setTargetId(refferal.getTargetId());
                    }
                    repository.save(cccp);
                }

                Triple<Long, Long, Long> triple = new ImmutableTriple<>(cpcDTO.getCampaignId(), cpcDTO.getAffiliateId(), cpcDTO.getChannelId());
                triples.add(triple);
            });

            triples.stream().distinct().collect(Collectors.toList()).forEach(triple -> {

                Long campaignId = (Long) triple.getLeft();
                Long affiliateId = (Long) triple.getMiddle();
                Long channelID = (Long) triple.getRight();
                LocalDate data = LocalDate.of(anno, mese, 1);
                if (campaignId != null) {

                    if (campaignRepository.findById(campaignId).orElse(null) != null)

                        for (int gg = start; gg <= end; gg++) {
                            data = LocalDate.of(anno, mese, gg);

                            CpcBusiness.Filter rq = new CpcBusiness.Filter();
                            rq.setDateFrom(data);
                            rq.setDateTo(data);
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
                            log.info("TRI {}-{}-{} :: {}", campaignId, affiliateId, channelID, totaleClick);

                            if (totaleClick > 0) {
                                TransactionCPCBusiness.BaseCreateRequest transaction = new TransactionCPCBusiness.BaseCreateRequest();

                                Campaign campaign = campaignRepository.findById(campaignId).orElse(null);
                                if (campaign != null) {
                                    if (campaign.getEndDate().isBefore(data)) {
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
                                        log.trace(accc.getCommissionId() + " " + accc.getCommissionValue());
                                        commVal = accc.getCommissionValue();
                                        transaction.setCommissionId(accc.getCommissionId());
                                    } else {
                                        transaction.setCommissionId(0L);
                                        log.trace("Transazione a commissione 0 : {} ", req.toString());
                                    }

                                    // calcolo valore
                                    double totale = DoubleRounder.round(commVal * totaleClick, 2);
                                    transaction.setValue(totale);
                                    transaction.setClickNumber(totaleClick);

                                    // incemento valore schedualto

                                    // decremento budget Affiliato
                                    AffiliateBudgetDTO bb = affiliateBudgetBusiness.getByIdCampaignAndIdAffiliate(campaignId, affiliateId).stream().findFirst().orElse(null);
                                    if (bb != null && bb.getBudget() != null) {
                                        // setto stato transazione a ovebudget editore se totale < 0
                                        if ((bb.getBudget() - totale) < 0) transaction.setDictionaryId(47L);
                                    }

                                    // setto stato transazione a ovebudget editore se totale < 0
                                    CampaignBudgetDTO campBudget = campaignBudgetBusiness.searchByCampaignAndDate(campaignId, data).stream().findFirst().orElse(null);
                                    if (campBudget != null && campBudget.getBudgetErogato() != null)
                                        if (campBudget.getBudgetErogato() - totale < 0)
                                            transaction.setDictionaryId(48L);

                                    // commissione scaduta
                                    if (accc != null && accc.getCommissionDueDate() != null && accc.getCommissionDueDate().isBefore(data))
                                        transaction.setDictionaryId(49L);

                                    if (blacklisted) transaction.setDictionaryId(70L);

                                }

                                transaction.setClickNumber(totaleClick);
                                transaction.setCampaignId(campaignId);
                                transaction.setAffiliateId(affiliateId);
                                transaction.setChannelId(channelID);
                                transaction.setMediaId(mediaId);
                                transaction.setApproved(true);
                                transaction.setPayoutPresent(false);
                                transaction.setDateTime(data.atTime(3, 0, 0));
                                transaction.setAgent("");
                                transaction.setStatusId(statusID);

                                // creo la transazione
                                TransactionCPCDTO tcpc = transactionCPCBusiness.createCpc(transaction);
                                log.trace(">>>RI-CLICK :: {} - {}-{} = {}", tcpc.getId(), campaignId, affiliateId, transaction.getClickNumber());

                            }// if totale click > 0
                            else {
                                log.trace("Totale click == 0 - verifica problema {}-{} : {} : {} : {}", dataDaGestireStart.getMonthValue(), dataDaGestireStart.getDayOfMonth(), campaignId, affiliateId, channelID);
                            }
                        }
                } else {
                    log.warn("Campaign id null {}-{} : {} : {} : {}", data.getMonthValue(), data.getDayOfMonth(), campaignId, affiliateId, channelID);
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
        private Integer year;
        private Integer month;
        private Integer day;
        private Long affiliateId;
        private Long campaignId;
    }


}