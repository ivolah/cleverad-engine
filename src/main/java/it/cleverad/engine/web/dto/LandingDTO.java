package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Landing;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LandingDTO {

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

    public static LandingDTO from(Landing landing) {
        return new LandingDTO(landing.getId(),
                landing.getNome(), landing.getCognome(), landing.getTelefono(), landing.getEmail(), landing.getCitta(), landing.getVia(), landing.getCivico()
                , landing.getCap(), landing.getProvincia(), landing.getPrivacy1(), landing.getPrivacy2(), landing.getPrivacy3(), landing.getIp()
                , landing.getReferral(), landing.getOrder(), landing.getTransaction(), landing.getCampagna());
    }

}