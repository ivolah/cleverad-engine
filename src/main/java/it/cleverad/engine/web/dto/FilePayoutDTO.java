package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.FilePayout;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilePayoutDTO {

    private Long id;
    private String name;
    private String type;
    private byte[] data;
    private LocalDateTime creationDate;

    private Long dictionaryId;
    private String dictionaryValue;
    private Long payoutId;

    private String path;

    public static FilePayoutDTO from(FilePayout file) {
        return new FilePayoutDTO(file.getId(), file.getName(), file.getType(), null, file.getCreationDate(),
                file.getDictionary().getId(), file.getDictionary().getName(), file.getPayout().getId(), file.getPath());
    }

}
