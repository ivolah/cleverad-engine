package it.cleverad.engine.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;


@Entity
@Table(name = "t_dictionary")
//@Data
@Getter
@Setter
@NoArgsConstructor
public class Dictionary {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String type;
    private boolean status;

    @OneToMany(mappedBy = "commission")
    private Set<Commission> commissions;

}
