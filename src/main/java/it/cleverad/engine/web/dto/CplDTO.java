package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.Cpl;
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

    private LocalDateTime date;
    private Boolean status;

    public CplDTO(long id, String refferal, String ip, String agent, LocalDateTime date, Boolean status) {
        this.id = id;
        this.refferal = refferal;
        this.ip = ip;
        this.agent = agent;
        this.date = date;
        this.status = status;
    }

    public static CplDTO from(Cpl cpl) {
        return new CplDTO(cpl.getId(), cpl.getRefferal(), cpl.getIp(),cpl.getAgent(), cpl.getDate(), cpl.getRead());
    }

}
