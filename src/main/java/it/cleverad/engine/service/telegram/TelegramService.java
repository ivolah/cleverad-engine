package it.cleverad.engine.service.telegram;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TelegramService {

    @Autowired
    private CleveradBot cleveradBot;

    public void invia(BaseCreateRequest request) {
        cleveradBot.sendMsg("208751791", request.getMessage());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BaseCreateRequest {
        private String message;
    }

}

