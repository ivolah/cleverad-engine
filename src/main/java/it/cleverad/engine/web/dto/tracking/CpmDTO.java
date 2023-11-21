package it.cleverad.engine.web.dto.tracking;

import it.cleverad.engine.persistence.model.tracking.Cpm;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CpmDTO {

    private long id;

    private Long imageId;
    private Long mediaId;
    private String refferal;
    private String ip;
    private String agent;
    private LocalDateTime date;
    private Boolean read;
    private Long campaignId;
    private String campaignName;
    private Long affiliateId;
    private String affiliateName;
    private Long channelId;
    private String channelName;

    public CpmDTO(long id, Long imageId, Long mediaId, String refferal, String ip, String agent, LocalDateTime date, Boolean read) {
        this.id = id;
        this.imageId = imageId;
        this.mediaId = mediaId;
        this.refferal = refferal;
        this.ip = ip;
        this.agent = agent;
        this.date = date;
        this.read = read;
    }

    public static CpmDTO from(Cpm cpm) {
        return new CpmDTO(cpm.getId(), cpm.getImageId(), cpm.getMediaId(), cpm.getRefferal(), cpm.getIp(), cpm.getAgent(), cpm.getDate(), cpm.getRead());
    }

}