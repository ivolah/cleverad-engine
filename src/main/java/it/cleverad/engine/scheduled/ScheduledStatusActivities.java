package it.cleverad.engine.scheduled;//package it.cleverad.engine.scheduled;

import it.cleverad.engine.business.BudgetBusiness;
import it.cleverad.engine.business.CampaignBusiness;
import it.cleverad.engine.business.CommissionBusiness;
import it.cleverad.engine.business.RevenueFactorBusiness;
import it.cleverad.engine.web.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Async
public class ScheduledStatusActivities {

    @Autowired
    private CampaignBusiness campaignBusiness;
    @Autowired
    private BudgetBusiness budgetBusiness;
    @Autowired
    private CommissionBusiness commissionBusiness;
    @Autowired
    private RevenueFactorBusiness revenueFactorBusiness;

    @Scheduled(cron = "30 0 0 * * ?")
    public void aggiornaStato() {
        log.info("AGGIORNAMENTO QUOTIDIANO STATO");

        // aggiorno Stato Budget
        List<BudgetDTO> listaBudget = budgetBusiness.getBudgetToDisable();
        listaBudget.stream().forEach(budgetDTO -> {
            budgetBusiness.disable(budgetDTO.getId());
            log.info("Disable Budget : {}", budgetDTO.getId());
        });

        // aggiorao sato commission
        List<CommissionDTO> listaCommission = commissionBusiness.getCommissionToDisable();
        listaCommission.stream().forEach(comm -> {
            commissionBusiness.disable(comm.getId());
            log.info("Disable Commission : {}", comm.getId());
        });

        // aggiorno stato revenue
        List<RevenueFactorDTO> listaRevenu = revenueFactorBusiness.getRevenueToDisable();
        listaRevenu.stream().forEach(rr -> {
            revenueFactorBusiness.disable(rr.getId());
            log.info("Disable Revenue : {}", rr.getId());
        });

        // aggiorno Stato Campagne
        List<CampaignDTO> listaCampagne = campaignBusiness.getCampaignsToDisable();
        listaCampagne.stream().forEach(campaignDTO -> {
            campaignBusiness.disable(campaignDTO.getId());
            log.info("Disable Campaign : {}", campaignDTO.getId());
        });

    }

}