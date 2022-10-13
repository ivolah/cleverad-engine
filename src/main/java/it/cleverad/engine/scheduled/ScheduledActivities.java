package it.cleverad.engine.scheduled;

import it.cleverad.engine.business.*;
import it.cleverad.engine.web.dto.AffiliateChannelCommissionCampaignDTO;
import it.cleverad.engine.web.dto.CommissionDTO;
import it.cleverad.engine.web.dto.WalletDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ScheduledActivities {

    @Autowired
    private TrackingBusiness trackingBusiness;

    @Autowired
    private TransactionBusiness transactionBusiness;

    @Autowired
    private WalletBusiness walletBusiness;

    @Autowired
    private CommissionBusiness commissionBusiness;

    @Autowired
    private AffiliateChannelCommissionCampaignBusiness affiliateChannelCommissionCampaignBusiness;

    // controlla quotidianamente se la data scadenza delle campagne è stata superata
    @Scheduled(fixedRateString = "60000")
    public void trasformaTracking() {
        try {
            // trovo uttti i tracking con read == false
            trackingBusiness.getUnread().stream().forEach(trackingDTO -> {

                // prendo reffereal e lo leggo
                String refferal = trackingDTO.getRefferalId();
                log.trace("REFF {}", refferal);

                byte[] decoder = Base64.getDecoder().decode(refferal);
                String str = new String(decoder);
                log.trace("TTTT {}", str);

                String[] tokens = str.split("\\|\\|");
                String mediaID = tokens[1];

                // setta transazione
                TransactionBusiness.BaseCreateRequest rr = new TransactionBusiness.BaseCreateRequest();
                rr.setAffiliateId(Long.valueOf(tokens[2]));
                rr.setCampaignId(Long.valueOf(tokens[0]));
                rr.setChannelId(Long.valueOf(tokens[3]));
                rr.setType("CPC");
                rr.setDateTime(trackingDTO.getCreationDate());
                rr.setApproved(false);

                // associo a wallet
                WalletDTO walletDTO = walletBusiness.findByIdAffilaite(Long.valueOf(tokens[2])).stream().findFirst().get();
                rr.setWalletId(walletDTO.getId());

                // g4esione commisione
                AffiliateChannelCommissionCampaignBusiness.Filter request = new AffiliateChannelCommissionCampaignBusiness.Filter();
                request.setAffiliateId(Long.valueOf(tokens[2]));
                request.setChannelId(Long.valueOf(tokens[3]));
                request.setCampaignId(Long.valueOf(tokens[0]));
                Page<AffiliateChannelCommissionCampaignDTO> ress = affiliateChannelCommissionCampaignBusiness.search(request, PageRequest.of(0, 10000, Sort.by(Sort.Order.asc("id"))));
                List<Long> cid = ress.stream().map(AffiliateChannelCommissionCampaignDTO::getCommissionId).collect(Collectors.toList());
                log.trace("DIM COMM {}", cid.size());
                cid.stream().forEach(aLong -> {

                    rr.setCommissionId(aLong);
                    CommissionDTO comm = commissionBusiness.findById(aLong);
                    rr.setValue(Double.valueOf(comm.getValue()));
                    log.info(rr.toString());
                    transactionBusiness.create(rr);

                });

                trackingBusiness.setRead(trackingDTO.getId());

            });
        } catch (Exception e) {
            log.error("Eccezione Scheduler --  {}", e.getMessage());
        }

    }//trasformaTracking

}
