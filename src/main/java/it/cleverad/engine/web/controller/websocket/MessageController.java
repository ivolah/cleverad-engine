package it.cleverad.engine.web.controller.websocket;

import it.cleverad.engine.web.dto.MessageDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    // Handles messages from /app/chat. (Note the Spring adds the /app prefix for us).
    @MessageMapping("/hello")
    // Sends the return value of this method to /topic/messages
    @SendTo("/topic/messages")
    public MessageDto getMessages(MessageDto dto) {
        MessageDto messageDto = new MessageDto("TEST WEBSOCKET CLEVERAD");
        return messageDto;
    }

}
