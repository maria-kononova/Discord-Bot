package com.example.bot.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "slash_command_option")
public class SlashCommandOption {

    @EmbeddedId
    private SlashCommandOptionId id;

    @Column(name = "required")
    private boolean required;

}
