package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "t_budget")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Budget {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long budget;
    private Date dueDate;

    @Column(nullable = false)
    private Boolean status = true;
    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime lastModificationDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "affiliate_id")
    private Affiliate affiliate;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;



}
