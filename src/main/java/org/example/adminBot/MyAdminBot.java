package org.example.adminBot;

import org.example.exception.DataNotFoundException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MyAdminBot extends TelegramLongPollingBot {
    public MyAdminBot() {
        super("7048541088:AAEIskpl5IRCebTtKS8kWFCrNc2LR6U6hAk");
    }

    @Override
    public void onUpdateReceived(Update update) {

        SendMessage response = AdminCommandHandler.adminHandle(update);
        send(response);
    }


    private void send(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String getBotUsername() {
        return "@vacancyadminn_bot";
    }
}
