package it.cleverad.engine.scheduled;

import it.cleverad.engine.business.AffiliateChannelCommissionCampaignBusiness;
import it.cleverad.engine.business.CampaignBusiness;
import it.cleverad.engine.business.TransactionBusiness;
import it.cleverad.engine.web.dto.AffiliateChannelCommissionCampaignDTO;
import it.cleverad.engine.web.dto.CampaignDTO;
import it.cleverad.engine.web.dto.TransactionCPCDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class Consolida {

    @Autowired
    private TransactionBusiness transactionBusiness;
    @Autowired
    private CampaignBusiness campaignBusiness;
    @Autowired
    private AffiliateChannelCommissionCampaignBusiness acccBusiness;

    @Scheduled(cron = "30 * * * * ?")
    public void consolidaCPC() {

        log.info(LocalDateTime.now().toString() + "");
        log.info(LocalDateTime.now().getDayOfYear() + "  " + LocalDateTime.now().getDayOfMonth());

        List<CampaignDTO> camps = campaignBusiness.getEnabledCampaigns();
        List<AffiliateChannelCommissionCampaignDTO> acccs = new ArrayList<>();
        camps.stream().forEach(campaignDTO -> {
            acccs.addAll(acccBusiness.searchByCampaignId(campaignDTO.getId(), PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id")))).getContent());
        });
        log.info(acccs.size() + " == ACCC");
        for (AffiliateChannelCommissionCampaignDTO dto : acccs) {

            TransactionBusiness.Filter request = new TransactionBusiness.Filter();
//            LocalDateTime oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
//            request.setDateTimeFrom(oraSpaccata.minusHours(3));
//            request.setDateTimeTo(oraSpaccata);
            LocalDateTime oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            request.setDateTimeFrom(oraSpaccata.toLocalDate().atStartOfDay());
            request.setDateTimeTo(oraSpaccata);
            request.setCampaignId(dto.getCampaignId());
            request.setAffiliateId(dto.getAffiliateId());
            request.setChannelId(dto.getChannelId());
            Page<TransactionCPCDTO> cpcs = transactionBusiness.searchCpc(request, PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("id"))));

            cpcs.stream().forEach(tcpc -> {
                log.info("TRANSAZIONE CPC :: {}", tcpc.getId());
            });


        }


    }//trasformaTrackingCPC

}
