package ru.projects.telegramweatherbot.model;

import java.util.LinkedHashMap;
import java.util.List;

public class Coord {
    private String id;
    private String name;
    private LinkedHashMap<String,Object> coord;
    private List<LinkedHashMap<String,Object>> weather;
    private LinkedHashMap<String,Object> main;

    public List<LinkedHashMap<String, Object>> getWeather() {
        return weather;
    }

    public void setWeather(List<LinkedHashMap<String, Object>> weather) {
        this.weather = weather;
    }

    public LinkedHashMap<String, Object> getMain() {
        return main;
    }

    public void setMain(LinkedHashMap<String, Object> main) {
        this.main = main;
    }

    public LinkedHashMap<String, Object> getCoord() {
        return coord;
    }

    public void setCoord(LinkedHashMap<String, Object> coord) {
        this.coord = coord;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
