package it.cleverad.engine.scheduled;//package it.cleverad.engine.scheduled;

import it.cleverad.engine.business.AffiliateBudgetBusiness;
import it.cleverad.engine.business.CampaignBusiness;
import it.cleverad.engine.business.CommissionBusiness;
import it.cleverad.engine.business.RevenueFactorBusiness;
import it.cleverad.engine.web.dto.AffiliateBudgetDTO;
import it.cleverad.engine.web.dto.CampaignDTO;
import it.cleverad.engine.web.dto.CommissionDTO;
import it.cleverad.engine.web.dto.RevenueFactorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@Async
public class DisableStausDailyScheduledActivities {

    @Autowired
    private CampaignBusiness campaignBusiness;
    @Autowired
    private AffiliateBudgetBusiness affiliateBudgetBusiness;
    @Autowired
    private CommissionBusiness commissionBusiness;
    @Autowired
    private RevenueFactorBusiness revenueFactorBusiness;

    @Scheduled(cron = "10 59 23 * * ?")
    public void aggiornaStato() {
        log.info("AGGIORNAMENTO QUOTIDIANO STATO");

        // aggiorno Stato Budget
        List<AffiliateBudgetDTO> listaBudget = affiliateBudgetBusiness.getBudgetToDisable();
        listaBudget.stream().forEach(budgetDTO -> {
            affiliateBudgetBusiness.disable(budgetDTO.getId());
            log.info("Disable Budget ({}) : {} ", LocalDate.now().plusDays(1), budgetDTO.getId());
        });

        // aggiorao sato commission
        List<CommissionDTO> listaCommission = commissionBusiness.getCommissionToDisable();
        listaCommission.stream().forEach(comm -> {
            commissionBusiness.disable(comm.getId());
            log.info("Disable Commission ({}) : {} ", LocalDate.now().plusDays(1), comm.getId());
        });

        // aggiorno stato revenue
        List<RevenueFactorDTO> listaRevenu = revenueFactorBusiness.getRevenueToDisable();
        listaRevenu.stream().forEach(rr -> {
            revenueFactorBusiness.disable(rr.getId());
            log.info("Disable Revenue ({}) : {} ", LocalDate.now().plusDays(1), rr.getId());
        });

        // aggiorno Stato Campagne
        List<CampaignDTO> listaCampagne = campaignBusiness.getCampaignsToDisable();
        listaCampagne.stream().forEach(campaignDTO -> {
            campaignBusiness.disable(campaignDTO.getId());
            log.info("Disable Campaign ({}) : {} ", LocalDate.now().plusDays(1), campaignDTO.getId());
        });

    }

}