package it.cleverad.engine.config.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class JwtResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String jwttoken;
    private final Long idUser;

    public JwtResponse(String jwttoken, Long idUser) {
        this.jwttoken = jwttoken;
        this.idUser = idUser;
    }

}