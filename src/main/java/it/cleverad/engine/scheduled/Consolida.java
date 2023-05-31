package it.cleverad.engine.scheduled;

import it.cleverad.engine.business.AffiliateChannelCommissionCampaignBusiness;
import it.cleverad.engine.business.CampaignBusiness;
import it.cleverad.engine.business.TransactionBusiness;
import it.cleverad.engine.web.dto.AffiliateChannelCommissionCampaignDTO;
import it.cleverad.engine.web.dto.CampaignDTO;
import it.cleverad.engine.web.dto.TransactionCPCDTO;
import it.cleverad.engine.web.dto.TransactionCPMDTO;
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

    @Scheduled(cron = "0 59 * * * ?")
    public void consolidaCPC() {

        List<CampaignDTO> camps = campaignBusiness.getEnabledCampaigns();
        List<AffiliateChannelCommissionCampaignDTO> acccs = new ArrayList<>();
        camps.stream().forEach(campaignDTO -> {
            acccs.addAll(acccBusiness.searchByCampaignIdAndType(campaignDTO.getId(), 10L, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id")))).getContent());
        });
        for (AffiliateChannelCommissionCampaignDTO dto : acccs) {
            log.info("ACCC CPC :: {}  - {}  - {}", dto.getCampaignId(), dto.getAffiliateId(), dto.getChannelId());

            TransactionBusiness.Filter request = new TransactionBusiness.Filter();
            LocalDateTime oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            request.setDateTimeFrom(oraSpaccata.toLocalDate().atStartOfDay());
            request.setDateTimeTo(oraSpaccata);
            request.setCampaignId(dto.getCampaignId());
            request.setAffiliateId(dto.getAffiliateId());
            request.setChannelId(dto.getChannelId());
            //request.setCommissionId(dto.getCommissionId());
            Page<TransactionCPCDTO> cpcs = transactionBusiness.searchCpc(request, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id"))));

            Long totaleClick = 0L;
            Double value = 0D;
            Long walletId = null;
            Long mediaId = null;
            Long dictionaryId = null;
            Long revenueId = null;
            for (TransactionCPCDTO tcpc : cpcs) {
                totaleClick += tcpc.getClickNumber();
                value += tcpc.getValue();
                walletId = tcpc.getWalletId();
                mediaId = tcpc.getMediaId();
                dictionaryId = tcpc.getDictionaryId();
                revenueId = tcpc.getRevenueId();
                log.info("TRANSAZIONE CPC ID :: {} : {} :: {}", tcpc.getId(), tcpc.getClickNumber(), tcpc.getDateTime());
                transactionBusiness.deleteInterno(tcpc.getId(), "CPC");
            }
            if (totaleClick > 0) {
                log.info("Click {}  x valore {}\n\n", totaleClick, value);


                TransactionBusiness.BaseCreateRequest bReq = new TransactionBusiness.BaseCreateRequest();
                bReq.setClickNumber(totaleClick);
                bReq.setValue(value);
                bReq.setCampaignId(dto.getCampaignId());
                bReq.setAffiliateId(dto.getAffiliateId());
                bReq.setChannelId(dto.getChannelId());
                bReq.setCommissionId(dto.getCommissionId());
                bReq.setApproved(true);
                bReq.setDateTime(oraSpaccata.minusMinutes(1));
                bReq.setMediaId(mediaId);
                bReq.setDictionaryId(dictionaryId);
                bReq.setRevenueId(revenueId);
                bReq.setWalletId(walletId);
                bReq.setAgent("");
                TransactionCPCDTO cpc = transactionBusiness.createCpc(bReq);
            }
        }


    }//trasformaTrackingCPC

    @Scheduled(cron = "0 0/1 * * * ?")
    public void consolidaCPM()  {

        List<CampaignDTO> camps = campaignBusiness.getEnabledCampaigns();
        List<AffiliateChannelCommissionCampaignDTO> acccs = new ArrayList<>();
        camps.stream().forEach(campaignDTO -> {
            acccs.addAll(acccBusiness.searchByCampaignIdAndType(campaignDTO.getId(), 50L, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id")))).getContent());
        });
        for (AffiliateChannelCommissionCampaignDTO dto : acccs) {
            log.info("ACCC CPM :: {}  - {}  - {}", dto.getCampaignId(), dto.getAffiliateId(), dto.getChannelId());

            TransactionBusiness.Filter request = new TransactionBusiness.Filter();
            LocalDateTime oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            request.setDateTimeFrom(oraSpaccata.toLocalDate().atStartOfDay());
            request.setDateTimeTo(oraSpaccata);
            request.setCampaignId(dto.getCampaignId());
            request.setAffiliateId(dto.getAffiliateId());
            request.setChannelId(dto.getChannelId());
            //request.setCommissionId(dto.getCommissionId());
            Page<TransactionCPMDTO> cpcs = transactionBusiness.searchCpm(request, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id"))));

            Long impressionNumber = 0L;
            Double value = 0D;
            Long walletId = null;
            Long mediaId = null;
            Long dictionaryId = null;
            Long revenueId = null;
            for (TransactionCPMDTO tcpm : cpcs) {
                impressionNumber += tcpm.getImpressionNumber();
                value += tcpm.getValue();
                walletId = tcpm.getWalletId();
                mediaId = tcpm.getMediaId();
                dictionaryId = tcpm.getDictionaryId();
                revenueId = tcpm.getRevenueId();
                log.info("TRANSAZIONE CPM ID :: {} : {} :: {}", tcpm.getId(), tcpm.getImpressionNumber(), tcpm.getDateTime());
                //transactionBusiness.deleteInterno(tcpc.getId(), "CPM");
            }
            if (impressionNumber > 0) {
                log.info("impression {}  x valore {}\n\n", impressionNumber, value);

                TransactionBusiness.BaseCreateRequest bReq = new TransactionBusiness.BaseCreateRequest();
                bReq.setImpressionNumber(impressionNumber);
                bReq.setValue(value);
                bReq.setCampaignId(dto.getCampaignId());
                bReq.setAffiliateId(dto.getAffiliateId());
                bReq.setChannelId(dto.getChannelId());
                bReq.setCommissionId(dto.getCommissionId());
                bReq.setApproved(true);
                bReq.setDateTime(oraSpaccata.minusMinutes(1));
                bReq.setMediaId(mediaId);
                bReq.setDictionaryId(dictionaryId);
                bReq.setRevenueId(revenueId);
                bReq.setWalletId(walletId);
                bReq.setAgent("");
                //transactionBusiness.createCpm(bReq);
            }
        }


    }//trasformaTrackingCPC


}
