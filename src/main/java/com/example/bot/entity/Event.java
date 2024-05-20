package com.example.bot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@NoArgsConstructor
@Data
@Table(name = "\"Event\"")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date datePlan;
    private Date dateStart;
    private Date dateEnd;
    @ManyToOne
    @JoinColumn(name = "type_id", referencedColumnName = "id")
    private EventType type; //тип ивента
    @ManyToOne
    @JoinColumn(name = "evnter_id", referencedColumnName = "id")
    private User eventer; //организатор ивента
    private int notification; //уведомления
    private boolean isPrized; //выдана ли нагарада

    public Event(Date datePlan, EventType type, User eventer) {
        this.datePlan = datePlan;
        this.type = type;
        this.eventer = eventer;
        this.notification = 1;
        this.isPrized = false;
    }
}
