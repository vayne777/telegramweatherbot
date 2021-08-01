package ru.projects.telegramweatherbot.handler;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.projects.telegramweatherbot.model.Coord;
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
public class WeatherHandler implements Handler {

    @Autowired
    StartHandler handler;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${weather.key}")
    private String apikey;

    public static final String SEND_LOCATION = "/send_location";
    public static final String GET_BALABOL = "/to_balabol_state";
    public static final String GET_WEATHER = "/get_weather";
    @Autowired
    private UserRepository userRepository;

    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, Location location){
            String lat = location.getLatitude().toString();
            String lon = location.getLongitude().toString();
            String url = String.format("http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s",lat,lon,apikey);
            ResponseEntity<Coord> responseEntity = restTemplate
                    .getForEntity(url,Coord.class);
            Coord coord = responseEntity.getBody();
            LinkedHashMap<String,Object> weatherMap = coord.getWeather().get(0);
            String name = coord.getName();
            String description = (String) weatherMap.get("description");
            LinkedHashMap<String,Object> main = coord.getMain();
            Double temperature =(Double) main.get("temp");
            Integer t = (int) (temperature - 273);

            SendMessage sendMessage = createMessageTemplate(user);
            sendMessage.setText(String.format("Местоположение: %s \r\nПогода: %s \r\nТемпература: %s °С", name, description, t));
            return List.of(sendMessage);
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        if (message.equalsIgnoreCase(SEND_LOCATION)) {
            return getLocation(user);
        }
        if (message.equalsIgnoreCase("/reset")){
            user.setBotstate(State.START);
            userRepository.save(user);
            return handler.handle(user, message);
        }
        if (message.equalsIgnoreCase(GET_BALABOL)) {
            user.setBotstate(State.BALABOL);
            userRepository.save(user);
            SendMessage sendMessage = createMessageTemplate(user);
            sendMessage.setText("Введите какой-нибудь текст");
            return List.of(sendMessage);
        } else {
            //запрос на получение погоды
            String city = message.toLowerCase();
            String url = String.format("http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s",city,apikey);
            ResponseEntity<Coord> responseEntity = null;
            try {
                responseEntity = restTemplate
                        .getForEntity(url, Coord.class);
            } catch (HttpClientErrorException e) {
                SendMessage sendMessage = createMessageTemplate(user);
                sendMessage.setText("Город не найден либо нет соединения с сервером :(");
                List<InlineKeyboardButton> list = Collections.singletonList(createInlineKeyboardButton("Отмена", "/enter_name_cancel"));
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(list));
                sendMessage.setReplyMarkup(markup);
                return List.of(sendMessage);
            }
            if (responseEntity != null && responseEntity.getBody() != null) {
                Coord coord = responseEntity.getBody();
                LinkedHashMap<String, Object> weatherMap = coord.getWeather().get(0);
                String name = coord.getName();
                String description = (String) weatherMap.get("description");
                LinkedHashMap<String, Object> main = coord.getMain();
                Double temperature = (Double) main.get("temp");
                Integer t = (int) (temperature - 273);
                SendMessage sendMessage = createMessageTemplate(user);
                sendMessage.setText(String.format("Местоположение: %s \r\nПогода: %s \r\nТемпература: %s °С", name, description, t));
                List<InlineKeyboardButton> list = Collections.singletonList(createInlineKeyboardButton("Отмена", "/enter_name_cancel"));
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(list));
                sendMessage.setReplyMarkup(markup);
                return List.of(sendMessage);
            } else
            {
                SendMessage sendMessage = createMessageTemplate(user);
                sendMessage.setText("Что-то пошло не так");
                List<InlineKeyboardButton> list = Collections.singletonList(createInlineKeyboardButton("Отмена", "/enter_name_cancel"));
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(list));
                sendMessage.setReplyMarkup(markup);
                return List.of(sendMessage);
            }

        }
    }

    private List<PartialBotApiMethod<? extends Serializable>> getLocation(User user) {
        SendMessage sendMessage = createMessageTemplate(user);
        user.setBotstate(State.NONE);
        userRepository.save(user);
        sendMessage.setText("Введите название города на латинице [a-z]");
        return List.of(sendMessage);
    }

    @Override
    public State operatedBotState() {
        return State.NONE;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(SEND_LOCATION,GET_BALABOL);
    }
}
