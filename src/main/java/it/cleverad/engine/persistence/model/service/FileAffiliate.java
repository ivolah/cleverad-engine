package it.cleverad.engine.persistence.model.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_file_affiliate")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileAffiliate {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    private String note;
    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();

    @Lob
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "affiliate_id")
    private Affiliate affiliate;

    @ManyToOne
    @JoinColumn(name = "dictionary_id")
    private Dictionary dictionary;

    public FileAffiliate(String name, String type, String docType, byte[] data, Affiliate affiliate, Dictionary dictionary) {
        this.name = name;
        this.note = docType;
        this.data = data;
        this.affiliate = affiliate;
        this.dictionary = dictionary;
    }

}
