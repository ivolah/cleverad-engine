package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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
    @JoinColumn(name = "dictionary_id")
    private Set<RevenueFactor> revenueFactors;

    @OneToMany
    @JoinColumn(name = "dictionary_id")
    private Set<FileAffiliate> fileAffiliates;

    @OneToMany
    @JoinColumn(name = "dictionary_id")
    private Set<FilePayout> filePayouts;

    @OneToMany
    @JoinColumn(name = "companytype_id")
    private Set<Affiliate> affiliateCompanyTypes;

    @OneToMany
    @JoinColumn(name = "channeltype_id")
    private Set<Affiliate> affiliateChannelType;

    @OneToMany
    @JoinColumn(name = "status_id")
    private Set<CampaignAffiliate> campaignAffiliates;

}


