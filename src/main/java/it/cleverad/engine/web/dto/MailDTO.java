package it.cleverad.engine.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailDTO {

    private Long templateId;
    private String campaignId;
    private String affiliateId;

}
