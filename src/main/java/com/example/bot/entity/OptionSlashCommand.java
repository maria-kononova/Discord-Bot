package com.example.bot.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Embeddable
@Table(name = "\"option_slash_command\"")
public class OptionSlashCommand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String type; //тип опции приведённый в OptionType (понять что там)
    private String name; //название опции для добавления
    private String description; //описание опции

}
