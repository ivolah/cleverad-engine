package it.cleverad.engine.persistence.model.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_file_payout")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilePayout {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;

    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "payout_id")
    private Payout payout;

    @ManyToOne
    @JoinColumn(name = "dictionary_id")
    private Dictionary dictionary;

    private String path;

    public FilePayout(String name, String docType, Payout payout, Dictionary dictionary, String path) {
        this.name = name;
        this.type = docType;
        this.payout = payout;
        this.dictionary = dictionary;
        this.path = path;
    }

}