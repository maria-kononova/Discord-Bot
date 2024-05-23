package com.example.bot.repository;

import com.example.bot.entity.SalaryDefault;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryDefaultRepository extends JpaRepository<SalaryDefault, Long> {
    SalaryDefault getSalaryDefaultByType(String type);
}
