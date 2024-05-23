package com.example.bot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@NoArgsConstructor
@Data
@Table(name = "\"Warning\"")
public class Warning {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "moderator_id", referencedColumnName = "id")
    private User moderator; //модератор

    @ManyToOne
    @JoinColumn(name = "violator_id", referencedColumnName = "id")
    private User violator; //нарушитель

    @ManyToOne
    @JoinColumn(name = "violation_id", referencedColumnName = "id")
    private Violation violation; //тип нарушения
    private Date dateTime; //время выдачи
    private String comment;

    public Warning(User moderator, User violator, Violation violation, String comment) {
        this.moderator = moderator;
        this.violator = violator;
        this.violation = violation;
        this.dateTime = new Date();
        this.comment = comment;
    }
}
