package it.cleverad.engine.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_media_campaign")
//@Data
@Getter
@Setter
@NoArgsConstructor
public class MediaCampaign {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    @OneToOne
    @JoinColumn(name="media_id")
    private Media media;

    @ManyToOne
    @JoinColumn(name="campaign_id")
    private Campaign campaign;

}
