package it.cleverad.engine.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_campaign_cookie")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class CampaignCookie {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    @ManyToOne
    @JoinColumn(name = "cookie_id")
    private Cookie cookie;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;
}
