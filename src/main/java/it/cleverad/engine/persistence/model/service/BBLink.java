package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_bb_link")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class BBLink {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String link;
    private String generated;
    private String referral;
    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "commission_id")
    private Commission commission;

    @ManyToOne
    @JoinColumn(name = "brandbuddies_id")
    private Affiliate affiliate;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

}