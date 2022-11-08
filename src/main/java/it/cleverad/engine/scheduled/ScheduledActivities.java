package it.cleverad.engine.scheduled;

import it.cleverad.engine.business.CpcBusiness;
import it.cleverad.engine.business.CplBusiness;
import it.cleverad.engine.business.CpmBusiness;
import it.cleverad.engine.business.TransactionBusiness;
import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.AffiliateChannelCommissionCampaign;
import it.cleverad.engine.persistence.repository.AffiliateChannelCommissionCampaignRepository;
import it.cleverad.engine.persistence.repository.WalletRepository;
import it.cleverad.engine.service.RefferalService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
    private AffiliateChannelCommissionCampaignRepository affiliateChannelCommissionCampaignRepository;

    @Autowired
    private RefferalService refferalService;

    //TODO  controlla quotidianamente se la data scadenza delle campagne è stata superata

    @Scheduled(cron = "0 0/1 * * * ?")
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
                    log.info("Gestisco ID {}", aLong);

                    CpcBusiness.Filter ff = new CpcBusiness.Filter();
                    ff.setRefferal(s);
                    cpcBusiness.search(ff, PageRequest.of(0, 10000));

                    // prendo reffereal e lo leggo
                    Refferal refferal = refferalService.decodificaRefferal(s);
                    log.info("CPC :: {} - {}", s, refferal);

                    // gesione commisione
                    AffiliateChannelCommissionCampaign accc = affiliateChannelCommissionCampaignRepository.findByAffiliateIdAndChannelIdAndCampaignId(refferal.getAffiliateId(), refferal.getChannelId(), refferal.getCampaignId());
                    rr.setCommissionId(accc.getCommission().getId());
                    rr.setValue(Double.valueOf(accc.getCommission().getValue()) * aLong);

                    // setta transazione
                    rr.setAffiliateId(refferal.getAffiliateId());
                    rr.setCampaignId(refferal.getCampaignId());
                    rr.setChannelId(refferal.getChannelId());
                    rr.setDateTime(LocalDateTime.now());
                    rr.setApproved(true);

                    // associo a wallet
                    rr.setWalletId(walletRepository.findByAffiliateId(refferal.getAffiliateId()).getId());

                    // creo la transazione
                    transactionBusiness.createCpc(rr);
                    log.info(">>>>>>> TRASNSAZIONE >>>>> {}", rr);
                });
            }
        } catch (Exception e) {
            log.error("Eccezione Scheduler CPC --  {}", e.getMessage(), e);
        }

    }//trasformaTrackingCPC

    @Scheduled(cron = "0 0/2 * * * ?")
    public void trasformaTrackingCPM() {
        try {
            // trovo uttti i tracking con read == false
            cpmBusiness.getUnread().stream().filter(cpmDTO -> cpmDTO.getRefferal() != null).forEach(cpmDTO -> {
                TransactionBusiness.BaseCreateRequest rr = new TransactionBusiness.BaseCreateRequest();

                // prendo reffereal e lo leggo
                Refferal refferal = refferalService.decodificaRefferal(cpmDTO.getRefferal());
                log.info("CPM :: {} - {}", cpmDTO.getRefferal(), refferal);

                // gesione commisione
                AffiliateChannelCommissionCampaign accc = affiliateChannelCommissionCampaignRepository.findByAffiliateIdAndChannelIdAndCampaignId(refferal.getAffiliateId(), refferal.getChannelId(), refferal.getCampaignId());
                rr.setCommissionId(accc.getCommission().getId());
                rr.setValue(Double.valueOf(accc.getCommission().getValue()));

                // setta transazione
                rr.setAffiliateId(refferal.getAffiliateId());
                rr.setCampaignId(refferal.getCampaignId());
                rr.setChannelId(refferal.getChannelId());
                rr.setDateTime(cpmDTO.getDate());
                rr.setApproved(false);

                // associo a wallet
                rr.setWalletId(walletRepository.findByAffiliateId(refferal.getAffiliateId()).getId());

                // creo la transazione
                transactionBusiness.createCpm(rr);
                log.info(">>>>>>> TRASNSAZIONE >>>>> {}", rr);

                // setto a gestito
                cpmBusiness.setRead(cpmDTO.getId());
            });
        } catch (Exception e) {
            log.error("Eccezione Scheduler CPM --  {}", e.getMessage(), e);
        }
    }//trasformaTrackingCPM

    @Scheduled(cron = "0 0/3 * * * ?")
    public void trasformaTrackingCPL() {
        try {
            // trovo uttti i tracking con read == false
            cplBusiness.getUnread().stream().filter(cplDTO -> StringUtils.isNotBlank(cplDTO.getCid())).forEach(cplDTO -> {

                // prendo reffereal e lo leggo
                Refferal reff = refferalService.decodificaRefferal(cplDTO.getCid());
                log.info("CPL :: {} - {}", reff.getMediaId(), reff.getCampaignId());

                // setta transazione
                TransactionBusiness.BaseCreateRequest rr = new TransactionBusiness.BaseCreateRequest();
                rr.setCampaignId(Long.valueOf(reff.getCampaignId()));
                rr.setDateTime(cplDTO.getDate());
                rr.setApproved(true);

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
