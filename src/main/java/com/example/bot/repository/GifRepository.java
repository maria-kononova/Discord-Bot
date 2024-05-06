package com.example.bot.repository;

import com.example.bot.entity.Gif;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GifRepository extends JpaRepository<Gif, Long> {
    List<Gif> findAllByType(String type);
}
