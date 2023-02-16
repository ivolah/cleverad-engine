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
    private byte[] data;
    private LocalDateTime creationDate;
    private String note;
    private Long dictionaryId;
    private String dictionaryValue;
    private Long affiliateId;
    private String affiliateName;


    public static FileAffiliateDTO from(FileAffiliate file) {
        return new FileAffiliateDTO(file.getId(), file.getName(), file.getType(), file.getData(), file.getCreationDate(), file.getNote(),
                file.getDictionary().getId(), file.getDictionary().getName(), file.getAffiliate().getId(), file.getAffiliate().getName());
    }

}
