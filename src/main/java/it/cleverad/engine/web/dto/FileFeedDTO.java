package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.FileFeed;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FileFeedDTO {

    private Long id;
    private String name;
    private String type;
    private String note;
    private String path;
    private LocalDateTime creationDate;
    private Long advertiserId;
    private String advertiserName;

    public static FileFeedDTO from(FileFeed file) {
        return new FileFeedDTO(
                file.getId(),
                file.getName(),
                file.getType(),
                file.getNote(),
                file.getPath(),
                file.getCreationDate(),
                file.getAdvertiser().getId(),
                file.getAdvertiser().getName()
        );
    }

}