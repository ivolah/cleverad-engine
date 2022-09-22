package it.cleverad.engine.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "t_affiliate")
@Inheritance(
        strategy = InheritanceType.JOINED
)
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

    @Override
    public String toString() {
        return "Affiliate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", vatNumber='" + vatNumber + '\'' +
                ", street='" + street + '\'' +
                ", streetNumber='" + streetNumber + '\'' +
                ", city='" + city + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", primaryMail='" + primaryMail + '\'' +
                ", secondaryMail='" + secondaryMail + '\'' +
                ", status='" + status + '\'' +
                ", creationDate=" + creationDate +
                ", lastModificationDate=" + lastModificationDate +
                ", affiliateCampaigns=" + affiliateCampaigns +
                ", commissionCampaigns=" + commissionCampaigns +
                '}';
    }
}
