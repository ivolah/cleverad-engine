package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    private Boolean status = true;
    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime lastModificationDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "type_id")
    private MediaType mediaType;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "medias")
    private Set<Campaign> campaigns = new HashSet<>();

    @OneToMany(mappedBy = "media")
    private Set<TransactionCPM> transactionCPMS;

    @OneToMany(mappedBy = "media")
    private Set<TransactionCPC> transactionCPCS;

    @OneToMany(mappedBy = "media")
    private Set<TransactionCPL> transactionCPLS;

}
