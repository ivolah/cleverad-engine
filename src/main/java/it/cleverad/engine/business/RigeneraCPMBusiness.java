package it.cleverad.engine.business;

import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.Campaign;
import it.cleverad.engine.persistence.model.service.RevenueFactor;
import it.cleverad.engine.persistence.model.service.TransactionCPC;
import it.cleverad.engine.persistence.model.service.TransactionCPM;
import it.cleverad.engine.persistence.repository.service.CampaignRepository;
import it.cleverad.engine.service.ReferralService;
import it.cleverad.engine.web.dto.AffiliateChannelCommissionCampaignDTO;
import it.cleverad.engine.web.dto.TransactionCPMDTO;
import it.cleverad.engine.web.dto.WalletDTO;
import it.cleverad.engine.web.dto.tracking.CpmDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component
@Transactional
public class RigeneraCPMBusiness {

    @Autowired
    private CpmBusiness CpmBusiness;
    @Autowired
    private TransactionCPMBusiness transactionCpmBusiness;
    @Autowired
    private WalletBusiness walletBusiness;
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private ReferralService referralService;
    @Autowired
    private AffiliateChannelCommissionCampaignBusiness affiliateChannelCommissionCampaignBusiness;
    @Autowired
    private RevenueFactorBusiness revenueFactorBusiness;

    public void rigenera(Integer anno, Integer mese, Integer giorno, Long campaignId, Long affiliateId) {

        if (giorno == null) throw new RuntimeException("IL GIORN ODEV ESSERE VALORIZZATO");

        LocalDate dataDaGestireStart = LocalDate.of(anno, mese, giorno);
        LocalDate dataDaGestireEnd = LocalDate.of(anno, mese, giorno);

        // GESTISCO LE TRANSAZIONI --->>> RIGETTATE E NOT MANUALILISTED
        List<TransactionCPM> transazioni74 = transactionCpmBusiness.searchStatusIdAndDateNotManual(74L, dataDaGestireStart, dataDaGestireEnd, affiliateId, campaignId);
        log.info("RIGETTATE --> 74 >> " + transazioni74.size());
        transazioni74.forEach(ttt -> transactionCpmBusiness.delete(ttt.getId()));

        // GESTISCO LE TRANSAZIONI --->>> APPROVATE E NOT MANUALILISTED
        List<TransactionCPM> transazioni73 = transactionCpmBusiness.searchStatusIdAndDateNotManual(73L, dataDaGestireStart, dataDaGestireEnd, affiliateId, campaignId);
        log.info("APPROVATE --> 73 >> " + transazioni73.size());
        transazioni73.forEach(ttt -> transactionCpmBusiness.delete(ttt.getId()));

        // GESTISCO LE TRANSAZIONI --->>> PENDING E NOT MANUALILISTED
        List<TransactionCPM> transazioni72 = transactionCpmBusiness.searchStatusIdAndDateNotManual(72L, dataDaGestireStart, dataDaGestireEnd, affiliateId, campaignId);
        log.info("PENDING --> 72 >> " + transazioni72.size());
        transazioni72.forEach(ttt -> transactionCpmBusiness.delete(ttt.getId()));


        // ==========================================================================================================================================
        // ==========================================================================================================================================
        // RIPASSO TUTTE LE Cpm
        // cancello transazioni

        this.gestisci(anno, mese, giorno, 73L, false, campaignId);
        // RIPASSO TUTTE LE Cpm BLACKLISTED
        this.gestisci(anno, mese, giorno, 74L, true, campaignId);
        // ==========================================================================================================================================
        // ==========================================================================================================================================
    }

