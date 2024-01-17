package it.cleverad.engine.service;

import it.cleverad.engine.business.AffiliateBudgetBusiness;
import it.cleverad.engine.business.TransactionCPCBusiness;
import it.cleverad.engine.business.TransactionCPLBusiness;
import it.cleverad.engine.persistence.model.service.TransactionCPC;
import it.cleverad.engine.persistence.model.service.TransactionCPL;
import it.cleverad.engine.persistence.repository.service.AffiliateBudgetRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@Transactional
@Service
public class AffiliaiteBudgetService {

    @Autowired
    private TransactionCPLBusiness transactionCPLBusiness;
    @Autowired
    private TransactionCPCBusiness transactionCPCBusiness;
    @Autowired
    private AffiliateBudgetRepository affiliateBudgetRepository;
    @Autowired
    private AffiliateBudgetBusiness affiliateBudgetBusiness;


    private void rigeneraAffiliateBudget(Long affiliateId, Long campaignId) {

        // 1. --- TRANSAZIONI CON VALORE > 0 non blacklisted e non rifiutate
        List<Long> campaignIdsCPL = (List<Long>) transactionCPLBusiness.searchLastModified().stream().mapToLong(value -> value.getCampaign().getId());
        List<Long> campaignIdsCPC = (List<Long>) transactionCPCBusiness.searchLastModified().stream().mapToLong(value -> value.getCampaign().getId());
        campaignIdsCPL.addAll(campaignIdsCPC);
campaignIdsCPL.stream().distinct();

        List<Long> affiliateIds = (List<Long>) transactionCPLBusiness.searchLastModified().stream().mapToLong(value -> value.getAffiliate().getId());

        // trovo le campagne
//        for (Long id : campaignIds) {
//            transactionCPLBusiness.searchByCampaign(id, Pageable.ofSize(Integer.MAX_VALUE));
//        }


        //searchByCampaignMese

        // CPC
        List<TransactionCPC> cpcs = transactionCPCBusiness.searchPayout(affiliateId, true);
        // CPL
        List<TransactionCPL> cpls = transactionCPLBusiness.searchPayout(affiliateId, true);
        // CPM
        //List<TransactionCPMDTO> dtoCpms = transactionBusiness.searchWithPayout(filter, PageRequest.of(0, Integer.MAX_VALUE)).stream().collect(Collectors.toList());
        // CPS
        //List<TransactionCPSDTO> dtoCpss = transactionBusiness.searchWithPayout(filter, PageRequest.of(0, Integer.MAX_VALUE)).stream().collect(Collectors.toList());


        // 1. --- PAYED - faccio la somma dei payout
//        Page<PayoutDTO> payouts = payoutBusiness.findByIdAffilaite(affiliateId, Pageable.ofSize(Integer.MAX_VALUE));
//        Double totalePayouts = payouts.stream().mapToDouble(PayoutDTO::getTotale).sum();
//        log.trace("TOTALE PAYOUTS {}", totalePayouts);

        // 3. --- TOTALE SENZA PAYOUT
        // CPC
        List<TransactionCPC> cpcsSenza = transactionCPCBusiness.searchPayout(affiliateId, false);
        // CPL
        List<TransactionCPL> cplsSenza = transactionCPLBusiness.searchPayout(affiliateId, false);

        // 4. --- SOMMA :: Faccio La Somma di tutti i valori delle transazioni
        double totaleConPayout = 0D;
        totaleConPayout += cpcs.stream().mapToDouble(TransactionCPC::getValue).sum();
        totaleConPayout += cpls.stream().mapToDouble(TransactionCPL::getValue).sum();
        log.trace("TOTALE CON PAYOUT per {} = {}", affiliateId, totaleConPayout);
        double totaleSenzaPayout = 0D;
        totaleSenzaPayout += cpcsSenza.stream().mapToDouble(TransactionCPC::getValue).sum();
        totaleSenzaPayout += cplsSenza.stream().mapToDouble(TransactionCPL::getValue).sum();
        log.trace("TOTALE SENZA PAYOUT per {} = {}", affiliateId, totaleSenzaPayout);

        double totale = DoubleRounder.round(totaleSenzaPayout + totaleConPayout, 2);
        log.trace("TOTALE per {} = {}", affiliateId, totaleSenzaPayout);

        // 5 - Cerco il Wallet e aggiorno
//        Long walletID = walletRepository.findByAffiliateId(affiliateId).getId();
//        WalletBusiness.Filter filter = new WalletBusiness.Filter();
//        filter.setId(walletID);
//        filter.setPayed(totalePayouts);
//        filter.setTotal(totale);
//        filter.setResidual(totale - totalePayouts);
//        walletBusiness.update(walletID, filter);

    }

    /**
     * filter.setDateTimeFrom(dataDaGestireStart.atStartOfDay());
     * filter.setDateTimeTo(LocalDateTime.of(dataDaGestireEnd, LocalTime.MAX));
     * <p>
     * int year = 2023;
     * int month = 12;
     * int combinedNumber = year * 100 + month;
     * // Extracting year and month from the combined number
     * int extractedYear = combinedNumber / 100;
     * int extractedMonth = combinedNumber % 100;
     **/

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class FilterUpdate {
        private String year;
        private String month;
        private String day;
        private Long affiliateId;
        private Long campaignId;
    }

}