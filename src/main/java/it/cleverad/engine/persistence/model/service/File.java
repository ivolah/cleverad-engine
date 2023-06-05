package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_file")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class File {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    @Lob
    private byte[] data;

    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();
    private String path;

    public File(String name, String type, byte[] data, String path) {
        this.name = name;
        this.type = type;
        this.data = data;
        this.creationDate = LocalDateTime.now();
        this.path = path;
    }

}
