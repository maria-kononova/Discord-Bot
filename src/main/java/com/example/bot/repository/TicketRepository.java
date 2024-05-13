package com.example.bot.repository;

import com.example.bot.entity.Ticket;
import com.example.bot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Ticket getTicketById(long id);
}
