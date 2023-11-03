package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.BBLink;
import it.cleverad.engine.persistence.model.service.BBPlatform;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BBLinkDTO {

    private Long id;
    private String link;
    private String generated;
    private String referral;
    private Long brandbuddiesId;
    private String brandbuddiesName;
    private Long campaignId;
    private String campaignName;

    public static BBLinkDTO from(BBLink link) {
        return new BBLinkDTO(link.getId(), link.getLink(), link.getGenerated(), link.getReferral(),
                link.getAffiliate() != null ? link.getAffiliate().getId() : null,
                link.getAffiliate() != null ? link.getAffiliate().getName() + " " + link.getAffiliate().getLastName() : null,
                link.getCampaign() != null ? link.getCampaign().getId() : null,
                link.getCampaign() != null ? link.getCampaign().getName() : null
        );
    }

}