package it.cleverad.engine.service.telegram;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@Transactional
public class CleveradBot
extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String BOT_TOKEN;
    @Value("${telegram.chat.id}")
    private String BOT_USERNAME;

    public synchronized void sendMsg(String chatId, String s) {
        SendMessage sendMessagee = new SendMessage();
        sendMessagee.enableMarkdown(true);
        log.info(chatId);
        sendMessagee.setChatId(chatId);
        sendMessagee.setText(s);
        try {
            execute(sendMessagee);
        } catch (TelegramApiException e) {
            log.error("Exception: ", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            sendMsg(update.getMessage().getChatId().toString(),"Grazie per aver scritto : " + message);
        }
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }
}
