package com.example.bot.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table(name = "\"EventUser\"")
public class EventUser {
    @EmbeddedId
    private EventUserId id;
    private int secondActive; //активных секунд
    private int secondMute; //секунд в муте
    private int prize; //получено монеток


    public EventUser(EventUserId id) {
        this.id = id;
        this.secondActive = 0;
        this.secondMute = 0;
        this.prize = 0;
    }

    public void update(boolean isMuted){
        if(isMuted) secondMute++;
        else secondActive++;
    }

}
