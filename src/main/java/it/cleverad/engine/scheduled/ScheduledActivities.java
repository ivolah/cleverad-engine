package it.cleverad.engine.scheduled;

import it.cleverad.engine.business.*;
import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.AffiliateChannelCommissionCampaign;
import it.cleverad.engine.persistence.repository.AffiliateChannelCommissionCampaignRepository;
import it.cleverad.engine.persistence.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Base64;

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
    private AffiliateChannelCommissionCampaignBusiness acccBusiness;
    @Autowired
    private AffiliateChannelCommissionCampaignRepository affiliateChannelCommissionCampaignRepository;

    private Refferal decodificaRefferal(String refferalString) {
        byte[] decoder = Base64.getDecoder().decode(refferalString);
        String str = new String(decoder);
        String[] tokens = str.split("\\|\\|");
        log.trace("NOM TOKEN  REFF  {}", tokens.length);
        Refferal refferal = new Refferal();
        if (tokens[0] != null) {
            refferal.setCampaignId(Long.valueOf(tokens[0]));
        }
        if (tokens[1] != null) {
            refferal.setMediaId(Long.valueOf(tokens[1]));
        }
        if (tokens[2] != null) {
            refferal.setAffiliateId(Long.valueOf(tokens[2]));
        }
        if (tokens[3] != null) {
            refferal.setChannelId(Long.valueOf(tokens[3]));
        }
        return refferal;
    }

    //TODO  controlla quotidianamente se la data scadenza delle campagne è stata superata

    @Scheduled(fixedRateString = "5000")
    public void trasformaTrackingCPC() {
        try {
            // trovo uttti i tracking con read == false
            cpcBusiness.getUnread().stream().filter(cpcDTO -> cpcDTO.getRefferal() != null).forEach(cpcDTO -> {
                TransactionBusiness.BaseCreateRequest rr = new TransactionBusiness.BaseCreateRequest();

                // prendo reffereal e lo leggo
                Refferal refferal = this.decodificaRefferal(cpcDTO.getRefferal());
                log.info("CPC :: {} - {}", cpcDTO.getRefferal(), refferal);

                // gesione commisione
                AffiliateChannelCommissionCampaign accc = affiliateChannelCommissionCampaignRepository.findByAffiliateIdAndChannelIdAndCampaignId(refferal.getAffiliateId(), refferal.getChannelId(), refferal.getCampaignId());
                rr.setCommissionId(accc.getCommission().getId());
                rr.setValue(Double.valueOf(accc.getCommission().getValue()));

                // setta transazione
                rr.setAffiliateId(refferal.getAffiliateId());
                rr.setCampaignId(refferal.getCampaignId());
                rr.setChannelId(refferal.getChannelId());
                rr.setType("CPC");
                rr.setDateTime(cpcDTO.getDate());
                rr.setApproved(false);

                // associo a wallet
                rr.setWalletId(walletRepository.findByAffiliateId(refferal.getAffiliateId()).getId());

                    // creo la transazione
                transactionBusiness.create(rr);
                log.info(">>>>>>> TRASNSAZIONE >>>>> {}", rr);

                // setto a gestito
                cpcBusiness.setRead(cpcDTO.getId());

            });
        } catch (Exception e) {
            log.error("Eccezione Scheduler CPC --  {}", e.getMessage(), e);
        }

    }//trasformaTrackingCPC

    @Scheduled(fixedRateString = "400000")
    public void trasformaTrackingCPM() {

        try {
            // trovo uttti i tracking con read == false
            cpmBusiness.getUnread().stream().filter(cpmDTO -> cpmDTO.getRefferal() != null ).forEach(cpmDTO -> {
                TransactionBusiness.BaseCreateRequest rr = new TransactionBusiness.BaseCreateRequest();

                // prendo reffereal e lo leggo
                Refferal refferal = this.decodificaRefferal(cpmDTO.getRefferal());
                log.info("CPM :: {} - {}", cpmDTO.getRefferal(), refferal);

                // gesione commisione
                AffiliateChannelCommissionCampaign accc = affiliateChannelCommissionCampaignRepository.findByAffiliateIdAndChannelIdAndCampaignId(refferal.getAffiliateId(), refferal.getChannelId(), refferal.getCampaignId());
                rr.setCommissionId(accc.getCommission().getId());
                rr.setValue(Double.valueOf(accc.getCommission().getValue()));

                // setta transazione
                rr.setAffiliateId(refferal.getAffiliateId());
                rr.setCampaignId(refferal.getCampaignId());
                rr.setChannelId(refferal.getChannelId());
                rr.setType("CPM");
                rr.setDateTime(cpmDTO.getDate());
                rr.setApproved(false);

                // associo a wallet
                rr.setWalletId(walletRepository.findByAffiliateId(refferal.getAffiliateId()).getId());

                // creo la transazione
                transactionBusiness.create(rr);
                log.info(">>>>>>> TRASNSAZIONE >>>>> {}", rr);

                // setto a gestito
                cpmBusiness.setRead(cpmDTO.getId());
            });
        } catch (Exception e) {
            log.error("Eccezione Scheduler CPM --  {}", e.getMessage(), e);
        }

    }//trasformaTrackingCPM

    @Scheduled(fixedRateString = "50000")
    public void trasformaTrackingCPL() {

        try {
            // trovo uttti i tracking con read == false
            cplBusiness.getUnread().stream().filter(cplDTO -> StringUtils.isNotBlank(cplDTO.getCid())).forEach(cplDTO -> {

                // prendo reffereal e lo leggo
                String refferal = cplDTO.getCid();

                byte[] decoder = Base64.getDecoder().decode(refferal);
                String campaignId = new String(decoder);
                log.info("CPL :: {} - {}", refferal, campaignId);

                // setta transazione
                TransactionBusiness.BaseCreateRequest rr = new TransactionBusiness.BaseCreateRequest();
                rr.setCampaignId(Long.valueOf(refferal));
                rr.setType("CPL");
                rr.setDateTime(cplDTO.getDate());
                rr.setApproved(false);

                // creo la transazione
                transactionBusiness.create(rr);
                log.info(">>>>>>> TRASNSAZIONE >>>>> {}", rr);

                // setto a gestito
                cplBusiness.setRead(cplDTO.getId());
            });
        } catch (Exception e) {
            log.error("Eccezione Scheduler CPL --  {}", e.getMessage(), e);
        }

    }//trasformaTrackingCPL

}
