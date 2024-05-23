package com.example.bot.repository;

import com.example.bot.entity.Penalty;
import com.example.bot.entity.Salary;
import com.example.bot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalaryRepository extends JpaRepository<Salary, Long> {
    List<Salary> getSalariesByType(String type);
}
