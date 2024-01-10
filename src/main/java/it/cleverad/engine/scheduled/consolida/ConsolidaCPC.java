package it.cleverad.engine.scheduled.consolida;

import it.cleverad.engine.business.TransactionCPCBusiness;
import it.cleverad.engine.web.dto.TransactionCPCDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ConsolidaCPC {

    @Autowired
    private TransactionCPCBusiness transactionCPCBusiness;

    @Async
    @Scheduled(cron = "23 58 * * * ?")
    public void ciclaCPC() {
        LocalDateTime oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        consolidaCPC(oraSpaccata, false);
        consolidaCPC(oraSpaccata, true);
    }//trasformaTrackingCPC

    public void consolidaCPC(LocalDateTime oraSpaccata, Boolean blacklisted) {

        TransactionCPCBusiness.Filter request = new TransactionCPCBusiness.Filter();
        request.setDateTimeFrom(oraSpaccata.toLocalDate().atStartOfDay());
        request.setDateTimeTo(oraSpaccata);
        if(blacklisted) {
            // setto rifiutato
            request.setStatusId(74L);
            // setto blacklisted
            request.setDictionaryId(70L);
        }
        else {
            request.setStatusId(72L);
        }

        Page<TransactionCPCDTO> cpcs = transactionCPCBusiness.searchCpc(request, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id"))));

        List<Triple> triples = new ArrayList<>();
        for (TransactionCPCDTO tcpm : cpcs) {
            Triple<Long, Long, Long> triple = new ImmutableTriple<>(tcpm.getCampaignId(), tcpm.getAffiliateId(), tcpm.getChannelId());
            triples.add(triple);
        }
        List<Triple> listWithoutDuplicates = triples.stream().distinct().collect(Collectors.toList());

        for (Triple ttt : listWithoutDuplicates) {
            request = new TransactionCPCBusiness.Filter();
            oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            request.setDateTimeFrom(oraSpaccata.toLocalDate().atStartOfDay());
            request.setDateTimeTo(oraSpaccata);
            request.setCampaignId((Long) ttt.getLeft());
            request.setAffiliateId((Long) ttt.getMiddle());
            request.setChannelId((Long) ttt.getRight());
            cpcs = transactionCPCBusiness.searchCpc(request, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id"))));

            Long totaleClick = 0L;
            Double value = 0D;
            Long walletId = null;
            Long mediaId = null;
            Long dictionaryId = null;
            Long revenueId = null;
            Long commissionId = null;
            Long statusId = null;
            for (TransactionCPCDTO tcpc : cpcs) {
                totaleClick += tcpc.getClickNumber();
                value += tcpc.getValue();
                walletId = tcpc.getWalletId();
                mediaId = tcpc.getMediaId();
                dictionaryId = tcpc.getDictionaryId();
                revenueId = tcpc.getRevenueId();
                commissionId = tcpc.getCommissionId();
                statusId = tcpc.getStatusId();
                log.trace("TRANSAZIONE CPC ID :: {} : {} :: {}", tcpc.getId(), tcpc.getClickNumber(), tcpc.getDateTime());
                transactionCPCBusiness.deleteInterno(tcpc.getId());
            }
            if (totaleClick > 0) {
                log.trace("CONSOLIDATO CPC :: {} :: {} ::: {} - {} - {} ::: ", totaleClick, value, ttt.getLeft(), ttt.getMiddle(), ttt.getRight(), totaleClick, value);
                TransactionCPCBusiness.BaseCreateRequest bReq = new TransactionCPCBusiness.BaseCreateRequest();
                bReq.setClickNumber(totaleClick);
                bReq.setValue(value);
                bReq.setCampaignId((Long) ttt.getLeft());
                bReq.setAffiliateId((Long) ttt.getMiddle());
                bReq.setChannelId((Long) ttt.getRight());
                bReq.setCommissionId(commissionId);
                bReq.setApproved(true);
                bReq.setDateTime(oraSpaccata.minusMinutes(1));
                bReq.setMediaId(mediaId);
                bReq.setDictionaryId(dictionaryId);
                bReq.setRevenueId(revenueId);
                bReq.setWalletId(walletId);
                bReq.setAgent("");
                bReq.setStatusId(statusId);
                transactionCPCBusiness.createCpc(bReq);
            }

        }//tripletta

    }//trasformaTrackingCPC

}