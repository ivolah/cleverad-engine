package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.MediaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaTypeDTO {

    private long id;
    private String name;

    public static MediaTypeDTO from(MediaType media) {
        return new MediaTypeDTO(media.getId(), media.getName());
    }

}
