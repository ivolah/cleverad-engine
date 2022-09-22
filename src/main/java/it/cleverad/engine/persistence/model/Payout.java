package it.cleverad.engine.persistence.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "t_payout")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@Getter
@Setter
@NoArgsConstructor
public class Payout {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long affiliateId;
    private Long commissionId;
    private Long clickNumber;
    private Long visitNumber;
    private Long total;
    private String notes;
    private String value;
    private boolean status;
    private String invoiceNumber;
    private Date invoiceDate;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;
}
