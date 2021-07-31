package ru.projects.telegramweatherbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.projects.telegramweatherbot.model.Coord;
import ru.projects.telegramweatherbot.model.WeatherDto;
import ru.projects.telegramweatherbot.model.WeatherEntity;

import java.net.http.HttpClient;
import java.util.LinkedHashMap;

@RestController
public class WeatherController {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${weather.key}")
    private String apikey;
    @GetMapping("/weather")
    public ResponseEntity<Coord> jsonResponse() {

        String url = "http://api.openweathermap.org/data/2.5/weather?lat=55.5&lon=37.5&appid=" + apikey;
        ResponseEntity<Coord> responseEntity = restTemplate
                .getForEntity(url,Coord.class);
        Coord coord = responseEntity.getBody();
        //Coord coord = weatherDto.getList().get(0);
        assert coord != null;
        LinkedHashMap<String,Object> map = coord.getCoord();
        Double lat = (Double) map.get("lat");
        Double lon = (Double) map.get("lon");
        LinkedHashMap<String,Object> weatherMap = coord.getWeather().get(0);
        String description = (String) weatherMap.get("description");
        LinkedHashMap<String,Object> main = coord.getMain();
        String temperature = (String) main.get("temp");
        return responseEntity;
    }


}
