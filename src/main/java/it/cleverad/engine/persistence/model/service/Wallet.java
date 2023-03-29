package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;


@Entity
@Table(name = "t_wallet")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Wallet {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String description;

    private Double total;
    private Double payed;
    private Double residual;

    private Boolean status = true;

    @ManyToOne
    @JoinColumn(name = "affiliate_id")
    private Affiliate affiliate;

    @OneToMany(mappedBy = "wallet")
    private Set<TransactionCPC> transactionCPCS;

    @OneToMany(mappedBy = "wallet")
    private Set<TransactionCPM> transactionCPMS;

    @OneToMany(mappedBy = "wallet")
    private Set<TransactionCPL> transactionCPLS;

    @OneToMany(mappedBy = "wallet")
    private Set<WalletTransaction> walletTransactions;

}
