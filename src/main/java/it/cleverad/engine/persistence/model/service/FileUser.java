package it.cleverad.engine.persistence.model.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_file_user")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileUser {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    private String note;
    private Boolean avatar;
    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();

    @Lob
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userFiles;

    public FileUser(String name, String docType, byte[] data, User userFiles,  String note, Boolean avatar) {
        this.name = name;
        this.type = docType;
        this.data = data;
        this.userFiles = userFiles;
        this.note = note;
        this.avatar = avatar;
    }

}
