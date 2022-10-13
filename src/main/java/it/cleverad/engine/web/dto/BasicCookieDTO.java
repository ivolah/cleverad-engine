package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.Cookie;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BasicCookieDTO {

    private long id;
    private String name;
    private String value;
    private Boolean status;

    public BasicCookieDTO(long id, String name, String value, Boolean status) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.status = status;
    }

    public static BasicCookieDTO from(Cookie cookie) {
        return new BasicCookieDTO(cookie.getId(), cookie.getName(), cookie.getValue(), cookie.getStatus());
    }

}
