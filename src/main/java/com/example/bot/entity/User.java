package com.example.bot.entity;

//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.util.Date;

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

    //конструктор для создания нового пользователя, назначающий начальные данные (валюту, опыт, дату, актив)
    public User(Long id) {
        this.id = id;
        this.coins = 200;
        this.exp = 10;
        this.active = true;
        this.dateEntry = new Date();
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
    //метод обновления валюты у пользователя
    public void updateCoins(int coins) {
        this.coins += coins;
    }
    //метод обновления опыта у пользователя
    public void updateExp(int exp) {
        this.exp += exp;
    }
}
