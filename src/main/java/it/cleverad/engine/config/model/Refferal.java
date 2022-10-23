package it.cleverad.engine.config.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Refferal {

    private Long mediaId;
    private Long campaignId;
    private Long affiliateId;
    private Long channelId;

    @Override
    public String toString() {
        return "Refferal{" + "mediaId=" + mediaId + ", campaignId=" + campaignId + ", affiliateId=" + affiliateId + ", channelId=" + channelId + '}';
    }
}
