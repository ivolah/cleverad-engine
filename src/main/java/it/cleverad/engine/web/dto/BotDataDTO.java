package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.BotData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotDataDTO {

    private Long id;
    private String cap;
    private String telefono;
    private String ip;
    private LocalDateTime ts;
    private String campaignName;
    private String campaignReferral;
    private String referral;
    private String email;
    private Boolean privacy1;
    private Boolean privacy2;

    public static BotDataDTO from(BotData botData) {
        return new BotDataDTO(
                botData.getId(),
                botData.getCap(),
                botData.getTelefono(),
                botData.getIp(),
                botData.getTs(),
                botData.getCampaignName(),
                botData.getCampaignReferral(),
                botData.getReferral(),
                botData.getEmail(),
                botData.getPrivacy1(),
                botData.getPrivacy2()
        );
    }

}