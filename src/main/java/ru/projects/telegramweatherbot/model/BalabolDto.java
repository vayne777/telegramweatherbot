package ru.projects.telegramweatherbot.model;

import lombok.Data;

@Data
public class BalabolDto {
    private String text;
    private String query;
    private String error;
    private String bad_query;
}
