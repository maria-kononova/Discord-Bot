package com.example.bot.entity;

//import jakarta.persistence.Embeddable;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;

import lombok.Data;
import jakarta.persistence.*;
import java.io.Serializable;

@Embeddable
@Data
public class SlashCommandOptionId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "slash_command_id")
    private SlashCommand slashCommand;

    @ManyToOne
    @JoinColumn(name = "option_id")
    private OptionSlashCommand optionSlashCommand;

    // Equals and Hashcode for composite key
}
