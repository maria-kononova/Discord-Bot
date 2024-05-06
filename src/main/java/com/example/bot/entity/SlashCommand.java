package com.example.bot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "\"slash_command\"")
public class SlashCommand {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    private String name; //название команды (будет отображаться при обращении)
    private String description; //описание команды (отображается)
    //@OneToMany(mappedBy = "slash_command_id")
    private Boolean hasOptions; //Список подключённых опций к команде
    private String permissions; //определение прав доступа для данной команды
    @ManyToOne
    @JoinColumn(name="type")
    private TypeSlashCommand type;
    private String reply;

}

