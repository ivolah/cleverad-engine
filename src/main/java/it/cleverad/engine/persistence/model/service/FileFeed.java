package it.cleverad.engine.persistence.model.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_file_feed")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileFeed {

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
    @JoinColumn(name = "feed_id")
    private Feed feed;

    public FileFeed(String name, String type, String note, String path, LocalDateTime creationDate, Advertiser advertiser) {
        this.name = name;
        this.type = type;
        this.note = note;
        this.path = path;
        this.creationDate = creationDate;
        this.advertiser = advertiser;
    }
}