package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "t_agent")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Agent {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;
    @Column(name = "campaign_id")
    private String campaignId;
    @Column(name = "affiliate_id")
    private String affiliateId;

    @Column(name = "device_name")
    private String DeviceName;
    @Column(name = "device_brand")
    private String DeviceBrand;
    @Column(name = "device_cpu")
    private String DeviceCpu;
    @Column(name = "device_cpu_bits")
    private String DeviceCpuBits;
    @Column(name = "device_version")
    private String DeviceVersion;

    @Column(name = "os_class")
    private String OperatingSystemClass;
    @Column(name = "os_name")
    private String OperatingSystemName;
    @Column(name = "os_version")
    private String OperatingSystemVersion;

    @Column(name = "le_class")
    private String LayoutEngineClass;
    @Column(name = "le_name")
    private String LayoutEngineName;
    @Column(name = "le_version")
    private String LayoutEngineVersion;

    @Column(name = "agent_class")
    private String AgentClass;
    @Column(name = "agent_name")
    private String AgentName;
    @Column(name = "agent_version")
    private String AgentVersion;

}