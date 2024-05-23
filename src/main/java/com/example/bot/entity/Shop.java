package com.example.bot.entity;

import com.example.bot.repository.UserRepository;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static com.example.bot.listeners.BotCommands.userRepository;

@Entity
@NoArgsConstructor
@Data
@Table(name = "\"Shop\"")
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "buyer_id", referencedColumnName = "id")
    private User buyer; //покупатель
    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Roles role; //роль
    private Date dateTime;

    public Shop(User buyer, Roles role) {
        this.buyer = buyer;
        this.role = role;
        this.dateTime = new Date();
    }
}
