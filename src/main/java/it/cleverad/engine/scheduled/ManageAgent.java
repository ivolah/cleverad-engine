package it.cleverad.engine.scheduled;//package it.cleverad.engine.scheduled;

import it.cleverad.engine.business.*;
import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.service.ReferralService;
import lombok.extern.slf4j.Slf4j;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ManageAgent {

    @Autowired
    private AgentBusiness agentBusiness;
    @Autowired
    private ReferralService referralService;
    @Autowired
    private CpcBusiness cpcBusiness;
    @Autowired
    private CpmBusiness cpmBusiness;
    @Autowired
    private CplBusiness cplBusiness;
    @Autowired
    private CpsBusiness cpsBusiness;

    @Scheduled(cron = "0 0 4 * * ?")
    public void aggiornaStato() {
        log.info("AGGIORNAMENTO QUOTIDIANO -- AGENT");
        UserAgentAnalyzer uaa = UserAgentAnalyzer.newBuilder().hideMatcherLoadStats().withCache(10000).build();

        // inserisco info Agent CPC
        cpcBusiness.getAllDayBefore().stream().filter(dto -> dto.getRefferal() != null).forEach(dto -> {
            // valorizzo agent
            if (StringUtils.isNotBlank(dto.getAgent()))
                this.generaAgent(uaa.parse(dto.getAgent()), dto.getRefferal(), "CPC");
        });

        // inserisco info Agent CPM
        cpmBusiness.getAllDayBefore().stream().filter(dto -> dto.getRefferal() != null).forEach(dto -> {
            // valorizzo agent
            if (StringUtils.isNotBlank(dto.getAgent()))
                this.generaAgent(uaa.parse(dto.getAgent()), dto.getRefferal(), "CPM");
        });

        // inserisco info Agent CPL
        cplBusiness.getAllDayBefore().stream().filter(dto -> dto.getRefferal() != null).forEach(dto -> {
            // valorizzo agent
            if (StringUtils.isNotBlank(dto.getAgent()))
                this.generaAgent(uaa.parse(dto.getAgent()), dto.getRefferal(), "CPL");
        });

        // inserisco info Agent CPS
        cpsBusiness.getAllDayBefore().stream().filter(dto -> dto.getRefferal() != null).forEach(dto -> {
            // valorizzo agent
            if (StringUtils.isNotBlank(dto.getAgent()))
                this.generaAgent(uaa.parse(dto.getAgent()), dto.getRefferal(), "CPS");
        });

    }


    private void generaAgent(UserAgent a, String refferal, String tipo) {
        AgentBusiness.BaseCreateRequest request = new AgentBusiness.BaseCreateRequest();
        request.setTipo(tipo);

        request.setAgentClass(a.get("AgentClass").getValue());
        request.setAgentVersion(a.get("AgentVersion").getValue());
        request.setAgentName(a.get("AgentName").getValue());
        request.setDeviceBrand(a.get("DeviceBrand").getValue());
        request.setDeviceCpu(a.get("DeviceCpu").getValue());
        request.setDeviceCpuBits(a.get("DeviceCpuBits").getValue());
        request.setDeviceVersion(a.get("DeviceVersion").getValue());
        request.setDeviceName(a.get("DeviceName").getValue());
        request.setLayoutEngineClass(a.get("LayoutEngineClass").getValue());
        request.setLayoutEngineVersion(a.get("LayoutEngineVersion").getValue());
        request.setLayoutEngineName(a.get("LayoutEngineName").getValue());
        request.setOperatingSystemVersion(a.get("OperatingSystemVersion").getValue());
        request.setOperatingSystemClass(a.get("OperatingSystemClass").getValue());
        request.setOperatingSystemName(a.get("OperatingSystemName").getValue());

        if (StringUtils.isNotBlank(refferal) && !refferal.contains("{{refferalId}}")) {
            Refferal reff = referralService.decodificaReferral(refferal);
            request.setCampaignId(reff.getCampaignId().toString());
            if (refferal.length() > 5) request.setAffiliateId(reff.getAffiliateId().toString());
        }
        agentBusiness.create(request);
    }


}
