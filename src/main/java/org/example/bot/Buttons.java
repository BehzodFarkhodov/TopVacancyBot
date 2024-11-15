package org.example.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class Buttons {
    public   ReplyKeyboardMarkup requestContact() {
        KeyboardButton button = new KeyboardButton("\uD83D\uDCDEShare Your number");
        button.setRequestContact(true);

        ReplyKeyboardMarkup replyKeyboardMarkup =
                new ReplyKeyboardMarkup(List.of(new KeyboardRow(List.of(button))));
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public  ReplyKeyboardMarkup requestLocation() {
        KeyboardButton button = new KeyboardButton("\uD83D\uDCCDShare your location");
        button.setRequestLocation(true);
        ReplyKeyboardMarkup replyKeyboardMarkup =
                new ReplyKeyboardMarkup(List.of(new KeyboardRow(List.of(button))));
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }



    public ReplyKeyboardMarkup getUserMenu(){
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("See Vacancies from regions 📍");
        row.add("Create own vacancy 📂");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Selected vacancies 💼");
        row2.add("Card number for Donate 😊 💲");

        KeyboardRow row3 = new KeyboardRow();


        row3.add("Join Channel 🔔");
        row3.add("Back");



        KeyboardRow row1 = new KeyboardRow();
        row1.add("Create Resume 📜");
        row1.add("Download All Vacancy 📦");


        keyboardRows.add(row);
        keyboardRows.add(row1);
        keyboardRows.add(row2);
        keyboardRows.add(row3);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardRows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getVacancyList() {
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Toshkent shahri");
        row1.add("Qashqadaryo vil");
        row1.add("Andijon vil");


        KeyboardRow row2 = new KeyboardRow();
        row2.add("Farg'ona");
        row2.add("Namangan");
        row2.add("Xorazm");


        KeyboardRow row3 = new KeyboardRow();
        row3.add("Namangan");
        row3.add("Samarqand");
        row3.add("Qoraqalpog'iston");

        KeyboardRow row4 = new KeyboardRow();
        row4.add("Back");

        keyboardRows.add(row1);
        keyboardRows.add(row2);
        keyboardRows.add(row3);
        keyboardRows.add(row4);


        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardRows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }


//    public ReplyKeyboardMarkup getResumeMenu(){
////        List<KeyboardRow> keyboardRows = new ArrayList<>();
////        KeyboardRow row = new KeyboardRow();
////        row.add("df");
//    }


    public InlineKeyboardMarkup getRequestButton(String callBackData){
        InlineKeyboardButton button = new InlineKeyboardButton("Add Apply");
        button.setCallbackData(callBackData);

        List<InlineKeyboardButton> keyboardButtons = List.of(button);
        List<List<InlineKeyboardButton>> keyBoard = List.of(keyboardButtons);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyBoard);
        return inlineKeyboardMarkup;
    }



}
