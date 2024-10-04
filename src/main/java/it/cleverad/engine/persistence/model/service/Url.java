package it.cleverad.engine.persistence.model.service;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "t_url")
@Data
@Getter
@Setter
@NoArgsConstructor
public class Url {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "long_url")
    private String longUrl;
    private String tiny;
    private String alias;
    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate = LocalDate.now();
    @Column(name = "expires_date")
    private LocalDate expiresDate = LocalDate.now();

}