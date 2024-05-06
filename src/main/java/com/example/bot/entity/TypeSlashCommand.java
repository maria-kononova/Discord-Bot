package com.example.bot.entity;

//import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@NoArgsConstructor
@Data
@Table(name = "\"TypeSlashCommand\"")
public class TypeSlashCommand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name; //название типа команды
    private String description; //описание типа
}
