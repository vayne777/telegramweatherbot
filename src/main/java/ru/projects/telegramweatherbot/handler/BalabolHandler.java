package ru.projects.telegramweatherbot.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.projects.telegramweatherbot.model.BalabolDto;
import ru.projects.telegramweatherbot.model.User;
import ru.projects.telegramweatherbot.repository.UserRepository;
import ru.projects.telegramweatherbot.service.Handler;
import ru.projects.telegramweatherbot.service.State;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static ru.projects.telegramweatherbot.utils.TelegramUtils.createInlineKeyboardButton;
import static ru.projects.telegramweatherbot.utils.TelegramUtils.createMessageTemplate;
@Component
public class BalabolHandler implements Handler {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    StartHandler handler;
    @Autowired
    RestTemplate restTemplate;
    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        if (message.equalsIgnoreCase("/reset")) {
            user.setBotstate(State.NONE);
            userRepository.save(user);
            return handler.handle(user, message);
        } else {
            LinkedHashMap<String,Object> body = new LinkedHashMap<>();
            body.put("filter",1);
            body.put("intro",0);
            body.put("query",message);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<LinkedHashMap> entity = new HttpEntity<>(body,headers);
            ResponseEntity<BalabolDto> response = restTemplate.postForEntity("https://zeapi.yandex.net/lab/api/yalm/text3",entity,
                    BalabolDto.class);
            String responseText = response.getBody().getText();
            SendMessage sendMessage = createMessageTemplate(user);
            sendMessage.setText(responseText);
            List<InlineKeyboardButton> list = Collections.singletonList(createInlineKeyboardButton("Отмена", "/enter_name_cancel"));
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(list));
            sendMessage.setReplyMarkup(markup);
         return List.of(sendMessage);
        }
    }

    @Override
    public State operatedBotState() {
        return State.BALABOL;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return Collections.emptyList();
    }
}
