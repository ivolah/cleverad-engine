package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.AffiliateChannelCommissionCampaign;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    private String commissionType;
    private LocalDate commissionDueDate;
    
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;
    
    public AffiliateChannelCommissionCampaignDTO(long id, Long campaignId, Long affiliateId, String affilateName, Long channelId, String channelName, Long commissionId, String commissionName, String commissionValue, String commissionType, LocalDate commissionDate, LocalDateTime creationDate, LocalDateTime lastModificationDate) {
        this.id                   = id;
        this.campaignId           = campaignId;
        this.affiliateId          = affiliateId;
        this.affilateName         = affilateName;
        this.channelId            = channelId;
        this.channelName          = channelName;
        this.commissionId         = commissionId;
        this.commissionName       = commissionName;
        this.commissionValue      = commissionValue;
        this.commissionType       = commissionType;
        this.commissionDueDate    = commissionDate;
        this.creationDate         = creationDate;
        this.lastModificationDate = lastModificationDate;
    }
    
    public static AffiliateChannelCommissionCampaignDTO from(AffiliateChannelCommissionCampaign accc) {
        return new AffiliateChannelCommissionCampaignDTO(accc.getId(),
                                                         accc.getCampaign()!= null ? accc.getCampaign().getId() : null,
                                                         accc.getAffiliate() != null ? accc.getAffiliate().getId() : null,
                                                         accc.getAffiliate() != null ? accc.getAffiliate().getName() : null,
                                                         accc.getChannel()  != null ? accc.getChannel().getId() : null,
                                                         accc.getChannel()  != null ? accc.getChannel().getName() : null,
                                                         accc.getCommission()  != null ? accc.getCommission().getId() : null,
                                                         accc.getCommission()   != null ? accc.getCommission().getName() : null,
                                                         accc.getCommission()  != null ? accc.getCommission().getValue() : null,
                                                         accc.getCommission().getDictionary() != null ? accc.getCommission().getDictionary().getName() : null,
                                                         accc.getCommission() != null ? accc.getCommission().getDueDate() : null,
                                                         accc.getCreationDate(), accc.getLastModificationDate());
    }
    




}
