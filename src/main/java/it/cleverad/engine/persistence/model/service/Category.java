package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "t_category")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Category {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String code;
    private String description;

    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();
    @Column(name = "last_modification_date")
    private LocalDateTime lastModificationDate = LocalDateTime.now();

    @OneToMany(mappedBy = "category")
    private Set<ChannelCategory> channelCategories;

}