package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.Tracking;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TrackingDTO {

    private long id;
    private String refferalId;
    private String ip;
    private String agent;
    private LocalDateTime creationDate;
    private Boolean read;




    public TrackingDTO(long id, String refferalId, String ip, String agent, LocalDateTime creationDate, Boolean read) {
        this.id = id;
        this.refferalId = refferalId;
        this.ip = ip;
        this.agent = agent;
        this.creationDate = creationDate;
        this.read = read;
    }

    public static TrackingDTO from(Tracking tracking) {
        return new TrackingDTO(tracking.getId(), tracking.getRefferalId(), tracking.getIp(), tracking.getAgent(), tracking.getCreationDate(), tracking.getRead());
    }


}
