package it.cleverad.engine.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "t_media_type")
@Getter
@Setter
@NoArgsConstructor
public class MediaType {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Boolean status = true;

    @OneToMany
    @JoinColumn(name = "type_id")
    private Set<Media>  medias;

}
