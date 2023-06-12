package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.File;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO {

    private Long id;
    private String name;
    private String type;
    private byte[] data;
    private LocalDateTime creationDate;
    private String nomeCodificato;
    private String path;

    public static FileDTO from(File file) {
        return new FileDTO(file.getId(), file.getName(), file.getType(), file.getData(), file.getCreationDate(), "", file.getPath());
    }

}
