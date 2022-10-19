package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.MediaType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor
public class MediaTypeDTO {

    private long id;
    private String name;
    private String description;
    private Boolean status;

    public MediaTypeDTO(long id, String name, String description, Boolean status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public static MediaTypeDTO from(MediaType media) {
        return new MediaTypeDTO(media.getId(), media.getName(), media.getDescription(), media.getStatus());
    }

}
