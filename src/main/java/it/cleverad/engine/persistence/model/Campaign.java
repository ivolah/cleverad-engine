package it.cleverad.engine.persistence.model;

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
    private Set<AffiliateChannelCommissionCampaign> affiliateChannelCommissionCampaigns;


    @OneToMany(mappedBy = "campaign")
    private Set<Commission> commissionCampaigns;

    @OneToMany(mappedBy = "campaign")
    private Set<CampaignCategory> campaignCategories;

    @OneToMany(mappedBy = "campaign")
    private Set<CampaignCookie> campaignCookies;

    @OneToMany(mappedBy = "campaign")
    private Set<Transaction> transactions;

    @OneToMany(mappedBy = "campaign")
    private Set<RevenueFactor> revenueFactors;

    @OneToMany(mappedBy = "campaign")
    private Set<Budget> budgets;

    @ManyToOne
    @JoinColumn(name = "cookie_id")
    private Cookie cookie;

    // >>>  CAMPAIGN + AFFILIATE >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "t_campaign_affiliate", joinColumns = @JoinColumn(name = "campaign_id"), inverseJoinColumns = @JoinColumn(name = "affilaite_id"))
    private Set<Affiliate> affiliates;

    public void addAffiliate(Affiliate affiliate) {
        this.affiliates.add(affiliate);
        affiliate.getCampaigns().add(this);
    }

    public void removeAffiliate(long tagId) {
        Affiliate affiliate = this.affiliates.stream().filter(t -> t.getId() == tagId).findFirst().orElse(null);
        if (affiliate != null) {
            this.affiliates.remove(affiliate);
            affiliate.getCampaigns().remove(this);
        }
    }

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

}
