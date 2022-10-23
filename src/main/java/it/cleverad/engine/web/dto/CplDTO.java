package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.Cpl;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CplDTO {

    private long id;

    private String cid;
    private String ip;
    private String agent;
    private String data;

    private LocalDateTime date;
    private Boolean read;

    public CplDTO(long id, String cid, String ip, String agent, String data, LocalDateTime date, Boolean read) {
        this.id = id;
        this.cid = cid;
        this.ip = ip;
        this.agent = agent;
        this.data = data;
        this.date = date;
        this.read = read;
    }

    public static CplDTO from(Cpl cpl) {
        return new CplDTO(cpl.getId(), cpl.getCid(), cpl.getIp(),cpl.getAgent(), cpl.getData(), cpl.getDate(), cpl.getRead());
    }

}
