package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.AffiliateChannelCommissionCampaign;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AffiliateChannelCommissionCampaignDTO {

    private long id;
    private Long campaignId;
    private Long affiliateId;
    private String affilateName;
    private Long channelId;
    private String channelName;
    private Long commissionId;
    private String commissionName;
    private String commissionValue;

    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    public AffiliateChannelCommissionCampaignDTO(long id, Long campaignId, Long affiliateId, String affilateName, Long channelId, String channelName, Long commissionId, String commissionName, String commissionValue, LocalDateTime creationDate, LocalDateTime lastModificationDate) {
        this.id = id;
        this.campaignId = campaignId;
        this.affiliateId = affiliateId;
        this.affilateName = affilateName;
        this.channelId = channelId;
        this.channelName = channelName;
        this.commissionId = commissionId;
        this.commissionName = commissionName;
        this.commissionValue = commissionValue;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
    }

    public static AffiliateChannelCommissionCampaignDTO from(AffiliateChannelCommissionCampaign accc) {
        return new AffiliateChannelCommissionCampaignDTO(accc.getId(), accc.getCampaign().getId(), accc.getAffiliate().getId(), accc.getAffiliate().getName(), accc.getChannel().getId(), accc.getChannel().getName(), accc.getCommission().getId(), accc.getCommission().getName(), accc.getCommission().getValue(), accc.getCreationDate(), accc.getLastModificationDate());
    }

}
