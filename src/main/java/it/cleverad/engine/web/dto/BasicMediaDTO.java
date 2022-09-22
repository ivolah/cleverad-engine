package it.cleverad.engine.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor
public class BasicMediaDTO {

    private long id;
    private String name;
    private String typeId;
    private String url;
    private String target;
    private String note;

}
