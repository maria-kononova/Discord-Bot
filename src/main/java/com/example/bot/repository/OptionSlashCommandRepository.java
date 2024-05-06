package com.example.bot.repository;

import com.example.bot.entity.OptionSlashCommand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionSlashCommandRepository extends JpaRepository<OptionSlashCommand, Long> {
}
