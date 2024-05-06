package com.example.bot.repository;

import com.example.bot.entity.User;
import com.example.bot.entity.VoiceChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoiceChannelRepository extends JpaRepository<VoiceChannel, Long> {
    Long getIdChannelByHost(User user);
    VoiceChannel getByIdChannel(long id);
    @Modifying(clearAutomatically = true)
    @Query(value = "update VoiceChannel set id_host = ?2 where idChannel = ?1", nativeQuery = true)
    void changeIdHostChannel(Long channel, User newHost);
    List<VoiceChannel> findAll();
}
