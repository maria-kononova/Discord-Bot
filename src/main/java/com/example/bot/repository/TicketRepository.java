package com.example.bot.repository;

import com.example.bot.entity.Ticket;
import com.example.bot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Ticket getTicketById(long id);

    List<Ticket> findTicketsByModerator(User moderator);
}
