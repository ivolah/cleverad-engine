package it.cleverad.engine.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_affiliate_budget_campaign")
@Inheritance(
        strategy = InheritanceType.JOINED
)
@Getter
@Setter
@NoArgsConstructor
public class AffiliateBudgetCampaign {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    @ManyToOne
    @JoinColumn(name="affiliate_id")
    private Affiliate affiliate;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @ManyToOne
    @JoinColumn(name="campaign_id")
    private Campaign campaign;

}
