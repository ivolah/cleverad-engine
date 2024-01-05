package it.cleverad.engine.scheduled.consolida;

import it.cleverad.engine.service.CampaignBudgetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConsolidaCampaignBudget {

    @Autowired
    private CampaignBudgetService campaignBudgetService;

    /**
     * Consolido i numeri e le percentuali dei Budget Campagne
     */
    @Scheduled(cron = "1 1 0/1 * * ?")
    //   @Scheduled(cron = "0 0/1 * * * ?")
    public void ciclaCampaignBudget() {
        campaignBudgetService.gestisciCampaignBudget(null);
    }//ciclaCampaignBudget

}