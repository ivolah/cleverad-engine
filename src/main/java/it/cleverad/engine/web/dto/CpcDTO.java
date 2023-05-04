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

    public CpcDTO(long id, String refferal, String ip, String agent, LocalDateTime date, Boolean read) {
        this.id = id;
        this.refferal = refferal;
        this.ip = ip;
        this.agent = agent;
        this.date = date;
        this.read = read;
    }

    public static CpcDTO from(Cpc cpc) {
        return new CpcDTO(cpc.getId(), cpc.getRefferal(),cpc.getIp(), cpc.getAgent(), cpc.getDate(), cpc.getRead());
    }

}
