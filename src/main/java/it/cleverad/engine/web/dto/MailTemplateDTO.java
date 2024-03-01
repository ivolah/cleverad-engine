package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.MailTemplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    public static MailTemplateDTO from(MailTemplate mailTemplate) {
        return new MailTemplateDTO(mailTemplate.getId(), mailTemplate.getName(), mailTemplate.getSubject(), mailTemplate.getContent(), mailTemplate.getStatus());
    }

}