package it.cleverad.engine.service.webapps;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@ToString
public class Check {

    @JsonProperty("status")
    public String status;
    @JsonProperty("chatId")
    public String chatId;

}