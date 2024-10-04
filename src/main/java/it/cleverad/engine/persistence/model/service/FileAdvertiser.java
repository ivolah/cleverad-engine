package it.cleverad.engine.persistence.model.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_file_advertiser")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileAdvertiser {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private String note;
    private String path;

    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "advertiser_id")
    private Advertiser advertiser;

    @ManyToOne
    @JoinColumn(name = "dictionary_id")
    private Dictionary dictionary;

    public FileAdvertiser(String name, String type, String note, String path, LocalDateTime creationDate, Advertiser advertiser, Dictionary dictionary) {
        this.name = name;
        this.type = type;
        this.note = note;
        this.path = path;
        this.creationDate = creationDate;
        this.advertiser = advertiser;
        this.dictionary = dictionary;
    }
}