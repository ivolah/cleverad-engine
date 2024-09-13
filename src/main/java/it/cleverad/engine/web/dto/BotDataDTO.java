package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.BotData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String projectName;
    private String projectReferral;

    public static BotDataDTO from(BotData botData) {
        return new BotDataDTO(
                botData.getId(),
                botData.getCap(),
                botData.getTelefono(),
                botData.getIp(),
                botData.getTs(),
                botData.getProjectName(),
                botData.getProjectReferral()
        );
    }

}