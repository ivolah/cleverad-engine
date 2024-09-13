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
    private LocalDateTime ts;
    @Column(name = "project_name")
    private String projectName;
    @Column(name = "project_referral")
    private String projectReferral;

}