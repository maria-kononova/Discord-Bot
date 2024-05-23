package com.example.bot.repository;

import com.example.bot.entity.MessageCommand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageCommandRepository extends JpaRepository<MessageCommand, Long> {
}
