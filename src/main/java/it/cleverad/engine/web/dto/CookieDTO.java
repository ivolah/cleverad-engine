package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.Cookie;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CookieDTO {

    private long id;
    private String name;
    private String value;
    private String status;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    public CookieDTO(long id, String name, String value, String status, LocalDateTime creationDate, LocalDateTime lastModificationDate) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.status = status;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
    }

    public static CookieDTO from(Cookie cookie) {
        return new CookieDTO(cookie.getId(), cookie.getName(), cookie.getValue(), cookie.getStatus(), cookie.getCreationDate(), cookie.getLastModificationDate());
    }

}
