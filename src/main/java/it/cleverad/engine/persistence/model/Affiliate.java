package it.cleverad.engine.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "t_affiliate")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Affiliate {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String vatNumber;
    private String street;
    private String streetNumber;
    private String city;
    private String province;
    private String zipCode;
    private String primaryMail;
    private String secondaryMail;
    private String country;
    private String phonePrefix;
    private String phoneNumber;

    private String note;

    private String bank;
    private String iban;
    private String swift;
    private String paypal;

    private String firstName;
    private String lastName;


    private String nomeSitoSocial;
    private String urlSitoSocial;

    @ManyToOne
    @JoinColumn(name = "companytype_id")
    private Dictionary dictionaryCompanyType;

    @ManyToOne
    @JoinColumn(name = "channeltype_id")
    private Dictionary dictionaryChannelType;

    private String contenutoSito;

    private String type;
    private Boolean cb;
    

    @Column(nullable = false)
    private Boolean status = true;
    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime lastModificationDate = LocalDateTime.now();

    @OneToMany(mappedBy = "affiliate")
    private Set<AffiliateChannelCommissionCampaign> affiliateChannelCommissionCampaigns;

//    @OneToMany(mappedBy = "affiliate")
//    private Set<AffiliateChannelCommissionCampaign> commissionCampaigns;

    @OneToMany(mappedBy = "affiliate")
    private Set<Wallet> wallets;

    @OneToMany(mappedBy = "affiliate")
    private Set<User> users;

    @OneToMany(mappedBy = "affiliate", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Representative> representatives;

    @OneToMany(mappedBy = "affiliate")
    private Set<Channel> channels;

    @OneToMany(mappedBy = "affiliate")
    private Set<Budget> budgets;

    @OneToMany(mappedBy = "affiliate")
    private Set<TransactionCPC> transactionCPCS;

    @OneToMany(mappedBy = "affiliate")
    private Set<TransactionCPM> transactionCPMS;

    @OneToMany(mappedBy = "affiliate")
    private Set<TransactionCPL> transactionCPLS;

    @OneToMany(mappedBy = "affiliate")
    private Set<Payout> payouts;

    @OneToMany(mappedBy = "affiliate")
    private Set<FileAffiliate> fileAffiliates;

    // >>>  CAMPAIGN + AFFILIATE >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    @OneToMany(mappedBy = "affiliate")
    private Set<CampaignAffiliate> campaignAffiliates;

//    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "affiliates")
//    private Set<Campaign> campaigns = new HashSet<>();

} 
