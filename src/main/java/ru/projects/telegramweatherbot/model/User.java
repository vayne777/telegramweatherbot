package ru.projects.telegramweatherbot.model;

import lombok.Data;
import ru.projects.telegramweatherbot.service.State;

import javax.persistence.*;

@Entity
@Table(name = "t_user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long chatid;
    private String name;
    private State botstate;
    public User() {

    }

    public User(Long chatid) {
        this.chatid = chatid;
        this.name = String.valueOf(chatid);
        this.botstate = State.START;
    }

}
