package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.AffiliateBudgetCampaign;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AffiliateBudgetCampaignDTO {

    private long id;
    private Long campaignId;
    private Long affiliateId;
    private Long budgetId;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    public AffiliateBudgetCampaignDTO(long id, Long campaignId, Long affiliateId, Long budgetId, LocalDateTime creationDate, LocalDateTime lastModificationDate) {
        this.id = id;
        this.campaignId = campaignId;
        this.affiliateId = affiliateId;
        this.budgetId = budgetId;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
    }

    public static AffiliateBudgetCampaignDTO from(AffiliateBudgetCampaign affiliate) {
        return new AffiliateBudgetCampaignDTO(affiliate.getId(), affiliate.getCampaign().getId(), affiliate.getAffiliate().getId(), affiliate.getBudget().getId(), affiliate.getCreationDate(), affiliate.getLastModificationDate());
    }

}
