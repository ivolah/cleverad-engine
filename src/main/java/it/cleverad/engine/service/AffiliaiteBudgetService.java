package it.cleverad.engine.service;

import it.cleverad.engine.business.AffiliateBudgetBusiness;
import it.cleverad.engine.business.TransactionCPCBusiness;
import it.cleverad.engine.business.TransactionCPLBusiness;
import it.cleverad.engine.persistence.model.service.TransactionCPC;
import it.cleverad.engine.persistence.model.service.TransactionCPL;
import it.cleverad.engine.web.dto.AffiliateBudgetDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private AffiliateBudgetBusiness affiliateBudgetBusiness;

    public void rigeneraAffiliateBudget(Integer anno, Integer mese, Integer giorno, Long affiliateId, Long campaignId) {

        int start = (giorno == null) ? 1 : giorno;
        int end = (giorno == null) ? LocalDate.of(anno, mese, 1).lengthOfMonth() : giorno;

        LocalDate dataDtart = LocalDate.of(anno, mese, start);
        LocalDate dataEnd = LocalDate.of(anno, mese, end);

        // 1. --- TRANSAZIONI CON VALORE > 0 non blacklisted e non rifiutate
        // trovo tutti le campagne delle ultime 24 ore
        List<Long> campaignIdsCPL = transactionCPLBusiness.searchForCampaignAffiliateBudget(campaignId, affiliateId, dataDtart, dataEnd).stream().mapToLong(value -> value.getCampaign().getId()).boxed().collect(Collectors.toList());
        List<Long> campaignIdsCPC = transactionCPCBusiness.searchForCampaignAffiliateBudget(campaignId, affiliateId, dataDtart, dataEnd).stream().mapToLong(value -> value.getCampaign().getId()).boxed().collect(Collectors.toList());
        campaignIdsCPL.addAll(campaignIdsCPC);
        campaignIdsCPL = campaignIdsCPL.stream().distinct().collect(Collectors.toList());

        // 2. --- Gli AFFILAITE BUDGET attivi sulle campagne
        List<AffiliateBudgetDTO> allBudgets = new ArrayList<>();
        campaignIdsCPL.stream().forEach(id -> {
            //List<AffiliateBudgetDTO> budgetDTOS = affiliateBudgetBusiness.getByActiveIdCampaign(id).getContent();
            List<AffiliateBudgetDTO> budgetDTOS = affiliateBudgetBusiness.getByIdCampaign(id).getContent();
            allBudgets.addAll(budgetDTOS);
        });

        log.trace("CAMPAIGNS SIZE: " + campaignIdsCPL.size() + " :: BUDGETS SIZE: " + allBudgets.size());

        // 3. --- Per ogni affiliate budget trovo le transazioni con valore > 0 non blacklisted
        // nell'arco di tempo valevole per il budget e non rifiutate e aggirono il valore del budget affiliato
        allBudgets.stream().distinct().forEach(affiliateBudgetDTO -> {
            // prendo data inizio e fine del affilaite budget
            LocalDate budgetStart = affiliateBudgetDTO.getStartDate();
            LocalDate budgetEnd = affiliateBudgetDTO.getDueDate();

            Double totaleCPL = 0.0D;
            Double totaleCPC = 0.0D;
            Long capCPL = 0L;
            Long capCPC = 0L;

            // CPL
            List<TransactionCPL> abCpl = transactionCPLBusiness.searchForCampaignAffiliateBudget(affiliateBudgetDTO.getCampaignId(), affiliateBudgetDTO.getAffiliateId(), budgetStart, budgetEnd);
            totaleCPL = abCpl.stream().mapToDouble(TransactionCPL::getValue).sum();
            capCPL += abCpl.size();

            // CPC
            List<TransactionCPC> abCpc = transactionCPCBusiness.searchForCampaignAffiliateBudget(affiliateBudgetDTO.getCampaignId(), affiliateBudgetDTO.getAffiliateId(), budgetStart, budgetEnd);
            totaleCPC = abCpc.stream().mapToDouble(TransactionCPC::getValue).sum();
            capCPC += abCpc.stream().mapToLong(TransactionCPC::getClickNumber).sum();

            double totale = DoubleRounder.round(totaleCPL + totaleCPC, 2);
            Long cap = capCPL + capCPC;
            Integer capTot = Math.toIntExact(affiliateBudgetDTO.getInitialCap() - cap);
            log.trace("\nAFF BUDGET ::{} ({}) {} : ({}) {}", affiliateBudgetDTO.getId(), affiliateBudgetDTO.getAffiliateId(), affiliateBudgetDTO.getAffiliateName(), affiliateBudgetDTO.getCampaignId(), affiliateBudgetDTO.getCampaignName());
            log.trace("CPL ({}:{})  ::: CPC ({}:{})", totaleCPL, capCPL, totaleCPC, capCPC);
            log.trace("BUDGET ({}) {} - {} === {}", affiliateBudgetDTO.getBudget(), affiliateBudgetDTO.getInitialBudget(), totale, affiliateBudgetDTO.getInitialBudget() - totale);
            log.trace("CAP ({}) {} - {} === {}", affiliateBudgetDTO.getCap(), affiliateBudgetDTO.getInitialCap(), cap, capTot);
            if (totale > 0 && cap > 0) {
                affiliateBudgetBusiness.updateBudget(affiliateBudgetDTO.getId(), DoubleRounder.round(affiliateBudgetDTO.getInitialBudget() - totale, 2));
                affiliateBudgetBusiness.updateCap(affiliateBudgetDTO.getId(), capTot);
            }
        });

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class FilterUpdate {
        private Integer year;
        private Integer month;
        private Integer day;
        private Long affiliateId;
        private Long campaignId;
    }

}