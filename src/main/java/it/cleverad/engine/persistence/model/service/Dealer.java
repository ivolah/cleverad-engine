package it.cleverad.engine.persistence.model.service;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "t_dealer")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@Getter
@Setter
@NoArgsConstructor
public class Dealer {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;
    private String email;
    private String phonePrefix;
    private String phone;
    private String mobilePrefix;
    private String mobile;

    @Column(nullable = false)
    private Boolean status = true;
    private LocalDateTime creationDate = LocalDateTime.now();

    @OneToMany
    @JoinColumn(name = "id")
    private Set<Campaign> campaigns;

}
