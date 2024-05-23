package com.example.bot.repository;

import com.example.bot.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarningRepository extends JpaRepository<Warning, Long> {
    List<Warning> findWarningsByModerator(User moderator);
    List<Warning> findWarningsByViolatorAndViolation(User violator, Violation violation);
}
