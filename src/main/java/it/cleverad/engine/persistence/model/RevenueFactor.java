package it.cleverad.engine.persistence.model;

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

    private Long revenue;
    private LocalDate dueDate;

    private boolean status;

    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    @ManyToOne()
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @ManyToOne()
    @JoinColumn(name = "type_id")
    private Dictionary dictionary;

}
