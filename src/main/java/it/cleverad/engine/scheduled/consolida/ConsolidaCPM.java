package it.cleverad.engine.scheduled.consolida;

import it.cleverad.engine.business.TransactionCPMBusiness;
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
public class ConsolidaCPM {

    @Autowired
    private TransactionCPMBusiness transactionCPMBusiness;

    @Async
    @Scheduled(cron = "9 59 * * * ?")
    public void ciclaCPM() {
        LocalDateTime oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        consolidaCPM(oraSpaccata);
        consolidaCPM(LocalDate.now().minusDays(1).atTime(LocalTime.MAX));

        // CONSOLIDA DATE PASSATE
        //        for (int i = 45; i < 60; i++) {
        //               consolidaCPM(LocalDate.now().minusDays(i).atTime(LocalTime.MAX));
        //        }
    }//trasformaTrackingCPC

    public void consolidaCPM(LocalDateTime oraSpaccata) {
        log.trace("\n\n\nCONSOLIDA CPM " + oraSpaccata.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        TransactionCPMBusiness.Filter request = new TransactionCPMBusiness.Filter();
        request.setDateTimeFrom(oraSpaccata.toLocalDate().atStartOfDay());
        request.setDateTimeTo(oraSpaccata);
        Page<TransactionCPMDTO> cpcM = transactionCPMBusiness.searchCpm(request, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id"))));

        ArrayList<Triple<Long, Long, Long>> triples = new ArrayList<>();
        for (TransactionCPMDTO tcpm : cpcM) {
            Triple<Long, Long, Long> triple = new ImmutableTriple<>(tcpm.getCampaignId(), tcpm.getAffiliateId(), tcpm.getChannelId());
            triples.add(triple);
        }

        List<Triple<Long, Long, Long>> listWithoutDuplicates = triples.stream().distinct().collect(Collectors.toList());
        for (Triple ttt : listWithoutDuplicates) {

            request = new TransactionCPMBusiness.Filter();
            oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            request.setDateTimeFrom(oraSpaccata.toLocalDate().atStartOfDay());
            request.setDateTimeTo(oraSpaccata);
            request.setCampaignId((Long) ttt.getLeft());
            request.setAffiliateId((Long) ttt.getMiddle());
            request.setChannelId((Long) ttt.getRight());
            cpcM = transactionCPMBusiness.searchCpm(request, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id"))));

            Long impressionNumber = 0L;
            Double value = 0D;
            Long walletId = null;
            Long mediaId = null;
            Long dictionaryId = null;
            Long revenueId = null;
            Long commissionId = null;
            Long statusId = null;
            String data = null;
            for (TransactionCPMDTO tcpm : cpcM) {
                impressionNumber += tcpm.getImpressionNumber();
                value += tcpm.getValue();
                walletId = tcpm.getWalletId();
                mediaId = tcpm.getMediaId();
                dictionaryId = tcpm.getDictionaryId();
                revenueId = tcpm.getRevenueId();
                commissionId = tcpm.getCommissionId();
                statusId = tcpm.getStatusId();
                data = tcpm.getNote();
                log.trace("TRANSAZIONE CPM ID :: {} : {} :: {}", tcpm.getId(), tcpm.getImpressionNumber(), tcpm.getDateTime());
                transactionCPMBusiness.deleteInterno(tcpm.getId());
                log.trace("DELETE {} ", tcpm.getId());
            }
            if (impressionNumber > 0) {
                log.trace("CONSOLIDATO CPM :: {} :: {} ::: {} - {} - {} ::: ", impressionNumber, value, ttt.getLeft(), ttt.getMiddle(), ttt.getRight());
                TransactionCPMBusiness.BaseCreateRequest bReq = new TransactionCPMBusiness.BaseCreateRequest();
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
                bReq.setData(data);
                bReq.setStatusId(statusId);
                transactionCPMBusiness.createCpm(bReq);
            }

        }//tripletta
    }//trasformaTrackingCPC

}