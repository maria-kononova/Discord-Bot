package com.example.bot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table(name = "\"Violation\"")
public class Violation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "penalty_id", referencedColumnName = "id")
    private Penalty penalty; //наказание


    public Violation(String name, String description, Penalty penalty) {
        this.name = name;
        this.description = description;
        this.penalty = penalty;
    }
}
