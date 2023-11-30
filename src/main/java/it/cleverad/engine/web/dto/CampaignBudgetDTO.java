package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.CampaignBudget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CampaignBudgetDTO {

    private Long id;
    private LocalDateTime creationDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long campaignId;
    private Long advertiserId;
    private Long plannerId;
    private Long canaleId;
    private Boolean prenotato;
    private Long tipologiaId;
    private Integer capIniziale;
    private Double payout;
    private Double budgetIniziale;
    private Integer capErogato;
    private Double capPc;
    private Double budgetErogato;
    private Double commissioniErogate;
    private Double revenuePC;
    private Double revenue;
    private Double scarto;
    private Double budgetErogatoPS;
    private Double commissioniErogatePS;
    private Double revenuePCPS;
    private Double revenuePS;
    private Double revenueDay;
    private String materiali;
    private String note;
    private Integer capFatturabile;
    private Double fatturato;
    private Long fatturaId;
    private Boolean status;

    public static CampaignBudgetDTO from(CampaignBudget campaignBudget) {
        return new CampaignBudgetDTO(
                campaignBudget.getId(),
                campaignBudget.getCreationDate(), campaignBudget.getStartDate(), campaignBudget.getEndDate(),
                campaignBudget.getCampaign().getId(),
                campaignBudget.getAdvertiser().getId(),
                campaignBudget.getPlanner().getId(),
                campaignBudget.getCanali().getId(),
                campaignBudget.getPrenotato(),
                campaignBudget.getDictionary().getId(),
                campaignBudget.getCapIniziale(),
                campaignBudget.getPayout(),
                campaignBudget.getBudgetIniziale(),
                campaignBudget.getCapErogato(),
                campaignBudget.getCapPc(),
                campaignBudget.getBudgetErogato(),
                campaignBudget.getCommissioniErogate(),
                campaignBudget.getRevenuePC(),
                campaignBudget.getRevenue(),
                campaignBudget.getScarto(),
                campaignBudget.getBudgetErogatoPS(),
                campaignBudget.getCommissioniErogatePS(),
                campaignBudget.getRevenuePCPS(),
                campaignBudget.getRevenuePS(),
                campaignBudget.getRevenueDay(),
                campaignBudget.getMateriali(),
                campaignBudget.getNote(),
                campaignBudget.getCapFatturabile(),
                campaignBudget.getFatturato(),
                campaignBudget.getFatturaId(),
                campaignBudget.getStatus()
        );
    }

}