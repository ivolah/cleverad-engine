package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.AffiliateBudget;
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
public class AffiliateBudgetDTO {

    private Long id;

    private Double budget;
    private Double initialBudget;

    private LocalDate startDate;
    private LocalDate dueDate;

    private Long affiliateId;
    private String affiliateName;
    private Long campaignId;
    private String campaignName;

    private Boolean status;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    private Integer cap;
    private Integer initialCap;
    public AffiliateBudgetDTO(Long id, Double budget, Double initialBudget, LocalDate startDate, LocalDate dueDate, Long affiliateId,
                              String affiliateName, Long campaignId, String campaignName, Boolean status, Integer cap, Integer initialCap) {
        this.id = id;
        this.budget = budget;
        this.initialBudget = initialBudget;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.affiliateId = affiliateId;
        this.affiliateName = affiliateName;
        this.campaignId = campaignId;
        this.campaignName = campaignName;
        this.status = status;
        this.cap  =cap;
        this.initialCap =initialCap;
    }

    public static AffiliateBudgetDTO from(AffiliateBudget affiliateBudget) {
        return new AffiliateBudgetDTO(
                affiliateBudget.getId(),
                affiliateBudget.getBudget(),
                affiliateBudget.getInitialBudget(),
                affiliateBudget.getStartDate(),
                affiliateBudget.getDueDate(),
                affiliateBudget.getAffiliate() != null ? affiliateBudget.getAffiliate().getId() : null,
                affiliateBudget.getAffiliate() != null ? affiliateBudget.getAffiliate().getName() : null,
                affiliateBudget.getCampaign() != null ? affiliateBudget.getCampaign().getId() : null,
                affiliateBudget.getCampaign() != null ? affiliateBudget.getCampaign().getName() : null,
                affiliateBudget.getStatus(), affiliateBudget.getCap(), affiliateBudget.getInitialCap()
        );
    }

}