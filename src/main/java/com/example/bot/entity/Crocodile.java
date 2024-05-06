package com.example.bot.entity;

import jakarta.persistence.*;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor
@Data
@Table(name = "\"Сrocodile\"")
public class Crocodile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name = "idUser")
    private User user;

    private int score; // +5 - за угаданное слово, +10 - за загаданное, -15 за неправильную игру
    private int guessedWords; //угаданные слова
    private int setWords;  //загаданные слова
    private int penalty; //наазание


    public Crocodile(User user) {
        this.user = user;
        this.score = 0;
        this.guessedWords = 0;
        this.setWords = 0;
        this.penalty = 0;
    }
    //отгадал слово
    public void guessWord(){
        guessedWords++;
        score = score + 5;
        if(penalty != 0) penalty--;
    }
    //загадал слово
    public void setWord(){
        setWords++;
        score = score + 10;
    }
    //
    public void fine(){
        score = score - 50;
        penalty = penalty + 3;
    }

}
