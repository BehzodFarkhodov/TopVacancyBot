package org.example.bot.handlers;

import org.example.bot.MyBot;
import org.example.enumerators.UserState;
import org.example.model.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;
import static org.example.bot.MyBot.buttons;
import static org.example.bot.MyBot.userService;

public class RegistrationHandler {
    public SendMessage handleContact(Message message, User currentUser) {
        Long chatId = message.getChatId();
        if (message.hasContact()) {
            Contact contact = message.getContact();
            if (!Objects.equals(contact.getUserId(), chatId)) {
                return new SendMessage(chatId.toString(), "Please share your own number 📞");
            }
            currentUser.setNumber(contact.getPhoneNumber());
            currentUser.setState(UserState.SHARE_LOCATION);
            MyBot.userService.update(currentUser);
            SendMessage sendMessage = new SendMessage(chatId.toString(), "thank you, please share your location 📍");
            sendMessage.setReplyMarkup(buttons.requestLocation());
            return sendMessage;
        }
        return new SendMessage(chatId.toString(), "please send your contact 📞");
    }

    public SendMessage handleLocation(Message message, User currentUser) {
        Long chatId = message.getChatId();
        if (message.hasLocation()) {
            Location location = message.getLocation();
            currentUser.setLocation(location);
            currentUser.setState(UserState.REGISTERED);

            MyBot.userService.update(currentUser);
            SendMessage sendMessage = new SendMessage(chatId.toString(), "Thank you 😊 Choose one : ");
            sendMessage.setReplyMarkup(buttons.getUserMenu());
            return sendMessage;

        }
        return new SendMessage(chatId.toString(), "please send your location 📍");
    }
}
