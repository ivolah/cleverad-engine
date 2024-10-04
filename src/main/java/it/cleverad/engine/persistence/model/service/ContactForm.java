package it.cleverad.engine.persistence.model.service;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_contact_form")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class ContactForm {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private String companyName;
    private String country;
    private String requestType;
    private String enquiry;

    private Boolean agreeMailingList;
    private Boolean agreeDataProcetction;

    private LocalDateTime creationDate;

}