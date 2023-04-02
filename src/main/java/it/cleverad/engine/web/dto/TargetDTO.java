package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Target;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetDTO {

    private Long id;
    private String target;
    private String cookieTime;
    private String followThorugh;
    private Long mediaId;

    public TargetDTO(Long id, String target, Long mediaId) {
        this.id = id;
        this.target = target;
        this.mediaId = mediaId;
    }

    public static TargetDTO from(Target target) {
        return new TargetDTO(
                target.getId(),
                target.getTarget(),
                target.getMedia().getId());
    }
}
