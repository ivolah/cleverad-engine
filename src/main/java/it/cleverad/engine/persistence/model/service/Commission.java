package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "t_commision")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Commission {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double value;
    private String description;
    @Column(name = "due_date")
    private LocalDate dueDate;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(nullable = false)
    private Boolean status = true;
    private Boolean base = false;

    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();
    @Column(name = "last_modification_date")
    private LocalDateTime lastModificationDate = LocalDateTime.now();


    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @ManyToOne
    @JoinColumn(name = "dictionary_id")
    private Dictionary dictionary;

    @OneToMany(mappedBy = "commission")
    private Set<TransactionCPC> transactionCPCS;

    @OneToMany(mappedBy = "commission")
    private Set<TransactionCPM> transactionCPMS;

    @OneToMany(mappedBy = "commission")
    private Set<TransactionCPL> transactionCPLS;

}
