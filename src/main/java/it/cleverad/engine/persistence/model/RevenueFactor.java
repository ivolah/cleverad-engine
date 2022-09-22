package it.cleverad.engine.persistence.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "t_revenuefactor")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@Getter
@Setter
@NoArgsConstructor
public class RevenueFactor {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String idType;
    private Long revenue;
    private LocalDate dueDate;

    private boolean status;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    @OneToMany(mappedBy = "revenuefactor")
    private Set<CampaignRevenueFactor> campaignRevenueFactors;

}
