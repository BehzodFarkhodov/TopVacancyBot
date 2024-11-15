package org.example;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.example.adminBot.MyAdminBot;
import org.example.bot.MyBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.FileInputStream;
import java.io.IOException;

public class Main {


    public static void main(String[] args) {
        try {
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(new MyBot());
                botsApi.registerBot(new MyAdminBot());
                System.out.println("Bot successfully started !!!");
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }




    }

}