package com.example.bot.repository;

import com.example.bot.entity.Crocodile;
import com.example.bot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrocodileRepository extends JpaRepository<Crocodile, Long> {
    Crocodile findByUser(User user);
}
