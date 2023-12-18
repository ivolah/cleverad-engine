package it.cleverad.engine.scheduled;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.business.CampaignBudgetBusiness;
import it.cleverad.engine.business.RevenueFactorBusiness;
import it.cleverad.engine.business.TransactionCPCBusiness;
import it.cleverad.engine.business.TransactionCPLBusiness;
import it.cleverad.engine.persistence.model.service.TransactionCPC;
import it.cleverad.engine.persistence.model.service.TransactionCPL;
import it.cleverad.engine.web.dto.CampaignBudgetDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ConsolidaCampaignBudget {

    @Autowired
    private CampaignBudgetBusiness campaignBudgetBusiness;
    @Autowired
    private TransactionCPLBusiness transactionCPLBusiness;
    @Autowired
    private TransactionCPCBusiness transactionCPCBusiness;
    @Autowired
    private RevenueFactorBusiness revenueFactorBusiness;
    @Autowired
    private Mapper mapper;

    /**
     * Consolido i numeri e le percentuali dei Budget Campagne
     */
    @Scheduled(cron = "1 1 0/1 * * ?")
    public void ciclaCampaignBudget() {
        this.gestisciCampaignBudget();
    }//ciclaCampaignBudget

    private void gestisciCampaignBudget() {
        // listo i budget attivi
        List<CampaignBudgetDTO> budgets = campaignBudgetBusiness.searchAttivi().stream().collect(Collectors.toList());

        budgets.forEach(dto -> {
            log.trace("GESTISCO {} \n\n", dto);

            Integer capErogato = 0;
            double budgetErogato = 0D;
            double commissioniErogate = 0D;
            double revenue = 0D;

            // TODO in futuro aggiungere proprietà per gestire solo quelle non precedentemente lette per consolida budget
            // TODO , usare come date da a quelle del budget
            // CPL
            if (dto.getCanaleId().equals(11L)) {
                // CPL -- trovo tutte le transazioni
                List<TransactionCPL> cpls = transactionCPLBusiness.searchByCampaignMese(dto.getCampaignId());
                for (TransactionCPL cpl : cpls) {
                    capErogato += 1;
                    budgetErogato += cpl.getValue();
                    commissioniErogate += cpl.getCommission().getValue();
                    revenue += revenueFactorBusiness.findById(cpl.getRevenueId()).getRevenue();
                }
            }
            // CPC
            else if (dto.getCanaleId().equals(10L)) {
                // CPL -- trovo tutte le transazioni
                List<TransactionCPC> cpcs = transactionCPCBusiness.searchByCampaignMese(dto.getCampaignId());
                for (TransactionCPC cpc : cpcs) {
                    capErogato += 1;
                    budgetErogato += cpc.getValue();
                    commissioniErogate += cpc.getCommission().getValue();
                    revenue += revenueFactorBusiness.findById(cpc.getRevenueId()).getRevenue();
                }
            }

            CampaignBudgetBusiness.BaseCreateRequest request = new CampaignBudgetBusiness.BaseCreateRequest();
            request.setCapErogato(capErogato);
            request.setCapPc(((double) (capErogato * 100) / dto.getCapIniziale()));
            request.setBudgetErogato(budgetErogato);
            request.setCommissioniErogate(commissioniErogate);
            request.setRevenuePC(1 - (commissioniErogate / budgetErogato));
            request.setRevenue(revenue);
            request.setBudgetErogatoPS(budgetErogato - (budgetErogato / 100 * dto.getScarto()));
            request.setCommissioniErogatePS(commissioniErogate - (commissioniErogate / 100 * dto.getScarto()));
            request.setRevenuePS(revenue - (revenue / 100 * dto.getScarto()));
            request.setRevenuePCPS(1 - (request.getCommissioniErogatePS() / request.getBudgetErogatoPS()));
            request.setRevenueDay(revenue / LocalDate.now().getDayOfMonth());
            mapper.map(dto, request);
            request.setId(null);
            request.setStatus(true);
            CampaignBudgetDTO cb = campaignBudgetBusiness.create(request);
            log.info("CREATO :: {}", cb.getId());

            // cancello se la data di creazione e oggi
            // in questo modo mantendo solo l'utimo budget del giorno precedente
            if (dto.getCreationDate().getDayOfYear() < LocalDateTime.now().getDayOfYear()) {
                // lo setto a non attivo
                campaignBudgetBusiness.disable(dto.getId());
                log.info("Disabilito :: {}", dto.getId());
            } else {
                // cancello
                campaignBudgetBusiness.delete(dto.getId());
                log.trace("ELIMINO {}", dto.getId());
            }

        });

        log.trace("END");
    }

}