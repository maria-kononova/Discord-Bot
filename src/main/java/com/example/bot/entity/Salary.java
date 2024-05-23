package com.example.bot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@NoArgsConstructor
@Data
@Table(name = "\"Salary\"")
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "staff_id", referencedColumnName = "id")
    private User staff; //кому выдали
    @ManyToOne
    @JoinColumn(name = "control_id", referencedColumnName = "id")
    private User control; //кто выдал
    private String type;
    private Date dateTime;
    private int coins;
    private boolean isGiven;


    public Salary(User staff, User control, String type, int coins, boolean isGiven) {
        this.staff = staff;
        this.control = control;
        this.dateTime = new Date();
        this.type = type;
        this.coins = coins;
        this.isGiven = isGiven;
    }
}
