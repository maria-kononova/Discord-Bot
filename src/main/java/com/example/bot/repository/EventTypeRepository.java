package com.example.bot.repository;

import com.example.bot.entity.Event;
import com.example.bot.entity.EventType;
import com.example.bot.entity.Gif;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTypeRepository extends JpaRepository<EventType, Long> {
    EventType getEventTypeById(Long id);
    EventType getEventTypeByName(String name);
}
