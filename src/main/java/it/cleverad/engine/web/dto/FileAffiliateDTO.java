package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.FileAffiliate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileAffiliateDTO {

    private Long id;
    private String name;
    private String type;
    private LocalDateTime creationDate;
    private Long dictionaryId;
    private String dictionaryValue;
    private Long affiliateId;
    private String affiliateName;
    private String path;

    public static FileAffiliateDTO from(FileAffiliate file) {
        return new FileAffiliateDTO(file.getId(), file.getName(), file.getType(), file.getCreationDate(), file.getDictionary().getId(), file.getDictionary().getName(), file.getAffiliate().getId(), file.getAffiliate().getName(), file.getPath());
    }

}