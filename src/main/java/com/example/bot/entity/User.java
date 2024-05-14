package com.example.bot.entity;

//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import org.antlr.v4.runtime.misc.Interval;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
@Data
@Table(name = "\"User\"")
public class User {
    @Id
    private Long id;
    private int coins; //заработанная пользователем валюта сервера, определённая в зёрнышках
    private int exp; //опыт пользователя, заработанный на сервере
    private Date dateEntry; //дата первого присоединения к серверу
    private boolean active; //показывает активен ли пользователь (присутствует на сервере)
    private int message;
    private int minute;

    //конструктор для создания нового пользователя, назначающий начальные данные (валюту, опыт, дату, актив)
    public User(Long id) {
        this.id = id;
        this.coins = 200;
        this.exp = 10;
        this.active = true;
        this.dateEntry = new Date();
        this.message = 0;
        this.minute = 0;
    }

    //метод получения уровня пользователя
    public int getLvl() {
        int lvl = 0;
        int nextLvl = 100;
        while (this.exp >= nextLvl) {
            lvl++;
            nextLvl = nextLvl + expToNextLvl(lvl);
        }
        return lvl;
    }

    //метод определения необходимого количества опыта для следующего уровня
    //расчитывается следующий_уровень = предыдущий_уровень * 1.5
    public int expToNextLvl(int lvl) {
        int expToNextLvl = 100;
        for (int i = 0; i < lvl; i++) {
            expToNextLvl = (int) (expToNextLvl * 1.5);
        }
        return expToNextLvl;
    }

    //метод определения необходимого количества опыта для текущего уровня пользователя
    public int expOnLvl() {
        int expOnLvl = 0;
        for (int i = 0; i < getLvl(); i++) {
            expOnLvl += expToNextLvl(i);
        }
        return this.exp - expOnLvl;
    }

    //формат 3ч. 4м.
    public String getMinuteToString() {
        if (minute < 60) return minute + "m.";
        int hour = minute / 60;
        int min = minute - hour * 60;
        return hour + "h." + min + "m.";
    }

    public void sendMsg() {
        updateCoins(1);
        updateExp(5);
        message++;
    }

    public void update(int count) {
        updateCoins(count);
        updateExp(count);
        minute++;
    }

    //метод определения рейтинга пользователя
    public int ratingUser(List<User> users, User user) {
        int min = users.size();
        long ratingVal = getRatingOfUser(user);
        List<Long> ratings = new ArrayList<>();
        for(User user_ : users){
            ratings.add(getRatingOfUser(user_));
        }
        Collections.sort(ratings);
        Collections.reverse(ratings);
        for(int i = 0; i < ratings.size(); i++ ){
            if(Objects.equals(getRatingOfUser(user), ratings.get(i))) {
                System.out.println(user.id + " "  + ratings.get(i));
                return i+1;
            }
        }
        return -1;
    }

    public Long getRatingOfUser(User user) {

        long diff = new Date().getTime() - user.getDateEntry().getTime();//as given

        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
        long minutes = (int) TimeUnit.MILLISECONDS.toMinutes(diff);

        return  user.exp + minutes;
    }

    //метод обновления валюты у пользователя
    public void updateCoins(int coins) {
        this.coins += coins;
    }

    //метод обновления опыта у пользователя
    public void updateExp(int exp) {
        this.exp += exp;
    }
}
