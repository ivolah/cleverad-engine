package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.Cpc;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CpcDTO {

    private long id;
    private String refferal;
    private LocalDateTime date;
    private Boolean status;

    public CpcDTO(long id, String refferal, LocalDateTime date, Boolean status) {
        this.id = id;
        this.refferal = refferal;
        this.date = date;
        this.status = status;
    }

    public static CpcDTO from(Cpc cpc) {
        return new CpcDTO(cpc.getId(), cpc.getRefferal(), cpc.getDate(), cpc.getStatus());
    }

}
