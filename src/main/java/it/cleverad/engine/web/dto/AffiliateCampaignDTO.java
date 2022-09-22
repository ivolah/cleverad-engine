package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.AffiliateChannelCommissionCampaign;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AffiliateCampaignDTO {

    private long id;
    private Long campaignId;
    private Long affiliateId;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    public AffiliateCampaignDTO(long id, Long campaignId, Long affiliateId, LocalDateTime creationDate, LocalDateTime lastModificationDate) {
        this.id = id;
        this.campaignId = campaignId;
        this.affiliateId = affiliateId;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
    }

    public static AffiliateCampaignDTO from(AffiliateChannelCommissionCampaign affiliate) {
        return new AffiliateCampaignDTO(affiliate.getId(), affiliate.getCampaign().getId(), null, affiliate.getCreationDate(), affiliate.getLastModificationDate());
    }

}
