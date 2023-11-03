package it.cleverad.engine.service.whatsapp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class Check {

    @JsonProperty("status")
    public String status;
    @JsonProperty("chatId")
    public String chatId;

}