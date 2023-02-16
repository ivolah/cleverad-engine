package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_editor")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Editor {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String vatNumber;
    private String street;
    private String streetNumber;
    private String city;
    private String province;
    private String zipCode;
    private String primaryMail;
    private String secondaryMail;
    private String country;
    private String phonePrefix;
    private String phoneNumber;

    private String note;

    private String bank;
    private String iban;
    private String swift;
    private String paypal;

    @Column(nullable = false)
    private Boolean status = true;
    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime lastModificationDate = LocalDateTime.now();

} 
