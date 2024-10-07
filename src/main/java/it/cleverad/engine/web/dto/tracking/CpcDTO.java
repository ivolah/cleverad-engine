package it.cleverad.engine.web.dto.tracking;

import it.cleverad.engine.persistence.model.tracking.Cpc;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
public class CpcDTO {

    private long id;
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
    private String htmlReferral;
    private String info;
    private String country;
    private Long mediaId;
    private Long targetId;
    private Boolean blacklisted;
    private Long rndTrs;
    private String transactionId;

    public CpcDTO(Long id, String refferal, String ip, String agent, LocalDateTime date, Boolean read, String htmlReferral, String info, String country, Long mediaId, Long campaignId, Long affiliateId, Long channelId, Long targetId, Boolean blacklisted, Long rndTrs, String transactionId) {
        this.id = id;
        this.refferal = refferal;
        this.ip = ip;
        this.agent = agent;
        this.date = date;
        this.read = read;
        this.htmlReferral = htmlReferral;
        this.info = info;
        this.country = country;
        this.mediaId = mediaId;
        this.campaignId = campaignId;
        this.affiliateId = affiliateId;
        this.channelId = channelId;
        this.targetId = targetId;
        this.blacklisted = blacklisted;
        this.rndTrs = rndTrs;
        this.transactionId = transactionId;
    }

    public static CpcDTO from(Cpc cpc) {
        return new CpcDTO(cpc.getId(), cpc.getRefferal(), cpc.getIp(), cpc.getAgent(), cpc.getDate(), cpc.getRead(), cpc.getHtmlReferral(), cpc.getInfo(), cpc.getCountry(),
                cpc.getMediaId(), cpc.getCampaignId(), cpc.getAffiliateId(), cpc.getChannelId(), cpc.getTargetId(), cpc.getBlacklisted(), cpc.getRndTrs(), cpc.getTransactionId());
    }
}