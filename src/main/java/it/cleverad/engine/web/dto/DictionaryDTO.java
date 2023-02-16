package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Dictionary;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor
public class DictionaryDTO {

    private Long id;
    private String name;
    private String description;
    private String type;
    private boolean status;

    public DictionaryDTO(Long id, String name, String description, String type, boolean status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.status = status;
    }


    public static DictionaryDTO from(Dictionary dictionary) {
        return new DictionaryDTO(dictionary.getId(), dictionary.getName(), dictionary.getDescription(), dictionary.getType(), dictionary.isStatus());
    }
}
