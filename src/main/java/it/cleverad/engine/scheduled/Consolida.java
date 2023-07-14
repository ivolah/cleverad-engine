package it.cleverad.engine.scheduled;

import it.cleverad.engine.business.TransactionBusiness;
import it.cleverad.engine.web.dto.TransactionCPCDTO;
import it.cleverad.engine.web.dto.TransactionCPMDTO;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class Consolida {

    @Autowired
    private TransactionBusiness transactionBusiness;

    @Async
    @Scheduled(cron = "8 58 * * * ?")
    public void ciclaCPC() {
        LocalDateTime oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        consolidaCPC(oraSpaccata);
    }//trasformaTrackingCPC

    public void consolidaCPC(LocalDateTime oraSpaccata) {

        TransactionBusiness.Filter request = new TransactionBusiness.Filter();
        request.setDateTimeFrom(oraSpaccata.toLocalDate().atStartOfDay());
        request.setDateTimeTo(oraSpaccata);
        Page<TransactionCPCDTO> cpcs = transactionBusiness.searchCpc(request, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id"))));

        List<Triple> triples = new ArrayList<>();
        for (TransactionCPCDTO tcpm : cpcs) {
            Triple<Long, Long, Long> triple = new ImmutableTriple<>(tcpm.getCampaignId(), tcpm.getAffiliateId(), tcpm.getChannelId());
            triples.add(triple);
        }
        List<Triple> listWithoutDuplicates = triples.stream().distinct().collect(Collectors.toList());

        for (Triple ttt : listWithoutDuplicates) {
            request = new TransactionBusiness.Filter();
            oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            request.setDateTimeFrom(oraSpaccata.toLocalDate().atStartOfDay());
            request.setDateTimeTo(oraSpaccata);
            request.setCampaignId((Long) ttt.getLeft());
            request.setAffiliateId((Long) ttt.getMiddle());
            request.setChannelId((Long) ttt.getRight());
            cpcs = transactionBusiness.searchCpc(request, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id"))));

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
                transactionBusiness.deleteInterno(tcpc.getId(), "CPC");
            }
            if (totaleClick > 0) {
                log.trace("CONSOLIDATO CPC :: {} :: {} ::: {} - {} - {} ::: ", totaleClick, value, ttt.getLeft(), ttt.getMiddle(), ttt.getRight(), totaleClick, value);
                TransactionBusiness.BaseCreateRequest bReq = new TransactionBusiness.BaseCreateRequest();
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
                transactionBusiness.createCpc(bReq);
            }

        }//tripletta

    }//trasformaTrackingCPC

    @Async
    @Scheduled(cron = "9 59 * * * ?")
    public void ciclaCPM() {
        LocalDateTime oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        consolidaCPM(oraSpaccata);
        consolidaCPM(LocalDate.now().minusDays(1).atTime(LocalTime.MAX));

        for (int i = 45; i < 60; i++) {
            //   consolidaCPM(LocalDate.now().minusDays(i).atTime(LocalTime.MAX));
        }
    }//trasformaTrackingCPC

    public void consolidaCPM(LocalDateTime oraSpaccata) {
        log.info("\n\n\nCONSOLIDA CPM " + oraSpaccata.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        TransactionBusiness.Filter request = new TransactionBusiness.Filter();
        request.setDateTimeFrom(oraSpaccata.toLocalDate().atStartOfDay());
        request.setDateTimeTo(oraSpaccata);
        Page<TransactionCPMDTO> cpcM = transactionBusiness.searchCpm(request, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id"))));

        List<Triple> triples = new ArrayList<>();
        for (TransactionCPMDTO tcpm : cpcM) {
            Triple<Long, Long, Long> triple = new ImmutableTriple<>(tcpm.getCampaignId(), tcpm.getAffiliateId(), tcpm.getChannelId());
            triples.add(triple);
        }

        List<Triple> listWithoutDuplicates = triples.stream().distinct().collect(Collectors.toList());
        for (Triple ttt : listWithoutDuplicates) {

            request = new TransactionBusiness.Filter();
            oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            request.setDateTimeFrom(oraSpaccata.toLocalDate().atStartOfDay());
            request.setDateTimeTo(oraSpaccata);
            request.setCampaignId((Long) ttt.getLeft());
            request.setAffiliateId((Long) ttt.getMiddle());
            request.setChannelId((Long) ttt.getRight());
            cpcM = transactionBusiness.searchCpm(request, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id"))));

            Long impressionNumber = 0L;
            Double value = 0D;
            Long walletId = null;
            Long mediaId = null;
            Long dictionaryId = null;
            Long revenueId = null;
            Long commissionId = null;
            Long statusId = null;
            for (TransactionCPMDTO tcpm : cpcM) {
                impressionNumber += tcpm.getImpressionNumber();
                value += tcpm.getValue();
                walletId = tcpm.getWalletId();
                mediaId = tcpm.getMediaId();
                dictionaryId = tcpm.getDictionaryId();
                revenueId = tcpm.getRevenueId();
                commissionId = tcpm.getCommissionId();
                statusId = tcpm.getStatusId();
                log.trace("TRANSAZIONE CPM ID :: {} : {} :: {}", tcpm.getId(), tcpm.getImpressionNumber(), tcpm.getDateTime());
                transactionBusiness.deleteInterno(tcpm.getId(), "CPM");
                log.trace("DELETE {} ", tcpm.getId());
            }
            if (impressionNumber > 0) {
                log.info("CONSOLIDATO CPM :: {} :: {} ::: {} - {} - {} ::: ", impressionNumber, value, ttt.getLeft(), ttt.getMiddle(), ttt.getRight());
                TransactionBusiness.BaseCreateRequest bReq = new TransactionBusiness.BaseCreateRequest();
                bReq.setImpressionNumber(impressionNumber);
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
                transactionBusiness.createCpm(bReq);
            }

        }//tripletta
    }//trasformaTrackingCPC

}
