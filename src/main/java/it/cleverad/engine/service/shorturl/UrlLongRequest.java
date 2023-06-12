package it.cleverad.engine.service.shorturl;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Data
public class UrlLongRequest {
    private String longUrl;
    private LocalDate expiresDate;
}