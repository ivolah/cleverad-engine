package it.cleverad.engine.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TargetDTO {

    private long id;
    private String target;
    private String cookieTime;

    public TargetDTO(long id, String target, String cookieTime) {
        this.id = id;
        this.target = target;
        this.cookieTime = cookieTime;
    }

}
