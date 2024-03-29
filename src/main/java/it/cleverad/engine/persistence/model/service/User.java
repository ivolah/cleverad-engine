package it.cleverad.engine.persistence.model.service;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "t_user")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(unique = true)
    private String username;
    private String password;
    private String surname;
    private String email;
    private String role;

    @Column(nullable = false)
    private Boolean status = true;
    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "affiliate_id")
    private Affiliate affiliate;

    @ManyToOne
    @JoinColumn(name = "advertiser_id")
    private Advertiser advertiser;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Dictionary dictionary;

    @OneToMany(mappedBy = "user")
    private Set<FileUser> fileUsers;
}