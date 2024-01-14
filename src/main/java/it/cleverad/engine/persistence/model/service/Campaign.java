package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
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
    @Column(name = "short_description")
    private String shortDescription;
    @Column(name = "long_description")
    private String longDescription;
    @Column(name = "note")
    private String note;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Column(name = "id_file")
    private String idFile;
    private String valuta;
    @Column(name = "encoded_id")
    private String encodedId;
    private Double budget;
    @Column(name = "initial_budget")
    private Double initialBudget;
    @Column
    private String cap;
    @Column(nullable = false)
    private Boolean status = true;
    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();
    @Column(name = "last_modification_date")
    private LocalDateTime lastModificationDate = LocalDateTime.now();

    @Column(name = "check_phone_number")
    private Boolean checkPhoneNumber = false;

    // ONE TO MANY

    @OneToMany(mappedBy = "campaign")
    private Set<AffiliateChannelCommissionCampaign> affiliateChannelCommissionCampaigns;

    @OneToMany(mappedBy = "campaign")
    private Set<Commission> commissionCampaigns;

    @OneToMany(mappedBy = "campaign")
    private Set<CampaignCategory> campaignCategories;

    @OneToMany(mappedBy = "campaign")
    private Set<CampaignCookie> campaignCookies;

    @OneToMany(mappedBy = "campaign")
    private Set<RevenueFactor> revenueFactors;

    @OneToMany(mappedBy = "campaign")
    private Set<AffiliateBudget> affiliateBudgets;

    @OneToMany(mappedBy = "campaign")
    private Set<TransactionCPC> transactionCPCS;

    @OneToMany(mappedBy = "campaign")
    private Set<TransactionCPM> transactionCPMS;

    // MANY TO ONE

    @ManyToOne
    @JoinColumn(name = "cookie_id")
    private Cookie cookie;

    @ManyToOne
    @JoinColumn(name = "planner_id")
    private Planner planner;

    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    // >>>  ADVERTISER  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    @ManyToOne
    @JoinColumn(name = "advertiser_id")
    private Advertiser advertiser;

    // >>>  TransactionCPL  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    @OneToMany(mappedBy = "campaign")
    private Set<TransactionCPL> transactionCPLS;

    // >>>  CAMPAIGN + AFFILIATE >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    @OneToMany(mappedBy = "campaign")
    private Set<CampaignAffiliate> campaignAffiliates;

//    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
//    @JoinTable(name = "t_campaign_affiliate", joinColumns = @JoinColumn(name = "campaign_id"), inverseJoinColumns = @JoinColumn(name = "affilaite_id"))
//    private Set<Affiliate> affiliates;

//    public void addAffiliate(Affiliate affiliate) {
//        this.affiliates.add(affiliate);
//        affiliate.getCampaigns().add(this);
//    }
//
//    public void removeAffiliate(long tagId) {
//        Affiliate affiliate = this.affiliates.stream().filter(t -> t.getId() == tagId).findFirst().orElse(null);
//        if (affiliate != null) {
//            this.affiliates.remove(affiliate);
//            affiliate.getCampaigns().remove(this);
//        }
//    }

    // >>>  CAMPAIGN + MEDIA     >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "t_campaign_media", joinColumns = @JoinColumn(name = "campaign_id"), inverseJoinColumns = @JoinColumn(name = "media_id"))
    private Set<Media> medias = new HashSet<>();

    public void addMedia(Media media) {
        this.medias.add(media);
        media.getCampaigns().add(this);
    }

    public void removeMedia(long tagId) {
        Media tag = this.medias.stream().filter(t -> t.getId() == tagId).findFirst().orElse(null);
        if (tag != null) {
            this.medias.remove(tag);
            tag.getCampaigns().remove(this);
        }
    }

    @OneToMany(mappedBy = "campaign")
    private Set<CampaignBudget> campaignBudgets;

}