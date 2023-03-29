package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Budget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDTO {

    private Long id;

    private Double budget;
    private LocalDate startDate;
    private LocalDate dueDate;

    private Long affiliateId;
    private String affiliateName;
    private Long campaignId;
    private String campaignName;

    private Boolean status;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    public static BudgetDTO from(Budget budget) {
        return new BudgetDTO(budget.getId(), budget.getBudget(), budget.getStartDate(), budget.getDueDate(), budget.getAffiliate() != null ? budget.getAffiliate().getId() : null, budget.getAffiliate() != null ? budget.getAffiliate().getName() : null, budget.getCampaign() != null ? budget.getCampaign().getId() : null, budget.getCampaign() != null ? budget.getCampaign().getName() : null, budget.getStatus(), budget.getCreationDate(), budget.getLastModificationDate());
    }

}
