package it.cleverad.engine.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_commision")
@Inheritance(strategy = InheritanceType.JOINED)
//@Data
@Getter
@Setter
@NoArgsConstructor
public class Commission {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    private String name;
    private String value;
    private String description;
    private Boolean status;
    private String idType;
    private LocalDate dueDate;

    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;


}
