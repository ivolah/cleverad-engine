package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.CampaignAffiliate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CampaignAffiliateDTO {

    private Long id;
    private Long affiliateId;
    private String affiliateName;
    private Long campaignId;
    private String campaignName;
    private String followThrough;
    private Long statusId;
    private String statusName;
    private Boolean brandbuddies;

    public static CampaignAffiliateDTO from(CampaignAffiliate campaignAffiliate) {
        return new CampaignAffiliateDTO(
                campaignAffiliate.getId(),
                campaignAffiliate.getAffiliate().getId(), campaignAffiliate.getAffiliate().getName(),
                campaignAffiliate.getCampaign().getId(), campaignAffiliate.getCampaign().getName(),
                campaignAffiliate.getFollowThrough(),
                null, null, campaignAffiliate.getBrandbuddies()
                // campaignAffiliate.getDictionaryStatusCampaignAffiliate().getId(), campaignAffiliate.getDictionaryStatusCampaignAffiliate().getName()
        );
    }

}