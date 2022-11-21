package it.cleverad.engine.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "t_payout")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Payout {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Double totale;
    private String valuta;
    private String note;
    private Boolean stato;
    private LocalDate data;

    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime lastModificationDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "affiliate_id")
    private Affiliate affiliate;

    @OneToMany(mappedBy = "payout")
    private Set<TransactionCPL> transactionCPLS;

    @OneToMany(mappedBy = "payout")
    private Set<TransactionCPC> transactionCPCS;

    @OneToMany(mappedBy = "payout")
    private Set<TransactionCPM> transactionCPMS;

}
