package org.example.adminBot;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.example.bot.handlers.RegistrationHandler;
import org.example.enumerators.AdminState;
import org.example.enumerators.UserState;
import org.example.model.User;
import org.example.model.Vacancy;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import static jdk.javadoc.internal.tool.Main.execute;
import static org.example.bot.MyBot.vacancyService;
import static org.example.enumerators.UserState.*;

public class AdminCommandHandler {
    public static  UserService userService = new UserService(new UserRepository());
    private final RegistrationHandler registrationHandler = new RegistrationHandler();
    public static Buttons buttons = new Buttons();

public static SendMessage adminHandle(Update update) {
    Message message = update.getMessage();
    String text = message.getText();
    Long chatId = message.getChatId();

    if (Objects.equals(text, "/start")) {
        return handleStartByAdmin(chatId, message.getFrom());
    }

    User currentUser = userService.findByChatId(chatId);

    if (currentUser != null && currentUser.getState().toString().startsWith("CREATING_VACANCY")) {
        return handleVacancyCreation(currentUser, text, chatId);
    }

    if (Objects.equals(text, "Create Vacancy")) {

        currentUser.setState(UserState.CREATING_VACANCY_TITLE);
        userService.update(currentUser);
        Vacancy vacancy = new Vacancy();
        vacancyService.add(vacancy);
        return new SendMessage(chatId.toString(), "Please enter the vacancy title:");
//    } else if (Objects.equals(text,"Send To Channel")) {
//        SendMessage sendMessage = new SendMessage(chatId.toString(), "Send Channel");
//        sendMessage.setReplyMarkup(buttons.adminMenu());
//        currentUser.setState(SEND_CHANNEL);
//        userService.update(currentUser);
//        return  new SendMessage(chatId.toString(),"Vacancy send Channel");

    }

    return new SendMessage(chatId.toString(), "Unknown command.");

}
    private static SendMessage handleStartByAdmin(Long chatId, org.telegram.telegrambots.meta.api.objects.User from) {
        User user = userService.findByChatId(chatId);
        if (user == null) {
            user = new User();
            user.setChatId(chatId);
            user.setState(UserState.MAIN_MENU);
            userService.add(user);
        } else {
            user.setState(UserState.MAIN_MENU);
            userService.update(user);
        }

        SendMessage sendMessage = new SendMessage(chatId.toString(), "Welcome, Admin!");
        sendMessage.setReplyMarkup(buttons.adminMenu());

        return sendMessage;
    }

    private static SendMessage handleVacancyCreation(User user, String text, Long chatId) {
        UserState currentState = user.getState();
        Vacancy vacancy = vacancyService.getStartsVacancy();
        switch (currentState) {
            case CREATING_VACANCY_TITLE -> {
               // Vacancy vacancy = new Vacancy();
                vacancy.setTitle(text);
                user.setState(UserState.CREATING_VACANCY_COMPANY);
                userService.update(user);
                vacancyService.update(vacancy);
                return new SendMessage(chatId.toString(), "Please enter the company name:");
            }
            case CREATING_VACANCY_COMPANY -> {
                vacancy.setCompany(text);
                user.setState(UserState.CREATING_VACANCY_EXPERIENCE);
                userService.update(user);
                vacancyService.update(vacancy);
                return new SendMessage(chatId.toString(), "Please enter the required experience:");
            }
            case CREATING_VACANCY_EXPERIENCE -> {
                vacancy.setExperience(text);
                user.setState(UserState.CREATING_VACANCY_REGION);
                userService.update(user);
                vacancyService.update(vacancy);
                return new SendMessage(chatId.toString(), "Please enter the region:");
            }
            case CREATING_VACANCY_REGION -> {
                vacancy.setRegion(text);
                user.setState(UserState.CREATING_VACANCY_PHONE_NUMBER);
                userService.update(user);
                vacancyService.update(vacancy);
                return new SendMessage(chatId.toString(), "Please enter the phone number of the company:");
            }
            case CREATING_VACANCY_PHONE_NUMBER -> {
                vacancy.setPhoneNumberCompany(text);
                user.setState(UserState.CREATING_VACANCY_SALARY);
                userService.update(user);
                vacancyService.update(vacancy);
                return new SendMessage(chatId.toString(), "Please enter the salary:");
            }
            case CREATING_VACANCY_SALARY -> {
                vacancy.setSalary(text);
                user.setState(UserState.CREATING_VACANCY_PHOTO_URL);
                userService.update(user);
                vacancyService.update(vacancy);
                return new SendMessage(chatId.toString(), "Please enter the photo URL:");
            }
            case CREATING_VACANCY_PHOTO_URL -> {
                vacancy.setPhotoUrl(text);
                user.setState(UserState.MAIN_MENU);
                userService.update(user);
                vacancyService.add(vacancy);



                return new SendMessage(chatId.toString(), "Vacancy created successfully!");
            }
            default -> throw new IllegalStateException("Unexpected state: " + currentState);
        }
    }



//    private boolean sendRequest(User currentUser,Message message,String text,SendMessage sendMessage){
//    if(currentUser.getState().equals(SEND_CHANNEL)){
//        Vacancy vacancy = vacancyService
//    }
    }









