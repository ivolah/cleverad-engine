package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.File;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class FileDTO {

    private Long id;
    private String name;
    private String type;
    private byte[] data;
    private LocalDateTime creationDate;

    public FileDTO(Long id, String name, String type, byte[] data, LocalDateTime creationDate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.data = data;
        this.creationDate = creationDate;
    }

    public static FileDTO from(File file) {
        return new FileDTO(file.getId(), file.getName(), file.getType(), file.getData(), file.getCreationDate());
    }

}
