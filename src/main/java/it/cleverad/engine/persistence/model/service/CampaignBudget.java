package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_campaign_budget")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class CampaignBudget {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "advertiser_id")
    private Advertiser advertiser;
    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @Column(name = "cap_iniziale")
    private Integer capIniziale;
    @Column(name = "cap_erogato")
    private Integer capErogato;
    @Column(name = "cap_fatturabile")
    private Integer capFatturabile;

    @Column(name = "budget_iniziale")
    private Double budgetIniziale;
    @Column(name = "budget_erogato")
    private Double budgetErogato;
    @Column(name = "fattura_id")
    private Long fatturaId;

    private Double fatturato;
    private Double scarto;
    private String materiali;

    private String note;
    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

}
