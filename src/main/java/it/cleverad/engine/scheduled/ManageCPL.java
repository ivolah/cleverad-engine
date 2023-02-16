package it.cleverad.engine.scheduled;

import it.cleverad.engine.business.CplBusiness;
import it.cleverad.engine.business.TransactionBusiness;
import it.cleverad.engine.business.WalletBusiness;
import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.AffiliateChannelCommissionCampaign;
import it.cleverad.engine.persistence.repository.service.AffiliateChannelCommissionCampaignRepository;
import it.cleverad.engine.persistence.repository.service.WalletRepository;
import it.cleverad.engine.service.RefferalService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ManageCPL {

    @Autowired
    private CplBusiness cplBusiness;
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

    @Scheduled(cron = "0 0/30 * * * ?")
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
                rr.setMediaId(refferal.getMediaId());
             //   rr.setDateTime(cplDTO.getDate());
                rr.setApproved(true);

                rr.setAgent(cplDTO.getAgent());
                rr.setIp(cplDTO.getIp());
                rr.setData(cplDTO.getData());

                rr.setMediaId(refferal.getMediaId());

                // associo a wallet
                Long walletID = walletRepository.findByAffiliateId(refferal.getAffiliateId()).getId();
                rr.setWalletId(walletID);

                // gesione commisione
                List<AffiliateChannelCommissionCampaign> accc = affiliateChannelCommissionCampaignRepository.findByAffiliateIdAndChannelIdAndCampaignId(refferal.getAffiliateId(), refferal.getChannelId(), refferal.getCampaignId());
                accc.stream().forEach(affiliateChannelCommissionCampaign -> {
                    if (affiliateChannelCommissionCampaign.getCommission().getDictionary().getName().equals("CPL")) {
                        rr.setCommissionId(affiliateChannelCommissionCampaign.getCommission().getId());

                        Double totale = Double.valueOf(affiliateChannelCommissionCampaign.getCommission().getValue()) * 1;
                        rr.setValue(totale);
                        rr.setClickNumber(Long.valueOf(1));

                        // incemento valore
                        walletBusiness.incement(walletID, totale);

                        // creo la transazione
                        transactionBusiness.createCpl(rr);
                    }
                });

                // setto a gestito
                cplBusiness.setRead(cplDTO.getId());

            });
        } catch (Exception e) {
            log.error("Eccezione Scheduler CPL --  {}", e.getMessage(), e);
        }
    }//trasformaTrackingCPL

}
