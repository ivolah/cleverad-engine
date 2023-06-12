package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.tracking.Cpl;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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

    public CplDTO(long id, String refferal, String ip, String agent, String data, LocalDateTime date, Boolean read, String info) {
        this.id = id;
        this.refferal = refferal;
        this.ip = ip;
        this.agent = agent;
        this.data = data;
        this.date = date;
        this.read = read;
        this.info = info;
    }

    public static CplDTO from(Cpl cpl) {
        return new CplDTO(cpl.getId(), cpl.getRefferal(), cpl.getIp(), cpl.getAgent(), cpl.getData(), cpl.getDate(), cpl.getRead(),  cpl.getInfo());
    }

}
