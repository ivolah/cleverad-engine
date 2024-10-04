package it.cleverad.engine.persistence.model.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
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
    private Boolean avatar;
    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String path;

    public FileUser(String name, String docType, User userFiles, Boolean avatar, String path) {
        this.name = name;
        this.type = docType;
        this.user = userFiles;
        this.avatar = avatar;
        this.path = path;
    }

}