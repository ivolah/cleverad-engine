package it.cleverad.engine.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "t_campaign")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Campaign {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String shortDescription;
    private String longDescription;
    private LocalDate startDate;
    private LocalDate endDate;
    private String idFile;
    private String defaultCommissionId;
    private String valuta;
    private Long budget;
    @Column(nullable = false)
    private Boolean status = true;
    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime lastModificationDate = LocalDateTime.now();

    @OneToMany(mappedBy = "campaign")
    private Set<MediaCampaign> mediaCampaignList;

    @OneToMany(mappedBy = "campaign")
    private Set<AffiliateChannelCommissionCampaign> affiliateChannelCommissionCampaigns;

    @OneToMany(mappedBy = "campaign")
    private Set<AffiliateCampaign> affiliateCampaigns;

    @OneToMany(mappedBy = "campaign")
    private Set<Commission> commissionCampaigns;

    @OneToMany(mappedBy = "campaign")
    private Set<CampaignCategory> campaignCategories;

    @OneToMany(mappedBy = "campaign")
    private Set<CampaignCookie> campaignCookies;

    @OneToMany(mappedBy = "campaign")
    private Set<AffiliateBudgetCampaign> affiliateBudgets;

    @OneToMany(mappedBy = "campaign")
    private Set<Transaction> transactions;

    @OneToMany(mappedBy = "campaign")
    private Set<RevenueFactor> revenueFactors;

    @ManyToOne
    @JoinColumn(name = "cookie_id")
    private Cookie cookie;

}
