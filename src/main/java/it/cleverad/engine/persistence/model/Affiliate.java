package it.cleverad.engine.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
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
    private String zipCode;
    private String primaryMail;
    private String secondaryMail;
    private String status;
    private LocalDate creationDate;
    private LocalDate lastModificationDate;

    @OneToMany(mappedBy = "affiliate")
    private Set<AffiliateChannelCommissionCampaign> affiliateCampaigns;

    @OneToMany(mappedBy = "affiliate")
    private Set<AffiliateChannelCommissionCampaign> commissionCampaigns;

    @OneToMany(mappedBy = "affiliate")
    private Set<AffiliateBudgetCampaign> affiliateBudgets;

    @OneToMany(mappedBy = "affiliate")
    private Set<Wallet> wallets;

    @OneToMany(mappedBy = "affiliate")
    private Set<Transaction> transactions;

    public void addWallet(Wallet wallet) {
        wallet.setAffiliate(this);
        this.wallets.add(wallet);
    }

}
