package com.example.bot.repository;

import com.example.bot.entity.SlashCommand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlashCommandRepository extends JpaRepository<SlashCommand, Long> {
    SlashCommand getByName(String name);
}
