package it.cleverad.engine.persistence.model.service;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "t_wallet_transaction")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WalletTransaction {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "total_before")
    private Double totalBefore;
    @Column(name = "payed_before")
    private Double payedBefore;
    @Column(name = "residual_before")
    private Double residualBefore;
    @Column(name = "total_after")
    private Double totalAfter;
    @Column(name = "payed_after")
    private Double payedAfter;
    @Column(name = "residual_after")
    private Double residualAfter;

    private LocalDateTime date = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

}
