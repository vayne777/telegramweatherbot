package ru.projects.telegramweatherbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.projects.telegramweatherbot.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> getByChatid(Long chatId);
}
