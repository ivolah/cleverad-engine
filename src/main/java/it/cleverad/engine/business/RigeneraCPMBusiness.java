package it.cleverad.engine.business;

import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.Campaign;
import it.cleverad.engine.persistence.repository.service.CampaignRepository;
import it.cleverad.engine.service.ReferralService;
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

    public void rigenera(Integer anno, Integer mese, Integer giorno) {
        // ==========================================================================================================================================
        // ==========================================================================================================================================
        // RIPASSO TUTTE LE Cpm PENDING
        this.gestisci(anno, mese, giorno, 72L, false);
        // RIPASSO TUTTE LE Cpm BLACKLISTED
        this.gestisci(anno, mese, giorno, 74L, true);
        // ==========================================================================================================================================
        // ==========================================================================================================================================
    }

    public void gestisci(Integer anno, Integer mese, Integer giorno, Long statusID, Boolean blacklisted) {

        if (giorno == null) throw new RuntimeException("IL GIORN ODEV ESSERE VALORIZZATO");

        LocalDate dataDaGestireStart = LocalDate.of(anno, mese, giorno);
        LocalDate dataDaGestireEnd = LocalDate.of(anno, mese, giorno);

        log.info(anno + "-" + mese + "-" + giorno + " >> " + dataDaGestireStart + " || " + dataDaGestireEnd);

        //RIGENERO
        List<CpmDTO> day = CpmBusiness.getAllByDay(dataDaGestireStart, dataDaGestireEnd, blacklisted).getContent();
        log.info(">>> RIGENERO :: " + day.size() + " >>> con status ::" + statusID);

        List<String> refs = day.stream().filter(dto -> dto.getRefferal() != null).map(CpmDTO::getRefferal).distinct().collect(Collectors.toList());
        log.info("Refs {}", refs.size());
        refs.stream().parallel().forEach(refferal -> {

            CpmBusiness.Filter rq = new CpmBusiness.Filter();
            rq.setDateFrom(dataDaGestireStart);
            rq.setDateTo(dataDaGestireStart);
            rq.setBlacklisted(blacklisted);
            rq.setRead(false);
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
            log.info("REF :: {} {} :: {}", refferal, campaignId, totImp);

            if (campaignId != null && affiliateId != null) {
                TransactionCPMBusiness.BaseCreateRequest transaction = new TransactionCPMBusiness.BaseCreateRequest();

                Campaign campaign = campaignRepository.findById(campaignId).orElse(null);
                if (campaign.getEndDate().isBefore(dataDaGestireStart)) {
                    // setto a campagna scaduta
                    transaction.setDictionaryId(49L);
                } else {
                    transaction.setDictionaryId(39L);
                }
                if (blacklisted) transaction.setDictionaryId(70L);

                // associo a wallet
                WalletDTO wDTO = walletBusiness.findByIdAffilaite(affiliateId).stream().findFirst().orElse(null);
                if (wDTO != null)
                    transaction.setWalletId(wDTO.getId());

                transaction.setRevenueId(1L);
                transaction.setCommissionId(0L);
                transaction.setValue(0D);
                transaction.setImpressionNumber(totImp);
                transaction.setCampaignId(campaignId);
                transaction.setAffiliateId(affiliateId);
                transaction.setChannelId(channelID);
                transaction.setMediaId(mediaId);
                transaction.setApproved(true);
                transaction.setPayoutPresent(false);
                transaction.setDateTime(dataDaGestireStart.atTime(12, 0, 0));
                transaction.setAgent("");
                transaction.setStatusId(statusID);

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
    }

}