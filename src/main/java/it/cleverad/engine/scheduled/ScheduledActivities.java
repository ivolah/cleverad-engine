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

    @Scheduled(cron = "0 0 1 * * ?")
    public void aggiornaStato() {
        log.info("AGGIORNAMENTO QUOTIDIANO STATO");

        // aggiorno Stato Campagne
        List<CampaignDTO> listaCampagne = campaignBusiness.getCampaignsToDisable();
        listaCampagne.stream().forEach(campaignDTO -> {
            CampaignBusiness.Filter filter = new CampaignBusiness.Filter();
            filter.setStatus(false);
            campaignBusiness.update(campaignDTO.getId(), filter);
        });

        // aggiorno Stato Budget
        List<BudgetDTO> listaBudget = budgetBusiness.getBudgetToDisable();
        listaBudget.stream().forEach(campaignDTO -> {
            BudgetBusiness.Filter filter = new BudgetBusiness.Filter();
            filter.setStatus(false);
            budgetBusiness.update(campaignDTO.getId(), filter);
        });

        // aggiorao sato commission
        List<CommissionDTO> listaCommission = commissionBusiness.getCommissionToDisable();
        listaCommission.stream().forEach(campaignDTO -> {
            CommissionBusiness.Filter filter = new CommissionBusiness.Filter();
            filter.setStatus(false);
            commissionBusiness.update(campaignDTO.getId(), filter);
        });

        // aggiorno stato revenue
        List<RevenueFactorDTO> listaRevenu = revenueFactorBusiness.getRevenueToDisable();
        listaCommission.stream().forEach(campaignDTO -> {
            RevenueFactorBusiness.Filter filter = new RevenueFactorBusiness.Filter();
            filter.setStatus(false);
            revenueFactorBusiness.update(campaignDTO.getId(), filter);
        });


    }

}
