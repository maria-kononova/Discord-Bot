package com.example.bot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"Ticket\"")
public class Ticket {
    @Id
    private Long id; //id созданного текстового канала, который предоставляется Discord
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user; //создатель тикета
    @ManyToOne
    @JoinColumn(name = "moderator_id", referencedColumnName = "id")
    private User moderator; //модератор принявший тикет
    private Date dateCreate; //дата создания тикета
    private Date dateClose; //дата закрытия тикета, если не закрыт, значит ещё решает вопрос
    private String typeProblem; //тип проблемы (Вопрос, Нарушение, Баг)
    private String descriptionProblem; //описание проблемы
    private String solution; //описание решения проблемы


    public Ticket(Long id, User user, String typeProblem, String descriptionProblem) {
        this.id = id;
        this.user = user;
        this.moderator = null;
        this.dateCreate =  new Date();
        this.dateClose = null;
        this.typeProblem = typeProblem;
        this.descriptionProblem = descriptionProblem;
        this.solution = null;
    }
}
