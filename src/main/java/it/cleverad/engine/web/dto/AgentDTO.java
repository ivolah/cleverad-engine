package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Agent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentDTO {

    private long id;

    private String tipo;
    private String campaignId;
    private String affiliateId;

    private String DeviceName;
    private String DeviceBrand;
    private String DeviceCpu;
    private String DeviceCpuBits;
    private String DeviceVersion;

    private String OperatingSystemClass;
    private String OperatingSystemName;
    private String OperatingSystemVersion;

    private String LayoutEngineClass;
    private String LayoutEngineName;
    private String LayoutEngineVersion;

    private String AgentClass;
    private String AgentName;
    private String AgentVersion;

    public static AgentDTO from(Agent a) {
        return new AgentDTO(a.getId(), a.getTipo(),a.getCampaignId(),a.getAffiliateId(),
                a.getDeviceName(), a.getDeviceBrand(), a.getDeviceCpu(), a.getDeviceCpuBits(), a.getDeviceVersion(),
                a.getOperatingSystemClass(), a.getOperatingSystemName(), a.getOperatingSystemVersion(),
                a.getLayoutEngineClass(), a.getLayoutEngineName(), a.getLayoutEngineVersion(),
                a.getAgentClass(), a.getAgentName(), a.getAgentVersion());
    }

}
