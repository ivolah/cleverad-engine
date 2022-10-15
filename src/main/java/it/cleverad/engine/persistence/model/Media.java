package it.cleverad.engine.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_media")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Media {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String url;
    private String target;
    private String bannerCode;
    private String note;
    private String idFile;
    private Boolean status;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    @OneToOne(mappedBy = "media")
    private MediaCampaign mediaCampaign;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private MediaType mediaType;

}
