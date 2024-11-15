package org.example.bot;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.example.bot.handlers.CommandHandler;
import org.example.model.Vacancy;
import org.example.repository.UserRepository;
import org.example.repository.VacancyRepository;
import org.example.service.UserService;
import org.example.service.VacancyService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyBot extends TelegramLongPollingBot {


public static UserService userService = new UserService(new UserRepository());
public static VacancyService vacancyService = new VacancyService(new VacancyRepository());
public static Buttons buttons =new Buttons();
public static CommandHandler commandHandler;

public MyBot(){
    super("7015774865:AAEaCGgnw8nagaMudA6Wt8851Ff73jaj7jA");
    commandHandler = new CommandHandler(this);
}

//    {
//        vacancyService.add(new Vacancy("SoftwareDeveloper", "Fido Biznes", "1-3 year", "Qashqadaryo vil","+998944514577","500 $","https://yandex.ru/images/search?text=epam+uzbekistan&img_url=https%3A%2F%2Fstatic4.tgstat.ru%2Fchannels%2F_0%2F28%2F28ea45294ca9663f66ff4da781c07cc4.jpg&pos=0&rpt=simage&stype=image&lr=10335&parent-reqid=1715786961383687-14250812035049778707-balancer-l7leveler-kubr-yp-sas-189-BAL&source=serp"));
//        vacancyService.add(new Vacancy("Python Mentor", "PDP", "1-4 year", "Qashqadaryo vil vil","+998956786543","500$","https://yandex.ru/images/search?text=epam+uzbekistan&img_url=https%3A%2F%2Fstatic4.tgstat.ru%2Fchannels%2F_0%2F28%2F28ea45294ca9663f66ff4da781c07cc4.jpg&pos=0&rpt=simage&stype=image&lr=10335&parent-reqid=1715786961383687-14250812035049778707-balancer-l7leveler-kubr-yp-sas-189-BAL&source=serp"));
//        vacancyService.add(new Vacancy("Frontend mentor", "Najot Ta'lim", "3-4 year", "Toshkent shahri","+998904567890","700$","https://yandex.ru/images/search?text=epam+uzbekistan&img_url=https%3A%2F%2Fstatic4.tgstat.ru%2Fchannels%2F_0%2F28%2F28ea45294ca9663f66ff4da781c07cc4.jpg&pos=0&rpt=simage&stype=image&lr=10335&parent-reqid=1715786961383687-14250812035049778707-balancer-l7leveler-kubr-yp-sas-189-BAL&source=serp"));
//        vacancyService.add(new Vacancy("PHP developer", "Aribus firma", "4-4 year", "Samarqand","+998934567897","800$","https://yandex.ru/images/search?text=epam+uzbekistan&img_url=https%3A%2F%2Fstatic4.tgstat.ru%2Fchannels%2F_0%2F28%2F28ea45294ca9663f66ff4da781c07cc4.jpg&pos=0&rpt=simage&stype=image&lr=10335&parent-reqid=1715786961383687-14250812035049778707-balancer-l7leveler-kubr-yp-sas-189-BAL&source=serp"));
//        vacancyService.add(new Vacancy("ITishnik", "Infinbank", "5-4 year", "Andijon vil","+998900000000","3-4 mln","https://yandex.ru/images/search?text=epam+uzbekistan&img_url=https%3A%2F%2Fstatic4.tgstat.ru%2Fchannels%2F_0%2F28%2F28ea45294ca9663f66ff4da781c07cc4.jpg&pos=0&rpt=simage&stype=image&lr=10335&parent-reqid=1715786961383687-14250812035049778707-balancer-l7leveler-kubr-yp-sas-189-BAL&source=serp"));
//        vacancyService.add(new Vacancy("Kassir", "Maktab", "6-4 year", "Qoraqalpog'iston","+998904444444","7-8 mln","https://yandex.ru/images/search?text=epam+uzbekistan&img_url=https%3A%2F%2Fstatic4.tgstat.ru%2Fchannels%2F_0%2F28%2F28ea45294ca9663f66ff4da781c07cc4.jpg&pos=0&rpt=simage&stype=image&lr=10335&parent-reqid=1715786961383687-14250812035049778707-balancer-l7leveler-kubr-yp-sas-189-BAL&source=serp"));
//        vacancyService.add(new Vacancy("IT developer", "Epam", "2-4 year", "Namangan","+9989055564534","9-10 mln","https://yandex.ru/images/search?text=epam+uzbekistan&img_url=https%3A%2F%2Fstatic4.tgstat.ru%2Fchannels%2F_0%2F28%2F28ea45294ca9663f66ff4da781c07cc4.jpg&pos=0&rpt=simage&stype=image&lr=10335&parent-reqid=1715786961383687-14250812035049778707-balancer-l7leveler-kubr-yp-sas-189-BAL&source=serp"));
//
//    }
//    public MyBot() {
//        super("7015774865:AAEaCGgnw8nagaMudA6Wt8851Ff73jaj7jA");
//    }

    @Override
    public void onUpdateReceived(Update update) {
//        SendMessage sendMessage = commandHandler.handle(update.getMessage());
//        if(update.hasMessage()) {
//            send(sendMessage);
//        } else if (update.hasCallbackQuery()) {
//            CallbackQuery callbackQuery = update.getCallbackQuery();
//            MaybeInaccessibleMessage message = callbackQuery.getMessage();
//            String data = callbackQuery.getData();
//            System.out.println(data);
//        }
        SendMessage response = null;
        try {
            if (update.hasMessage()) {
                response = commandHandler.handle(update.getMessage());
            } else if (update.hasCallbackQuery()) {
                response = commandHandler.handleCallbackQuery(update.getCallbackQuery());
            }
            if (response != null) {
                execute(response);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void send(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

//    public static byte[] generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
//
//        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
//        return byteArrayOutputStream.toByteArray();
//    }

    @Override
    public String getBotUsername() {
        return "@openvacancy_bot";
    }


}
