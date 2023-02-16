package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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
    private LocalDate dueDate;

    private Boolean status = true;
    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime lastModificationDate = LocalDateTime.now();

    @ManyToOne()
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @ManyToOne()
    @JoinColumn(name = "dictionary_id")
    private Dictionary dictionary;

}
