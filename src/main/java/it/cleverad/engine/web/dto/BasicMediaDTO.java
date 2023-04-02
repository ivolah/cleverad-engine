package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Target;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Data
@NoArgsConstructor
public class BasicMediaDTO {

    private long id;
    private String name;
    private String typeId;
    private String url;
    //private List<Target> target;
    private String note;

}
