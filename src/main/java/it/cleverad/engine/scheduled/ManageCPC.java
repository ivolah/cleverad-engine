package it.cleverad.engine.scheduled;

import it.cleverad.engine.business.CpcBusiness;
import it.cleverad.engine.business.TransactionBusiness;
import it.cleverad.engine.business.WalletBusiness;
import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.AffiliateChannelCommissionCampaign;
import it.cleverad.engine.persistence.repository.service.AffiliateChannelCommissionCampaignRepository;
import it.cleverad.engine.persistence.repository.service.WalletRepository;
import it.cleverad.engine.service.RefferalService;
import it.cleverad.engine.web.dto.CpcDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ManageCPC {

    @Autowired
    private CpcBusiness cpcBusiness;
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

    //TODO controlla quotidianamente se la data scadenza delle campagne è stata superata

    @Scheduled(cron = "0 0/30 * * * ?")
    public void trasformaTrackingCPC() {
        try {

            // trovo tutti i tracking con read == false
            Map<String, Integer> mappa = new HashMap<>();
            Page<CpcDTO> last = cpcBusiness.getUnreadLastHour();
            last.stream().filter(cpcDTO -> cpcDTO.getRefferal() != null).forEach(cpcDTO -> {
                // gestisco calcolatore
                Integer num = mappa.get(cpcDTO.getRefferal());
                if (num == null) num = 0;
                mappa.put(cpcDTO.getRefferal(), num + 1);
                // setto a gestito
                cpcBusiness.setRead(cpcDTO.getId());
            });

            mappa.forEach((s, aLong) -> {
                log.info("Gestisco trasformaTrackingCPC ID {}", aLong);

                // prendo reffereal e lo leggo
                Refferal refferal = refferalService.decodificaRefferal(s);
                log.info("CPC :: {} - {}", s, refferal);

                // setta transazione
                TransactionBusiness.BaseCreateRequest rr = new TransactionBusiness.BaseCreateRequest();
                rr.setAffiliateId(refferal.getAffiliateId());
                rr.setCampaignId(refferal.getCampaignId());
                rr.setChannelId(refferal.getChannelId());
           //     rr.setDateTime(LocalDateTime.now());
                rr.setMediaId(refferal.getMediaId());
                rr.setApproved(true);

                rr.setMediaId(refferal.getMediaId());

                // associo a wallet
                Long walletID = walletRepository.findByAffiliateId(refferal.getAffiliateId()).getId();
                rr.setWalletId(walletID);

                // gesione commisione
                List<AffiliateChannelCommissionCampaign> accc = affiliateChannelCommissionCampaignRepository.findByAffiliateIdAndChannelIdAndCampaignId(refferal.getAffiliateId(), refferal.getChannelId(), refferal.getCampaignId());
                accc.stream().forEach(affiliateChannelCommissionCampaign -> {
                    if (affiliateChannelCommissionCampaign.getCommission().getDictionary().getName().equals("CPC")) {
                        rr.setCommissionId(affiliateChannelCommissionCampaign.getCommission().getId());

                        Double totale = Double.valueOf(affiliateChannelCommissionCampaign.getCommission().getValue()) * aLong;
                        rr.setValue(totale);
                        rr.setClickNumber(Long.valueOf(aLong));

                        // incemento valore
                        walletBusiness.incement(walletID, totale);

                        // creo la transazione
                        transactionBusiness.createCpc(rr);
                    }
                });
            });

        } catch (Exception e) {
            log.error("Eccezione Scheduler CPC --  {}", e.getMessage(), e);
        }

    }//trasformaTrackingCPC

}
