package it.cleverad.engine.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "t_company")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Company {

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

    private Boolean status = true;
    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime lastModificationDate = LocalDateTime.now();

    @OneToMany(mappedBy = "company")
    private Set<TransactionCPL> transactionCPLS;

    @OneToMany(mappedBy = "company")
    private Set<Campaign> campaigns;

}
