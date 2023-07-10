package it.cleverad.engine.persistence.model.tracking;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_cps")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Cps {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String refferal;
    private String ip;
    private String agent;
    private String data;

    private LocalDateTime date = LocalDateTime.now();
    private Boolean read = false;

    //dati refferal
    @Column(name = "media_id")
    private Long mediaId;
    @Column(name = "campaign_id")
    private Long campaignId;
    @Column(name = "affiliate_id")
    private Long affiliateId;
    @Column(name = "channel_id")
    private Long channelId;
    @Column(name = "target_id")
    private Long targetId;

    private Boolean blacklisted = false;
}
