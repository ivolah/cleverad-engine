package it.cleverad.engine.service;

import it.cleverad.engine.business.PayoutBusiness;
import it.cleverad.engine.business.TransactionCPCBusiness;
import it.cleverad.engine.business.TransactionCPLBusiness;
import it.cleverad.engine.business.WalletBusiness;
import it.cleverad.engine.persistence.model.service.TransactionCPC;
import it.cleverad.engine.persistence.model.service.TransactionCPL;
import it.cleverad.engine.persistence.repository.service.WalletRepository;
import it.cleverad.engine.web.dto.PayoutDTO;
import it.cleverad.engine.web.dto.WalletDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@Transactional
@Service
public class WalletService {

    @Autowired
    private TransactionCPLBusiness transactionCPLBusiness;
    @Autowired
    private TransactionCPCBusiness transactionCPCBusiness;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private WalletBusiness walletBusiness;
    @Autowired
    private PayoutBusiness payoutBusiness;

    public void rigenera(Long affiliateId) {
        try {
            if (affiliateId == null) {
                // se affiliate non valorizzato prendo tutti gli wallet
                walletBusiness.getAll().get().mapToLong(WalletDTO::getAffiliateId).forEach(this::calcola);
            } else {
                calcola(affiliateId);
            }
        } catch (Exception e) {
            log.error("RIGENERA WALLET --  {}", e.getMessage(), e);
        }
    }

    private void calcola(Long affiliateId) {
        // 1. --- PAYED - faccio la somma dei payout
        Page<PayoutDTO> payouts = payoutBusiness.findByIdAffilaite(affiliateId, Pageable.ofSize(Integer.MAX_VALUE));
        Double totalePayouts = payouts.stream().mapToDouble(PayoutDTO::getTotale).sum();
        log.trace("TOTALE PAYOUTS {}", totalePayouts);

        // 2. --- TRANSAZIONI CON PAYOUT
        // CPC
        List<TransactionCPC> cpcs = transactionCPCBusiness.searchPayout(affiliateId, true);
        // CPL
        List<TransactionCPL> cpls = transactionCPLBusiness.searchPayout(affiliateId, true);
        // CPM
        //List<TransactionCPMDTO> dtoCpms = transactionBusiness.searchWithPayout(filter, PageRequest.of(0, Integer.MAX_VALUE)).stream().collect(Collectors.toList());
        // CPS
        //List<TransactionCPSDTO> dtoCpss = transactionBusiness.searchWithPayout(filter, PageRequest.of(0, Integer.MAX_VALUE)).stream().collect(Collectors.toList());

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
        Long walletID = walletRepository.findByAffiliateId(affiliateId).getId();
        WalletBusiness.Filter filter = new WalletBusiness.Filter();
        filter.setId(walletID);
        filter.setPayed(totalePayouts);
        filter.setTotal(totale);
        filter.setResidual(totale - totalePayouts);
        walletBusiness.update(walletID, filter);

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