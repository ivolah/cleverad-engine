package it.cleverad.engine.persistence.model.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_file_campaign_budget_invoice")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileCampaignBudgetInvoice {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    private String path;
    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "campaign_budget_id")
    private CampaignBudget campaignBudget;

    public FileCampaignBudgetInvoice(String name, String docType, String path, CampaignBudget campaignBudget) {
        this.name = name;
        this.type = docType;
        this.path = path;
        this.campaignBudget = campaignBudget;
    }

}