package com.example.bot.repository;

import com.example.bot.entity.Penalty;
import com.example.bot.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepository extends JpaRepository<Roles, Long> {
    Roles getRolesById(long id);
}
