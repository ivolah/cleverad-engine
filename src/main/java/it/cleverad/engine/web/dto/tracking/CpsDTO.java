package it.cleverad.engine.web.dto.tracking;

import it.cleverad.engine.persistence.model.tracking.Cps;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CpsDTO {

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

    public CpsDTO(long id, String refferal, String ip, String agent, String data, LocalDateTime date, Boolean read, String info, String country, Long mediaId, Long campaignId, Long affiliateId, Long channelId, Long targetId, Boolean blacklisted, Boolean multiple, Long cpcId, String actionId) {
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

    public static CpsDTO from(Cps cps) {
        return new CpsDTO(cps.getId(), cps.getRefferal(), cps.getIp(), cps.getAgent(), cps.getData(), cps.getDate(), cps.getRead(), cps.getInfo(), cps.getCountry(), cps.getMediaId(), cps.getCampaignId(), cps.getAffiliateId(), cps.getChannelId(), cps.getTargetId(), cps.getBlacklisted(), cps.getMultiple(), cps.getCpcId(), cps.getActionId());
    }

}