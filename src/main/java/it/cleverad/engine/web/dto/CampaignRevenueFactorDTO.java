package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.CampaignRevenueFactor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CampaignRevenueFactorDTO {

    private Long id;
    private Long revenuefactorId;
    private Long campaignId;

    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    public CampaignRevenueFactorDTO(Long id, Long revenuefactorId, Long campaignId, LocalDateTime creationDate, LocalDateTime lastModificationDate) {
        this.id = id;
        this.revenuefactorId = revenuefactorId;
        this.campaignId = campaignId;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
    }

    public static CampaignRevenueFactorDTO from(CampaignRevenueFactor revenueFactor) {
        return new CampaignRevenueFactorDTO(revenueFactor.getId(), revenueFactor.getRevenuefactor().getId(), revenueFactor.getCampaign().getId(), revenueFactor.getCreationDate(), revenueFactor.getLastModificationDate());
    }

}
