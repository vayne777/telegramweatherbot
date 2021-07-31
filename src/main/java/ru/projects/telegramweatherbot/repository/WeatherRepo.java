package ru.projects.telegramweatherbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.projects.telegramweatherbot.model.WeatherEntity;

public interface WeatherRepo extends JpaRepository<WeatherEntity, Long> {
}
