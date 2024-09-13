package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_bot_data")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class BotData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cap;
    private String telefono;
    private String ip;
    private LocalDateTime ts = LocalDateTime.now();
    @Column(name = "campaign_name")
    private String campaignName;
    @Column(name = "campaign_referral")
    private String campaignReferral;
    private String referral;
    private String email;
    private Boolean privacy1;
    private Boolean privacy2;
}