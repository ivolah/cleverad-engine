package it.cleverad.engine.business;

import it.cleverad.engine.persistence.repository.service.WalletRepository;
import it.cleverad.engine.web.dto.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class RigeneraWalletBusiness {

    @Autowired
    private TransactionBusiness transactionBusiness;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private WalletBusiness walletBusiness;
    @Autowired
    private PayoutBusiness payoutBusiness;

    public void rigenera(Integer anno, Integer mese, Integer giorno, Long affiliateId) {
        try {
            if (affiliateId == null) {
                log.warn("RIGENERA WALLET - Affiliate ID nullo, non faccio nulla");
            } else {
                Integer start = giorno;
                Integer end = giorno;
                if (giorno == null) {
                    start = 1;
                    end = LocalDate.of(anno, mese, 1).lengthOfMonth();
                }

                LocalDate dataDaGestireStart = LocalDate.of(anno, mese, start);
                LocalDate dataDaGestireEnd = LocalDate.of(anno, mese, end);
                log.info(anno + "-" + mese + "-" + giorno + " >> " + dataDaGestireStart + " || " + dataDaGestireEnd + " per " + affiliateId);

                // 1 - prendo tutte le Transazioni > 0, con stato Approvato o Pending
                TransactionBusiness.Filter filter = new TransactionBusiness.Filter();
                filter.setAffiliateId(affiliateId);
                filter.setDateTimeFrom(dataDaGestireStart.atStartOfDay());
                filter.setDateTimeTo(LocalDateTime.of(dataDaGestireEnd, LocalTime.MAX));
                filter.setValueNotZero(true);
                List<Long> listStatues = new ArrayList<>();
                listStatues.add(72L);
                listStatues.add(73L);
                filter.setStatusIdIn(listStatues);

                // CPC
                List<TransactionCPCDTO> dtoCpcs = transactionBusiness.searchCpc(filter, PageRequest.of(0, Integer.MAX_VALUE)).stream().collect(Collectors.toList());
                // CPL
                List<TransactionCPLDTO> dtoCpls = transactionBusiness.searchCpl(filter, PageRequest.of(0, Integer.MAX_VALUE)).stream().collect(Collectors.toList());
                // CPM
                List<TransactionCPMDTO> dtoCpms = transactionBusiness.searchCpm(filter, PageRequest.of(0, Integer.MAX_VALUE)).stream().collect(Collectors.toList());
                // CPS
                List<TransactionCPSDTO> dtoCpss = transactionBusiness.searchCps(filter, PageRequest.of(0, Integer.MAX_VALUE)).stream().collect(Collectors.toList());

                // 2 - Faccio La Somma di tutti i valori delle transazioni
                Double totale = 0D;
                totale += dtoCpcs.stream().mapToDouble(TransactionCPCDTO::getValue).sum();
                totale += dtoCpls.stream().mapToDouble(TransactionCPLDTO::getValue).sum();
                totale += dtoCpms.stream().mapToDouble(TransactionCPMDTO::getValue).sum();
                totale += dtoCpss.stream().mapToDouble(TransactionCPSDTO::getValue).sum();
                totale = DoubleRounder.round(totale, 2);

                log.info("TOTALE per {} = {}", affiliateId, totale);

                // 3 - Cerco il Wallet e ne incremento il valore
                Long walletID = walletRepository.findByAffiliateId(affiliateId).getId();
                walletBusiness.incement(walletID, totale);

            }
        } catch (Exception e) {
            log.error("RIGENERA WALLET --  {}", e.getMessage(), e);
        }
    }

    public void decrementaPayout(Long affiliateId){

        // 1 - cerco eventuali Payout
        Page<PayoutDTO> payouts = payoutBusiness.findByIdAffilaite(affiliateId, Pageable.ofSize(Integer.MAX_VALUE));
        log.info("DIM PAYOUTS {}", payouts.getSize());
        Double totalePayouts = 0D;
        totalePayouts = payouts.stream().mapToDouble(PayoutDTO::getTotale).sum();
        log.info("TOTALE PAYOUTS {}", totalePayouts);

        // 2 - Cerco il Wallet e ne incremento il valore
        Long walletID = walletRepository.findByAffiliateId(affiliateId).getId();
        walletBusiness.decrement(walletID, totalePayouts);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class FilterUpdate {
        private String year;
        private String month;
        private String day;
        private Long affiliateId;
    }
}