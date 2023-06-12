package it.cleverad.engine.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicAgentDTO {

    private Long count;

    private String DeviceName;

    private String OperatingSystemName;
    private String OperatingSystemVersion;

    private String AgentName;
    private String AgentVersion;

}
