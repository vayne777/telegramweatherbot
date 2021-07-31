package ru.projects.telegramweatherbot.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "t_weather")
@Data
public class WeatherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String lat;
    private String lon;
    private String name;
    private String temperature;
    private Date date;

}
