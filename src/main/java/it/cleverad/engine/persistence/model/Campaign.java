package it.cleverad.engine.persistence.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "t_campaign")
@Inheritance(
        strategy = InheritanceType.JOINED
)
@Data
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
    private Boolean status;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String idFile;
    private String defaultCommissionId;
    private String valuta;
    private Long budget;

    @OneToMany(mappedBy = "campaign")
    private Set<MediaCampaign> mediaCampaignList;

//    @OneToMany(mappedBy = "campaign")
//    private Set<AffiliateChannelCommissionCampaign> affiliateChannelCommissionCampaigns;

    @OneToMany(mappedBy = "campaign")
    private Set<AffiliateCampaign> affiliateCampaigns;

    @OneToMany(mappedBy = "campaign")
    private Set<Commission> commissionCampaigns;

    @OneToMany(mappedBy = "campaign")
    private Set<CampaignCategory> campaignCategories;

    @OneToMany(mappedBy = "campaign")
    private Set<CampaignCookie> campaignCookies;

    @OneToMany(mappedBy = "campaign")
    private Set<CampaignRevenueFactor> campaignRevenueFactors;

    @OneToMany(mappedBy = "campaign")
    private Set<AffiliateBudgetCampaign> affiliateBudgets;

    public Campaign(Long id, String name, String shortDescription, String longDescription, Boolean status, LocalDateTime creationDate, LocalDateTime lastModificationDate, LocalDate startDate, LocalDate endDate, String idFile, String defaultCommissionId, String valuta, Long budget, Set<MediaCampaign> mediaCampaignList, Set<AffiliateCampaign> affiliateCampaigns, Set<Commission> commissionCampaigns, Set<CampaignCategory> campaignCategories, Set<CampaignCookie> campaignCookies, Set<CampaignRevenueFactor> campaignRevenueFactors) {
        this.id = id;
        this.name = name;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.status = status;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.idFile = idFile;
        this.valuta = valuta;
        this.budget = budget;
        this.mediaCampaignList = mediaCampaignList;
        this.affiliateCampaigns = affiliateCampaigns;
        this.commissionCampaigns = commissionCampaigns;
        this.campaignCategories = campaignCategories;
        this.campaignCookies = campaignCookies;
        this.campaignRevenueFactors = campaignRevenueFactors;
    }
}
