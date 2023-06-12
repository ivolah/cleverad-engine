package it.cleverad.engine.web.dto;

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

    public CpsDTO(long id, String refferal, String ip, String agent, String data, LocalDateTime date, Boolean read) {
        this.id = id;
        this.refferal = refferal;
        this.ip = ip;
        this.agent = agent;
        this.data = data;
        this.date = date;
        this.read = read;
    }

    public static CpsDTO from(Cps cps) {
        return new CpsDTO(cps.getId(), cps.getRefferal(), cps.getIp(), cps.getAgent(), cps.getData(), cps.getDate(), cps.getRead());
    }

}
