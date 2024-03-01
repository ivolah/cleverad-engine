package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.FileCost;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileCostDTO {

    private Long id;
    private String name;
    private LocalDateTime creationDate;
    private String path;
    private String type;

    public static FileCostDTO from(FileCost file) {
        return new FileCostDTO(file.getId(), file.getName(), file.getCreationDate(), file.getPath(), file.getType());
    }

}