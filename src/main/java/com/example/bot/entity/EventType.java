package com.example.bot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

@Entity
@NoArgsConstructor
@Data
@Table(name = "\"EventType\"")
public class EventType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private int defaultCoins;
    private int winCoins;
    private String URL;
    private String color;

    public EventType(Long id, String name, String description, int defaultCoins, int winCoins) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.defaultCoins = defaultCoins;
        this.winCoins = winCoins;
    }
}
