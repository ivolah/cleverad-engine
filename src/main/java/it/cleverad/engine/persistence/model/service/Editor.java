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
    @Column(name = "vat_number")
    private String vatNumber;
    private String street;
    @Column(name = "strett_number")
    private String streetNumber;
    private String city;
    private String province;
    @Column(name = "zip_code")
    private String zipCode;
    @Column(name = "primary_mail")
    private String primaryMail;
    @Column(name = "secondary_mail")
    private String secondaryMail;
    private String country;
    @Column(name = "phone_prefix")
    private String phonePrefix;
    @Column(name = "phone_number")
    private String phoneNumber;

    private String note;

    private String bank;
    private String iban;
    private String swift;
    private String paypal;

    @Column(nullable = false)
    private Boolean status = true;
    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();
    @Column(name = "last_modification_date")
    private LocalDateTime lastModificationDate = LocalDateTime.now();

} 
