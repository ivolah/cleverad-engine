package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Budget;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
public class BudgetDTO {

    private Long id;

    private Long budget;
    private Date dueDate;

    private Long affiliateId;
    private String affiliateName;
    private Long campaignId;
    private String campaignName;

    private Boolean status;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    public static BudgetDTO from(Budget budget) {
        return new BudgetDTO(budget.getId(), budget.getBudget(), budget.getDueDate(),
                budget.getAffiliate() != null ? budget.getAffiliate().getId() : null,
                budget.getAffiliate() != null ? budget.getAffiliate().getName() : null,
                budget.getCampaign() != null ? budget.getCampaign().getId() : null,
                budget.getCampaign() != null ? budget.getCampaign().getName() : null,
              budget.getStatus(), budget.getCreationDate(), budget.getLastModificationDate());
    }

    public BudgetDTO(Long id, Long budget, Date dueDate, Long affiliateId, String affiliateName, Long campaignId, String campaignName, Boolean status, LocalDateTime creationDate, LocalDateTime lastModificationDate) {
        this.id = id;
        this.budget = budget;
        this.dueDate = dueDate;
        this.affiliateId = affiliateId;
        this.affiliateName = affiliateName;
        this.campaignId = campaignId;
        this.campaignName = campaignName;
        this.status = status;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
    }
}
