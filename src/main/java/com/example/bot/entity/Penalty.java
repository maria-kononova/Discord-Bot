package com.example.bot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table(name = "\"Penalty\"")
public class Penalty {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String type; //мут, варн, бан
    private int time; //в минутах
    private int fineValue; //оценка нарушения


    public Penalty(String type, int time, int fineValue) {
        this.type = type;
        this.time = time;
        this.fineValue = fineValue;
    }
}
