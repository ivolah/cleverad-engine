package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.CampaignBudget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CampaignBudgetDTO {

    private Long id;
    private Long advertiserId;
    private String advertiserName;
    private Long campaignId;
    private String campaignName;
    private Integer capIniziale;
    private Integer capErogato;
    private Integer capFatturabile;

    private Double budgetIniziale;
    private Double budgetErogato;

    private Long fatturaId;

    private Double fatturato;
    private Double scarto;

    private String materiali;
    private String note;

    private LocalDateTime creationDate;
    private LocalDate startDate;
    private LocalDate endDate;

    public static CampaignBudgetDTO from(CampaignBudget campaignBudget) {
        return new CampaignBudgetDTO(
                campaignBudget.getId(),
                campaignBudget.getAdvertiser().getId(), campaignBudget.getAdvertiser().getName(),
                campaignBudget.getCampaign().getId(), campaignBudget.getCampaign().getName(),
                campaignBudget.getCapIniziale(), campaignBudget.getCapErogato(), campaignBudget.getCapFatturabile(),
                campaignBudget.getBudgetIniziale(), campaignBudget.getBudgetErogato(),
                campaignBudget.getFatturaId(),
                campaignBudget.getFatturato(), campaignBudget.getScarto(), campaignBudget.getMateriali(), campaignBudget.getNote(),
                campaignBudget.getCreationDate(), campaignBudget.getStartDate(), campaignBudget.getEndDate()
        );
    }

}
