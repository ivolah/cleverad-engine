package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.CampaignAffiliateRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignAffiliateRequestDTO {

    private Long id;
    private Long affiliateId;
    private String affiliateName;
    private Long campaignId;
    private String campaignName;
    private Long statusId;
    private String statusName;
    private LocalDateTime requestDate;

    public static CampaignAffiliateRequestDTO from(CampaignAffiliateRequest campaignAffiliateRequest) {
        return new CampaignAffiliateRequestDTO(
                campaignAffiliateRequest.getId(),
                campaignAffiliateRequest.getAffiliate().getId(), campaignAffiliateRequest.getAffiliate().getName(),
                campaignAffiliateRequest.getCampaign().getId(), campaignAffiliateRequest.getCampaign().getName(),
                campaignAffiliateRequest.getDictionaryStatusCampaignAffiliateRequest().getId(), campaignAffiliateRequest.getDictionaryStatusCampaignAffiliateRequest().getName(),
                campaignAffiliateRequest.getRequestDate()
        );
    }

}
