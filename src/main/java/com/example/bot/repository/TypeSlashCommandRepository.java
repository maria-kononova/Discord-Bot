package com.example.bot.repository;

import com.example.bot.entity.TypeSlashCommand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeSlashCommandRepository extends JpaRepository<TypeSlashCommand, Long> {
}
