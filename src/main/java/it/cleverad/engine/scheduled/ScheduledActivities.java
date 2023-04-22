package it.cleverad.engine.scheduled;//package it.cleverad.engine.scheduled;

import it.cleverad.engine.business.BudgetBusiness;
import it.cleverad.engine.business.CampaignBusiness;
import it.cleverad.engine.business.CommissionBusiness;
import it.cleverad.engine.business.RevenueFactorBusiness;
import it.cleverad.engine.service.RefferalService;
import it.cleverad.engine.web.dto.BudgetDTO;
import it.cleverad.engine.web.dto.CampaignDTO;
import it.cleverad.engine.web.dto.CommissionDTO;
import it.cleverad.engine.web.dto.RevenueFactorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ScheduledActivities {

    @Autowired
    private CampaignBusiness campaignBusiness;
    @Autowired
    private BudgetBusiness budgetBusiness;
    @Autowired
    private CommissionBusiness commissionBusiness;
    @Autowired
    private RevenueFactorBusiness revenueFactorBusiness;

    @Scheduled(cron = "0 15 0 * * ?")
    public void aggiornaStato() {
        log.info("AGGIORNAMENTO QUOTIDIANO STATO");

        // aggiorno Stato Budget
        List<BudgetDTO> listaBudget = budgetBusiness.getBudgetToDisable();
        listaBudget.stream().forEach(campaignDTO -> {
            budgetBusiness.disable(campaignDTO.getId());
        });

        // aggiorao sato commission
        List<CommissionDTO> listaCommission = commissionBusiness.getCommissionToDisable();
        listaCommission.stream().forEach(comm -> {
            commissionBusiness.disable(comm.getId());
        });

        // aggiorno stato revenue
        List<RevenueFactorDTO> listaRevenu = revenueFactorBusiness.getRevenueToDisable();
        listaRevenu.stream().forEach(rr -> {
            revenueFactorBusiness.disable(rr.getId());
        });

        // aggiorno Stato Campagne
        List<CampaignDTO> listaCampagne = campaignBusiness.getCampaignsToDisable();
        listaCampagne.stream().forEach(campaignDTO -> {
            campaignBusiness.disable(campaignDTO.getId());
        });

    }

}
