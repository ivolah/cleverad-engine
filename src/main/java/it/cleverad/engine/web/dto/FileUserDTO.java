package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.FileUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUserDTO {

    private Long id;
    private String name;
    private String type;
    private byte[] data;
    private LocalDateTime creationDate;
    private String note;

    private Long userId;
    private Boolean avatar;

    public static FileUserDTO from(FileUser file) {
        return new FileUserDTO(file.getId(), file.getName(), file.getType(), file.getData(), file.getCreationDate(), file.getNote(), file.getUserFiles().getId(), file.getAvatar());
    }

}
