package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "t_dictionary")
@Getter
@Setter
@NoArgsConstructor
public class Dictionary {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String type;

    private boolean status = true;

    @OneToMany
    @JoinColumn(name = "dictionary_id")
    private Set<Commission> commission;

    @OneToMany
    @JoinColumn(name = "role_id")
    private Set<User> users;

    @OneToMany
    @JoinColumn(name = "role_id")
    private Set<Representative> representatives;

    @OneToMany
    @JoinColumn(name = "dictionary_id")
    private Set<Channel> channels;
    @OneToMany
    @JoinColumn(name = "type_id")
    private Set<Channel> channelsTypes;
    @OneToMany
    @JoinColumn(name = "owner_id")
    private Set<Channel> channelsOwners;
    @OneToMany
    @JoinColumn(name = "business_type_id")
    private Set<Channel> channelsBusinessType;

    @OneToMany
    @JoinColumn(name = "dictionary_id")
    private Set<RevenueFactor> revenueFactors;

    @OneToMany
    @JoinColumn(name = "dictionary_id")
    private Set<FileAffiliate> fileAffiliates;
    @OneToMany
    @JoinColumn(name = "dictionary_id")
    private Set<FilePayout> filePayouts;
    @OneToMany
    @JoinColumn(name = "dictionary_id")
    private Set<FileAdvertiser> fileAdvertisers;

    @OneToMany
    @JoinColumn(name = "companytype_id")
    private Set<Affiliate> affiliateCompanyTypes;

    @OneToMany
    @JoinColumn(name = "status_id")
    private Set<CampaignAffiliate> campaignAffiliates;

    @OneToMany
    @JoinColumn(name = "dictionary_id")
    private Set<TransactionCPC> transactionCPCS;
    @OneToMany
    @JoinColumn(name = "dictionary_id")
    private Set<TransactionCPM> transactionCPMS;
    @OneToMany
    @JoinColumn(name = "dictionary_id")
    private Set<TransactionCPL> transactionCPLS;
    @OneToMany
    @JoinColumn(name = "dictionary_id")
    private Set<TransactionCPS> transactionCPS;

    @OneToMany
    @JoinColumn(name = "status_id")
    private Set<TransactionCPC> transactionCPCSstatus;
    @OneToMany
    @JoinColumn(name = "status_id")
    private Set<TransactionCPM> transactionCPMSstatus;
    @OneToMany
    @JoinColumn(name = "status_id")
    private Set<TransactionCPL> transactionCPLSstatus;
    @OneToMany
    @JoinColumn(name = "status_id")
    private Set<TransactionCPS> transactionCPSstatus;

    @OneToMany
    @JoinColumn(name = "tipologia_id")
    private Set<CampaignBudget> campaignBudgets;

    @OneToMany
    @JoinColumn(name = "format_id")
    private Set<Media> mediaFormats;

}