package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Table(name = "t_landing")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Landing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cognome;
    private String telefono;
    private String email;
    private String citta;
    private String via;
    private String civico;
    private String cap;
    private String provincia;
    private Boolean privacy1;
    private Boolean privacy2;
    private Boolean privacy3;
    private String ip;
    private String referral;
    private String order;
    private String transaction;
    private Long campagna;

}