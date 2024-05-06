package com.example.bot.repository;

import com.example.bot.entity.SlashCommand;
import com.example.bot.entity.SlashCommandOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SlashCommandOptionRepository extends JpaRepository<SlashCommandOption, Long> {

    @Query("SELECT sco FROM SlashCommandOption sco WHERE sco.id.slashCommand = :slashCommand")
    List<SlashCommandOption> findSlashCommandOptionsBySlashCommand(@Param("slashCommand") SlashCommand slashCommand);
}
