package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CategoryDTO {

    private long id;

    private String name;
    private String code;
    private String description;

    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    public CategoryDTO(long id, String name, String code, String description, LocalDateTime creationDate, LocalDateTime lastModificationDate) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
    }

    public static CategoryDTO from(Category category) {
        return new CategoryDTO(category.getId(), category.getName(), category.getCode(), category.getDescription(), category.getCreationDate(), category.getLastModificationDate());
    }

}
