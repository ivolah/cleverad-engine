//package it.cleverad.engine.web.dto;
//
//import it.cleverad.engine.persistence.model.incoming.Registrazione;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class RegistrazioneDTO {
//
//    private Long id;
//
//    private String nome;
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
//
//
//    public static RegistrazioneDTO from(Registrazione registrazione) {
//        return new RegistrazioneDTO(
//                registrazione.getId(),
//                registrazione.getNome(), registrazione.getRagioneSociale(), registrazione.getPiva(), registrazione.getTelefono(), registrazione.getEmail(),
//                registrazione.getCitta(), registrazione.getIndirizzo(), registrazione.getCivico(), registrazione.getCap(), registrazione.getProvincia(),
//                registrazione.getPrivacy1(), registrazione.getPrivacy2(), registrazione.getPrivacy3(), registrazione.getIp()
//        );
//    }
//
//}