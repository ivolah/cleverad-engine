package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.MediaCampaign;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MediaCampaignDTO {

    private long id;
    private Long mediaId;
    private Long campaignId;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    public MediaCampaignDTO(long id, Long mediaId, Long campaignId, LocalDateTime creationDate, LocalDateTime lastModificationDate) {
        this.id = id;
        this.mediaId = mediaId;
        this.campaignId = campaignId;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
    }

    public static MediaCampaignDTO from(MediaCampaign media) {
        return new MediaCampaignDTO(media.getId(), media.getMedia().getId(), media.getCampaign().getId(), media.getCreationDate(), media.getLastModificationDate());
    }

}
