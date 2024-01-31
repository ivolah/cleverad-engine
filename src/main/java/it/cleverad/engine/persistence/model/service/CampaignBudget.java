package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "t_campaign_budget")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CampaignBudget {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();

    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @ManyToOne
    @JoinColumn(name = "advertiser_id")
    private Advertiser advertiser;

    @ManyToOne
    @JoinColumn(name = "planner_id")
    private Planner planner;

    @ManyToOne
    @JoinColumn(name = "canale_id")
    private Dictionary canali;

    private Boolean prenotato = true;

    @ManyToOne
    @JoinColumn(name = "tipologia_id")
    private Dictionary dictionary;

    @Column(name = "cap_iniziale")
    private Integer capIniziale;

    private Double payout;

    @Column(name = "budget_iniziale")
    private Double budgetIniziale;

    @Column(name = "cap_erogato")
    private Integer capErogato;
    @Column(name = "cap_volume")
    private Integer capVolume;

    @Column(name = "cap_pc")
    private Double capPc;

    @Column(name = "budget_erogato")
    private Double budgetErogato;

    // costo editore --> commissioni
    @Column(name = "commissioni_erogate")
    private Double commissioniErogate;

    // margine --> revenue
    @Column(name = "revenue_pc")
    private Double revenuePC;

    @Column(name = "revenue")
    private Double revenue;

    // stima scarto editore
    private Double scarto;

    // post scarto
    @Column(name = "budget_erogatops")
    private Double budgetErogatops;
    @Column(name = "commissioni_erogateps")
    private Double commissioniErogateps;
    @Column(name = "revenue_pcps")
    private Double revenuePCPS;
    @Column(name = "revenue_ps")
    private Double revenuePS;

    @Column(name = "revenue_day")
    private Double revenueDay;

    private String materiali;
    private String note;

    @Column(name = "cap_fatturabile")
    private Integer capFatturabile;
    private Double fatturato;
    private Boolean status;

    @Column(name = "stato_fatturato")
    private Boolean statoFatturato;
    @Column(name = "stato_pagato")
    private Boolean statoPagato;

    @Column(name = "invoice_due_date")
    private LocalDate invoiceDueDate;

    private Integer volume;
    @Column(name = "volume_date")
    private LocalDate volumeDate;
    @Column(name = "volume_delta")
    private Integer volumeDelta;

    @OneToMany(mappedBy = "campaignBudget")
    private Set<FileCampaignBudgetInvoice> fileCampaignBudgetInvoices;

    @OneToMany(mappedBy = "campaignBudget")
    private Set<FileCampaignBudgetOrder> fileCampaignBudgetOrders;

}