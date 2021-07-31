package ru.projects.telegramweatherbot.service;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Component
public class MyTelegramBot extends TelegramLongPollingBot {
    private final static Logger LOGGER = LoggerFactory.getLogger(MyTelegramBot.class);
    @Value("${telegram.key}")
    private String token;
    @Value("${telegram.name}")
    private String name;
    @Autowired
    private UpdateReceiver updateReceiver;
/*
    public MyTelegramBot(UpdateReceiver updateReceiver) {
        this.updateReceiver = updateReceiver;
    }*/
    public MyTelegramBot() {
    }

    @Override
    public String getBotUsername() {
        return "CustomDemoVayneBot";
    }

    @Override
    public String getBotToken() {
        return "1933568228:AAGy5NvNh0r9i83hTBrd5b3DCtuM4lD6KH8";
    }

    @Override
    public void onUpdateReceived(Update update) {
        List<PartialBotApiMethod<? extends Serializable>> messagesToSend = updateReceiver.handle(update);
/*        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        executeWithExceptionCheck(sendMessage);*/
        if (messagesToSend != null && !messagesToSend.isEmpty()) {
            messagesToSend.forEach(response -> {
                if (response instanceof SendMessage) {
                    executeWithExceptionCheck((SendMessage) response);
                }
            });
        }
/*            sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
            Location location = update.getMessage().getLocation();
            sendMessage.setText("hi");*/
    }
    public void executeWithExceptionCheck(SendMessage sendMessage) {
        try {
/*            KeyboardButton locationButton = new KeyboardButton();
            locationButton.setRequestLocation(true);
            locationButton.setText("Узнать погоду");
            KeyboardRow keyboardRow = new KeyboardRow(Collections.singleton(locationButton));
            ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(List.of(keyboardRow));
            sendMessage.setText("hello");
            sendMessage.setReplyMarkup(markup);*/
            execute(sendMessage);
        } catch (TelegramApiException e) {
        }
    }

}
