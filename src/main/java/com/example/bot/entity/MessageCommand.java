package com.example.bot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table(name = "\"MessageCommand\"")
public class MessageCommand {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String commands; //на которые нужно ответить через ";" все возможные формы
    private String message;
    private String noAnswer; //если есть, то не нужно овтечать, через ";" все возможные формы

    public MessageCommand(String command, String message, String noAnswer) {
        this.commands = command;
        this.message = message;
        this.noAnswer = noAnswer;
    }

    public String check(String cmd) {
        String[] commands = getCommands().split(";");
        if (getNoAnswer() != null) {
            String[] noAnswers = getNoAnswer().split(";");
            for (String noAnswer : noAnswers) {
                if (cmd.contains(noAnswer)) return null;
            }
        }
        for (String command : commands) {
            if (cmd.contains(command)) return message;
        }
        return null;
    }
}
