package org.example.adminBot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class Buttons {
public ReplyKeyboardMarkup adminMenu(){
    List<KeyboardRow> keyboardRows = new ArrayList<>();
    KeyboardRow row = new KeyboardRow();
    row.add("Create Vacancy");
    //row.add("Send to Channel");

    keyboardRows.add(row);
    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardRows);
    replyKeyboardMarkup.setResizeKeyboard(true);
    return replyKeyboardMarkup;


}

public ReplyKeyboardMarkup sendToChannel(){
    List<KeyboardRow> keyboardRows = new ArrayList<>();
    KeyboardRow row = new KeyboardRow();
    row.add("Send To Channel");
    keyboardRows.add(row);
    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardRows);
    replyKeyboardMarkup.setResizeKeyboard(true);
    return replyKeyboardMarkup;
}
}
