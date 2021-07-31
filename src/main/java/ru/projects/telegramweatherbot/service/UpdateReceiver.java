package ru.projects.telegramweatherbot.service;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.projects.telegramweatherbot.handler.WeatherHandler;
import ru.projects.telegramweatherbot.model.Coord;
import ru.projects.telegramweatherbot.model.User;
import ru.projects.telegramweatherbot.repository.UserRepository;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
@Component
public class UpdateReceiver {
    private final List<Handler> handlers;
    private final UserRepository userRepository;
    @Autowired
    private WeatherHandler weatherHandler;

    public UpdateReceiver(List<Handler> handlers, UserRepository userRepository) {
        this.handlers = handlers;
        this.userRepository = userRepository;
    }
    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update) {
        // try-catch, чтобы при несуществующей команде просто возвращать пустой список
        try {
            // Проверяем, если Update - сообщение с текстом
            if (isMessageWithLocation(update)) {
                final Message message = update.getMessage();
                final @NonNull Long chatId = message.getFrom().getId();
                final User user = userRepository.getByChatid(chatId)
                        .orElseGet(() -> userRepository.save(new User(chatId)));
                return weatherHandler.handle(user, message.getLocation());
            }
            if (isMessageWithText(update)) {
                // Получаем Message из Update
                final Message message = update.getMessage();
                // Получаем айди чата с пользователем
                final @NonNull Long chatId = message.getFrom().getId();

                // Просим у репозитория пользователя. Если такого пользователя нет - создаем нового и возвращаем его.
                // Как раз на случай нового пользователя мы и сделали конструктор с одним параметром в классе User
                final User user = userRepository.getByChatid(chatId)
                        .orElseGet(() -> userRepository.save(new User(chatId)));
                // Ищем нужный обработчик и возвращаем результат его работы
                return getHandlerByState(user.getBotstate()).handle(user, message.getText());

            } else if (update.hasCallbackQuery()) {
                final CallbackQuery callbackQuery = update.getCallbackQuery();
                final @NonNull Long chatId = callbackQuery.getFrom().getId();
                final User user = userRepository.getByChatid(chatId)
                        .orElseGet(() -> userRepository.save(new User(chatId)));

                return getHandlerByCallBackQuery(callbackQuery.getData()).handle(user, callbackQuery.getData());
            }

            throw new UnsupportedOperationException();
        } catch (UnsupportedOperationException e) {
            return Collections.emptyList();
        }
    }
    private Handler getHandlerByState(State state) {
        return handlers.stream()
                .filter(h -> h.operatedBotState() != null)
                .filter(h -> h.operatedBotState().equals(state))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }

    private Handler getHandlerByCallBackQuery(String query) {
        return handlers.stream()
                .filter(h -> h.operatedCallBackQuery().stream()
                        .anyMatch(query::startsWith))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }

    private boolean isMessageWithText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText();
    }
    private boolean isMessageWithLocation(Update update) {
        return !update.hasCallbackQuery() && update.getMessage().getLocation() != null;
    }
}
