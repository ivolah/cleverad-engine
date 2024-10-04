package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Table(name = "t_bb_platform")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class BBPlatform {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String dimension;
    private Boolean verified = true;

    @ManyToOne
    @JoinColumn(name = "brandbuddies_id")
    private Affiliate affiliate;

    @ManyToOne
    @JoinColumn(name = "platform_id")
    private Dictionary dictionary;

}