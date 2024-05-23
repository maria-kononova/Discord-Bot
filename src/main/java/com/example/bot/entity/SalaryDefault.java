package com.example.bot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table(name = "\"SalaryDefault\"")
public class SalaryDefault {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String type;
    private int coins;

    public SalaryDefault(Long id, String type, int coins) {
        this.id = id;
        this.type = type;
        this.coins = coins;
    }
}
