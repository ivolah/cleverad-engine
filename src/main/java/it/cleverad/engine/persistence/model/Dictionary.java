package it.cleverad.engine.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "t_dictionary")
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

    private boolean status = true;

    @OneToMany
    @JoinColumn(name = "dictionary_id")
    private Set<Commission> commission;

    @OneToMany
    @JoinColumn(name = "role_id")
    private Set<User> users;

    @OneToMany
    @JoinColumn(name = "role_id")
    private Set<Representative> representatives;

    @OneToMany
    @JoinColumn(name = "dictionary_id")
    private Set<Channel> channels;

    @OneToMany
    @JoinColumn(name = "dictionary_id")
    private Set<RevenueFactor> revenueFactors;

}
