//package it.cleverad.engine.persistence.model.incoming;
//
//import javax.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Entity
//@Table(name = "dati_registrazione")
//@Inheritance(strategy = InheritanceType.JOINED)
//@Getter
//@Setter
//@NoArgsConstructor
//public class Registrazione {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String nome;
//    @Column(name = "ragione_sociale")
//    private String ragioneSociale;
//    private String piva;
//    private String telefono;
//    private String email;
//    private String citta;
//    private String indirizzo;
//    private String civico;
//    private String cap;
//    private String provincia;
//    private String privacy1;
//    private String privacy2;
//    private String privacy3;
//    private String ip;
////    @Column(name = "user_agent")
////    private String userAgent;
//
//}