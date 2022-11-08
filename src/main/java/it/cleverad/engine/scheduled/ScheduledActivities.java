package it.cleverad.engine.scheduled;

import it.cleverad.engine.business.*;
import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.AffiliateChannelCommissionCampaign;
import it.cleverad.engine.persistence.repository.AffiliateChannelCommissionCampaignRepository;
import it.cleverad.engine.persistence.repository.WalletRepository;
import it.cleverad.engine.service.RefferalService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ScheduledActivities {

    @Autowired
    private CplBusiness cplBusiness;
    @Autowired
    private CpcBusiness cpcBusiness;
    @Autowired
    private CpmBusiness cpmBusiness;
    @Autowired
    private TransactionBusiness transactionBusiness;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private WalletBusiness walletBusiness;

    @Autowired
    private AffiliateChannelCommissionCampaignRepository affiliateChannelCommissionCampaignRepository;

    @Autowired
    private RefferalService refferalService;

    //TODO  controlla quotidianamente se la data scadenza delle campagne è stata superata

    @Scheduled(cron = "0 0/3 * * * ?")
    public void trasformaTrackingCPC() {
        try {
            TransactionBusiness.BaseCreateRequest rr = new TransactionBusiness.BaseCreateRequest();
            Map<String, Integer> mappa = new HashMap<>();

            // trovo tutti i tracking con read == false
            cpcBusiness.getUnreadLastHour().stream().filter(cpcDTO -> cpcDTO.getRefferal() != null).forEach(cpcDTO -> {
                // gestisco calcolatore
                Integer num = mappa.get(cpcDTO.getRefferal());
                if (num == null) num = 0;
                mappa.put(cpcDTO.getRefferal(), num + 1);

                // setto a gestito
                cpcBusiness.setRead(cpcDTO.getId());
            });

            if (!mappa.isEmpty()) {
                mappa.forEach((s, aLong) -> {
                    log.info("Gestisco trasformaTrackingCPC ID {}", aLong);

                    // prendo reffereal e lo leggo
                    Refferal refferal = refferalService.decodificaRefferal(s);
                    log.info("CPC :: {} - {}", s, refferal);

                    // gesione commisione
                    AffiliateChannelCommissionCampaign accc = affiliateChannelCommissionCampaignRepository.findByAffiliateIdAndChannelIdAndCampaignId(refferal.getAffiliateId(), refferal.getChannelId(), refferal.getCampaignId());
                    rr.setCommissionId(accc.getCommission().getId());
                    Double totale = Double.valueOf(accc.getCommission().getValue()) * aLong;
                    rr.setValue(totale);
                    rr.setClickNumber(Long.valueOf(aLong));

                    // setta transazione
                    rr.setAffiliateId(refferal.getAffiliateId());
                    rr.setCampaignId(refferal.getCampaignId());
                    rr.setChannelId(refferal.getChannelId());
                    rr.setDateTime(LocalDateTime.now());
                    rr.setApproved(true);

                    // associo a wallet
                    Long walletID = walletRepository.findByAffiliateId(refferal.getAffiliateId()).getId();
                    rr.setWalletId(walletID);
                    // incemento valore
                    walletBusiness.incement(walletID, totale);

                    // creo la transazione
                    transactionBusiness.createCpc(rr);
                    log.info(">>>>>>> TRASNSAZIONE >>>>> {}", rr);
                });
            }
        } catch (Exception e) {
            log.error("Eccezione Scheduler CPC --  {}", e.getMessage(), e);
        }

    }//trasformaTrackingCPC

    @Scheduled(cron = "0 0/6 * * * ?")
    public void trasformaTrackingCPM() {
        try {
            TransactionBusiness.BaseCreateRequest rr = new TransactionBusiness.BaseCreateRequest();
            Map<String, Integer> mappa = new HashMap<>();

            // trovo tutti i tracking con read == false
            cpmBusiness.getUnreadLastHour().stream().filter(cpmDTO -> cpmDTO.getRefferal() != null).forEach(cpmDTO -> {
                // gestisco calcolatore
                Integer num = mappa.get(cpmDTO.getRefferal());
                if (num == null) num = 0;
                mappa.put(cpmDTO.getRefferal(), num + 1);

                // setto a gestito
                cpcBusiness.setRead(cpmDTO.getId());
            });

            if (!mappa.isEmpty()) {
                mappa.forEach((s, aLong) -> {
                    log.info("Gestisco trasformaTrackingCPM ID {}", aLong);

                    // prendo reffereal e lo leggo
                    Refferal refferal = refferalService.decodificaRefferal(s);
                    log.info("CPM :: {} - {}", s, refferal);

                    // gesione commisione
                    AffiliateChannelCommissionCampaign accc = affiliateChannelCommissionCampaignRepository.findByAffiliateIdAndChannelIdAndCampaignId(refferal.getAffiliateId(), refferal.getChannelId(), refferal.getCampaignId());
                    rr.setCommissionId(accc.getCommission().getId());
                    Double totale = Double.valueOf(accc.getCommission().getValue()) * aLong;
                    rr.setValue(totale);
                    rr.setClickNumber(Long.valueOf(aLong));

                    // setta transazione
                    rr.setAffiliateId(refferal.getAffiliateId());
                    rr.setCampaignId(refferal.getCampaignId());
                    rr.setChannelId(refferal.getChannelId());
                    rr.setDateTime(LocalDateTime.now());
                    rr.setApproved(false);

                    // associo a wallet
                    Long walletID = walletRepository.findByAffiliateId(refferal.getAffiliateId()).getId();
                    rr.setWalletId(walletID);
                    // incemento valore
                    walletBusiness.incement(walletID, totale);

                    // creo la transazione
                    transactionBusiness.createCpm(rr);
                    log.info(">>>>>>> TRASNSAZIONE >>>>> {}", rr);
                });
            }

        } catch (Exception e) {
            log.error("Eccezione Scheduler CPM --  {}", e.getMessage(), e);
        }
    }//trasformaTrackingCPM

    @Scheduled(cron = "0 0/3 * * * ?")
    public void trasformaTrackingCPL() {
        try {
            // trovo uttti i tracking con read == false
            cplBusiness.getUnread().stream().filter(cplDTO -> StringUtils.isNotBlank(cplDTO.getRefferal())).forEach(cplDTO -> {

                // prendo reffereal e lo leggo
                Refferal refferal = refferalService.decodificaRefferal(cplDTO.getRefferal());
                log.info("CPL :: {} - {}", cplDTO, refferal);

                // setta transazione
                TransactionBusiness.BaseCreateRequest rr = new TransactionBusiness.BaseCreateRequest();
                rr.setAffiliateId(refferal.getAffiliateId());
                rr.setCampaignId(refferal.getCampaignId());
                rr.setChannelId(refferal.getChannelId());
                rr.setDateTime(cplDTO.getDate());
                rr.setApproved(true);

                // gesione commisione
                AffiliateChannelCommissionCampaign accc = affiliateChannelCommissionCampaignRepository.findByAffiliateIdAndChannelIdAndCampaignId(refferal.getAffiliateId(), refferal.getChannelId(), refferal.getCampaignId());
                rr.setCommissionId(accc.getCommission().getId());
                Double totale = Double.valueOf(accc.getCommission().getValue());
                rr.setValue(totale);

                // associo a wallet
                Long walletID = walletRepository.findByAffiliateId(refferal.getAffiliateId()).getId();
                rr.setWalletId(walletID);
                // incemento valore
                walletBusiness.incement(walletID, totale);

                // creo la transazione
                transactionBusiness.createCpl(rr);
                log.info(">>>>>>> TRASNSAZIONE >>>>> {}", rr);

                // setto a gestito
                cplBusiness.setRead(cplDTO.getId());
            });
        } catch (Exception e) {
            log.error("Eccezione Scheduler CPL --  {}", e.getMessage(), e);
        }
    }//trasformaTrackingCPL

}
