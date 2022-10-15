package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.Cpm;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CpmDTO {

    private long id;
    private Long campaignId;
    private Long imageId;
    private Long mediaId;
    private LocalDateTime creationDate;
    private Boolean read;

    public CpmDTO(long id, Long campaignId, Long imageId, Long mediaId, LocalDateTime creationDate, Boolean read) {
        this.id = id;
        this.campaignId = campaignId;
        this.imageId = imageId;
        this.mediaId = mediaId;
        this.creationDate = creationDate;
        this.read = read;
    }

    public static CpmDTO from(Cpm cpm) {
        return new CpmDTO(cpm.getId(), cpm.getCampaignId(), cpm.getImageId(), cpm.getMediaId(), cpm.getTimeStamp(), cpm.getRead());
    }

}
