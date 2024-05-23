package com.example.bot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table(name = "\"Roles\"")
public class Roles {
    @Id
    private Long id; //с сервера
    private String name;
    private String color;
    private String description;
    private int coins;
    private String animationIcon;

    public Roles(Long id, String name, String color, String description, int coins) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.description = description;
        this.coins = coins;
    }
}
