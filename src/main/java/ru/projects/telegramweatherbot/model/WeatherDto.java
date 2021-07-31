package ru.projects.telegramweatherbot.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class WeatherDto {
private List<Coord> list;

    public void setList(List<Coord> list) {
        this.list = list;
    }

    public List<Coord> getList() {
        return list;
    }
}
