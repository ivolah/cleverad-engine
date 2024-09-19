package it.cleverad.engine.scheduled.consolida;

import it.cleverad.engine.business.TransactionCPCBusiness;
import it.cleverad.engine.business.TransactionStatusBusiness;
import it.cleverad.engine.persistence.model.service.QueryTransaction;
import it.cleverad.engine.web.dto.TransactionCPCDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private TransactionStatusBusiness transactionStatusBusiness;

    @Async
    @Scheduled(cron = "23 58 * * * ?")
    public void ciclaCPC() {
        LocalDateTime oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        consolidaCPC(oraSpaccata, false);
        consolidaCPC(oraSpaccata, true);
    }//trasformaTrackingCPC

    public void consolidaCPC(LocalDateTime oraSpaccata, Boolean blacklisted) {

        TransactionStatusBusiness.QueryFilter request = new TransactionStatusBusiness.QueryFilter();
        request.setDateTimeFrom(oraSpaccata.toLocalDate());
        request.setDateTimeTo(oraSpaccata.toLocalDate());
        request.setTipo("CPC");
        List<Long> not = new ArrayList<>();
        not.add(68L); // MANUALE
        request.setNotInDictionaryId(not);
        not = new ArrayList<>();
        not.add(74L); // RIGETTATO
        request.setNotInStausId(not);
        Page<QueryTransaction> ls = transactionStatusBusiness.searchPrefiltratoN(request, Pageable.ofSize(Integer.MAX_VALUE));
        log.info(">>> TOT :: " + ls.getTotalElements());

        ArrayList<Triple> triples = new ArrayList<>();
        for (QueryTransaction queryTransaction : ls) {
            Triple<Long, Long, Long> triple
                    = new ImmutableTriple<>(
                    queryTransaction.getCampaignId(),
                    Long.parseLong(queryTransaction.getAffiliateid()),
                    queryTransaction.getChannelid()
            );
            triples.add(triple);
        }

        List<Triple> listWithoutDuplicates = triples.stream().distinct().collect(Collectors.toList());

        for (Triple ttt : listWithoutDuplicates) {
            TransactionCPCBusiness.Filter rrr = new TransactionCPCBusiness.Filter();
            oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            rrr.setDateTimeFrom(oraSpaccata.toLocalDate().atStartOfDay());
            rrr.setDateTimeTo(oraSpaccata);
            rrr.setCampaignId((Long) ttt.getLeft());
            rrr.setAffiliateId((Long) ttt.getMiddle());
            rrr.setChannelId((Long) ttt.getRight());
            Page<TransactionCPCDTO> cpcs = transactionCPCBusiness.searchCpc(rrr, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id"))));

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
                bReq.setValue(DoubleRounder.round(value, 2));
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