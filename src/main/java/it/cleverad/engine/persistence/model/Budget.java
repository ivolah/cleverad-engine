package it.cleverad.engine.persistence.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "t_budget")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@Getter
@Setter
@NoArgsConstructor
public class Budget {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idAffiliate;
    private Long budget;
    private Date dueDate;

    private boolean status;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    @OneToMany(mappedBy = "affiliate")
    private Set<AffiliateBudgetCampaign> affiliateBudgets;

}