    public void gestisci(Integer anno, Integer mese, Integer giorno, Long statusID, Boolean blacklisted, Long campaignID) {

        LocalDate dataDaGestireStart = LocalDate.of(anno, mese, giorno);
        LocalDate dataDaGestireEnd = LocalDate.of(anno, mese, giorno);
        log.trace("C:: " + campaignID + " >> " + anno + "-" + mese + "-" + giorno + " >> " + dataDaGestireStart + " || " + dataDaGestireEnd);

        //RIGENERO
        List<CpmDTO> day = CpmBusiness.getAllByDay(dataDaGestireStart, dataDaGestireEnd, blacklisted, true, campaignID).getContent();
        log.info(">>> RIGENERO :: (" + anno + "-" + mese + "-" + giorno + ")  :: " + campaignID + "=" + day.size() + " >>> status ::" + statusID);

        //trovo tutti i refferal per poi consolidare
        List<String> refs = day.stream().filter(dto -> dto.getRefferal() != null).map(CpmDTO::getRefferal).distinct().collect(Collectors.toList());
        //ciclo i refferal per poi creare la transazione
        refs.stream().parallel().forEach(refferal -> {

            CpmBusiness.Filter rq = new CpmBusiness.Filter();
            rq.setDateFrom(dataDaGestireStart);
            rq.setDateTo(dataDaGestireStart);
            rq.setBlacklisted(blacklisted);
            rq.setRead(true);
            rq.setRefferal(refferal);
            Page<CpmDTO> cpms = CpmBusiness.search(rq, Pageable.ofSize(Integer.MAX_VALUE));

            Long totImp = cpms.getTotalElements();
            Long mediaId = null;
            Long campaignId = null;
            Long affiliateId = null;
            Long channelID = null;

            Refferal d = referralService.decodificaReferral(refferal);
            if (d != null) {
                if (d.getMediaId() != null)
                    mediaId = d.getMediaId();
                if (d.getCampaignId() != null)
                    campaignId = d.getCampaignId();
                if (d.getAffiliateId() != null)
                    affiliateId = d.getAffiliateId();
                if (d.getChannelId() != null)
                    channelID = d.getChannelId();
            }
            log.info("{} - {} :: {}", campaignId, totImp, refferal);

            if (campaignId != null && affiliateId != null) {
                TransactionCPMBusiness.BaseCreateRequest transaction = new TransactionCPMBusiness.BaseCreateRequest();

                transaction.setStatusId(statusID);


                // gesione commisione
                Double commVal = 0D;

                AffiliateChannelCommissionCampaignBusiness.Filter req = new AffiliateChannelCommissionCampaignBusiness.Filter();
                req.setAffiliateId(d.getAffiliateId());
                req.setChannelId(d.getChannelId());
                req.setCampaignId(d.getCampaignId());
                req.setCommissionDicId(50L);
                AffiliateChannelCommissionCampaignDTO acccFirst = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);

                if (acccFirst != null) {
                    log.trace(acccFirst.getCommissionId() + " " + acccFirst.getCommissionValue());
                    commVal = acccFirst.getCommissionValue();
                    transaction.setCommissionId(acccFirst.getCommissionId());
                } else {
                    transaction.setCommissionId(0L);
                }

                transaction.setValue(totImp *commVal );
                transaction.setImpressionNumber(totImp);

                // gestione revenue factor
                RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(d.getCampaignId(), 50L);
                if (rf != null && rf.getId() != null) {
                    transaction.setRevenueId(rf.getId());
                } else {
                    transaction.setRevenueId(3L);
                }

                transaction.setCampaignId(campaignId);
                transaction.setAffiliateId(affiliateId);
                transaction.setChannelId(channelID);
                transaction.setMediaId(mediaId);
                transaction.setApproved(true);
                transaction.setPayoutPresent(false);
                transaction.setDateTime(dataDaGestireStart.atTime(12, 0, 0));
                transaction.setAgent(null);
                transaction.setData("RIGENERATO");

                Campaign campaign = campaignRepository.findById(campaignId).orElse(null);
                if (campaign.getEndDate().isBefore(dataDaGestireStart)) {
                    log.info("Campaign scaduto: " + campaign.getName() + " " + campaign.getId());
                    // setto a campagna scaduta
                    transaction.setDictionaryId(49L);
                    transaction.setStatusId(74L); // rigettato
                } else {
                    transaction.setDictionaryId(39L);
                }
                if (blacklisted) transaction.setDictionaryId(70L);

                // associo a wallet
                WalletDTO wDTO = walletBusiness.findByIdAffilaite(affiliateId).stream().findFirst().orElse(null);
                if (wDTO != null)
                    transaction.setWalletId(wDTO.getId());

                // creo la transazione
                TransactionCPMDTO tCpm = transactionCpmBusiness.createCpm(transaction);
                log.trace(">>>CPMM :: {} - {}-{} = {}", tCpm.getId(), campaignId, affiliateId, transaction.getClickNumber());

                cpms.stream().forEach(cpmDTO -> CpmBusiness.setRead(cpmDTO.getId()));
            }

        });

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class FilterUpdate {
        private Integer year;
        private Integer month;
        private Integer day;
        private Long campaignId;
        private Long affiliateId;
    }


}