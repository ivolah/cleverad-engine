package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_revenuefactor")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class RevenueFactor {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double revenue;
    @Column(name = "due_date")
    private LocalDate dueDate;
    @Column(name = "start_date")
    private LocalDate startDate;
    private Boolean status = true;
    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();
    @Column(name = "last_modification_date")
    private LocalDateTime lastModificationDate = LocalDateTime.now();

    @ManyToOne()
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @ManyToOne()
    @JoinColumn(name = "dictionary_id")
    private Dictionary dictionary;

    private String action;
    private Double sale;

}