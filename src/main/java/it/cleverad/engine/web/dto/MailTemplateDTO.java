package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.MailTemplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailTemplateDTO {

    private Long id;
    private String name;
    private String subject;
    private String content;

    private Boolean status;
    private LocalDateTime creationDate;

    public static MailTemplateDTO from(MailTemplate mailTemplate) {
        return new MailTemplateDTO(mailTemplate.getId(), mailTemplate.getName(), mailTemplate.getSubject(), mailTemplate.getContent(), mailTemplate.getStatus(), mailTemplate.getCreationDate());
    }

}
