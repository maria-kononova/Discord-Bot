package com.example.bot.repository;

import com.example.bot.entity.Event;
import com.example.bot.entity.EventUser;
import com.example.bot.entity.EventUserId;
import com.example.bot.entity.Gif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventUserRepository extends JpaRepository<EventUser, Long> {
    EventUser findById(EventUserId eventUserId);
    @Query("SELECT eu FROM EventUser eu WHERE eu.id.event = :event")
    List<EventUser> findEventUsersByEvent(Event event);
}
