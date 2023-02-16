package it.cleverad.engine.persistence.model.service;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_representative")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@Getter
@Setter
@NoArgsConstructor
public class Representative {

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

    @ManyToOne
    @JoinColumn(name = "affiliate_id")
    private Affiliate affiliate;

    @ManyToOne
    @JoinColumn(name = "advertiser_id")
    private Advertiser advertiser;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Dictionary dictionary;

}
