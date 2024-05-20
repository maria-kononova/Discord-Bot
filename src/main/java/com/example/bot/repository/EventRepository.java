package com.example.bot.repository;

import com.example.bot.entity.Event;
import com.example.bot.entity.Gif;
import com.example.bot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    Event getEventById(Long id);

    List<Event> findEventsByEventer(User eventer);
}
