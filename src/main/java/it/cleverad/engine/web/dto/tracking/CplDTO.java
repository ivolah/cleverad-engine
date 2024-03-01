package it.cleverad.engine.web.dto.tracking;

import it.cleverad.engine.persistence.model.tracking.Cpl;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
public class CplDTO {

    private long id;

    private String refferal;
    private String ip;
    private String agent;
    private String data;
    private LocalDateTime date;
    private Boolean read;

    private Long campaignId;
    private String campaignName;
    private Long affiliateId;
    private String affiliateName;
    private Long channelId;
    private String channelName;
    private String info;
    private String country;
    private Long targetId;
    private Long mediaId;

    private Boolean blacklisted;
    private Boolean multiple;

    private Long cpcId;
    private String actionId;

    public CplDTO(long id, String refferal, String ip, String agent, String data, LocalDateTime date, Boolean read, String info, String country, Long mediaId, Long campaignId, Long affiliateId, Long channelId, Long targetId, Boolean blacklisted, Boolean multiple, Long cpcId, String actionId) {
        this.id = id;
        this.refferal = refferal;
        this.ip = ip;
        this.agent = agent;
        this.data = data;
        this.date = date;
        this.read = read;
        this.info = info;
        this.country = country;
        this.mediaId = mediaId;
        this.campaignId = campaignId;
        this.affiliateId = affiliateId;
        this.channelId = channelId;
        this.targetId = targetId;
        this.blacklisted = blacklisted;
        this.multiple = multiple;
        this.cpcId = cpcId;
        this.actionId = actionId;
    }

    public static CplDTO from(Cpl cpl) {
        return new CplDTO(cpl.getId(), cpl.getRefferal(), cpl.getIp(), cpl.getAgent(), cpl.getData(), cpl.getDate(), cpl.getRead(), cpl.getInfo(), cpl.getCountry(), cpl.getMediaId(), cpl.getCampaignId(), cpl.getAffiliateId(), cpl.getChannelId(), cpl.getTargetId(), cpl.getBlacklisted(), cpl.getMultiple(), cpl.getCpcId(), cpl.getActionId());
    }

}