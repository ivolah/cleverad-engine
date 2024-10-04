package it.cleverad.engine.persistence.model.tracking;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_cpm")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Cpm {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ip;
    private String agent;
    @CreatedDate
    private LocalDateTime date = LocalDateTime.now();
    @Column(nullable = false)
    private Boolean read = false;
    private Boolean blacklisted = false;

    //dati refferal
    private String refferal;
    @Column(name = "campaign_id")
    private Long campaignId;
    @Column(name = "image_id")
    private Long imageId;
    @Column(name = "media_id")
    private Long mediaId;
    @Column(name = "affiliate_id")
    private Long affiliateId;
    @Column(name = "channel_id")
    private Long channelId;
    @Column(name = "target_id")
    private Long targetId;

}