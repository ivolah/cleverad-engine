package it.cleverad.engine.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetDTO {

    private long id;
    private String target;
    private String cookieTime;
    private String followThorugh;

}
