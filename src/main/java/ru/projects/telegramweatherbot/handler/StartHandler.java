package ru.projects.telegramweatherbot.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.projects.telegramweatherbot.model.User;
import ru.projects.telegramweatherbot.repository.UserRepository;
import ru.projects.telegramweatherbot.service.Handler;
import ru.projects.telegramweatherbot.service.State;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static ru.projects.telegramweatherbot.utils.TelegramUtils.createMessageTemplate;


@Component
public class StartHandler implements Handler {
    private UserRepository userRepository;
    @Value("${telegram.name}")
    private String botUsername;

    public StartHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        SendMessage welcomeMessage = createMessageTemplate(user);
        welcomeMessage.setText(String.format(
                        "Hola! I'm *%s*%nI am here to help you find out the weather in your region", botUsername
                ));
        // Просим назваться
        SendMessage registrationMessage = createMessageTemplate(user);
                registrationMessage.setText("Enter your name");
        // Меняем пользователю статус на - "ожидание ввода имени"
        user.setBotstate(State.ENTER_NAME);
        userRepository.save(user);
        return List.of(welcomeMessage, registrationMessage);
    }


    @Override
    public State operatedBotState() {
        return State.START;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return Collections.emptyList();
    }
}
