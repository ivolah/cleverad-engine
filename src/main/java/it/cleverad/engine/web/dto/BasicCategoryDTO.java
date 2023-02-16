package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BasicCategoryDTO {

    private long id;

    private String name;
    private String code;
    private String description;

    public BasicCategoryDTO(long id, String name, String code, String description) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
    }

    public static BasicCategoryDTO from(Category category) {
        return new BasicCategoryDTO(category.getId(), category.getName(), category.getCode(), category.getDescription());
    }

}
