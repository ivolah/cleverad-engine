package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.tracking.Cpc;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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

    public CpcDTO(long id, String refferal, String ip, String agent, LocalDateTime date, Boolean read, String htmlReferral, String info, String country) {
        this.id = id;
        this.refferal = refferal;
        this.ip = ip;
        this.agent = agent;
        this.date = date;
        this.read = read;
        this.htmlReferral = htmlReferral;
        this.info = info;
        this.country = country;
    }

    public static CpcDTO from(Cpc cpc) {
        return new CpcDTO(cpc.getId(), cpc.getRefferal(), cpc.getIp(), cpc.getAgent(), cpc.getDate(), cpc.getRead(), cpc.getHtmlReferral(), cpc.getInfo(), cpc.getCountry());
    }
}
