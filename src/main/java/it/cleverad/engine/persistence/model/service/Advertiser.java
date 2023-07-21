package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "t_advertiser")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Advertiser {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(name = "vat_number")
    private String vatNumber;
    private String street;
    private String country;
    @Column(name = "street_number")
    private String streetNumber;
    private String city;
    @Column(name = "zip_code")
    private String zipCode;
    @Column(name = "primary_mail")
    private String primaryMail;
    @Column(name = "secondary_mail")
    private String secondaryMail;

    private Boolean status = true;

    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();
    @Column(name = "last_modification_date")
    private LocalDateTime lastModificationDate = LocalDateTime.now();

    @OneToMany(mappedBy = "advertiser")
    private Set<TransactionCPL> transactionCPLS;

    @OneToMany(mappedBy = "advertiser")
    private Set<Campaign> campaigns;

    @OneToMany(mappedBy = "advertiser")
    private Set<Representative> representatives;

    @OneToMany(mappedBy = "advertiser")
    private Set<CampaignBudget> campaignBudgets;
    @OneToMany(mappedBy = "advertiser")
    private Set<FileAdvertiser> fileAdvertisers;

}
