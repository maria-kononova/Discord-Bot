package com.example.bot.repository;

import com.example.bot.entity.Crocodile;
import com.example.bot.entity.Violation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViolationRepository extends JpaRepository<Violation, Long> {
    Violation getViolationById(Long id);
    Violation getViolationByName(String name);
}
