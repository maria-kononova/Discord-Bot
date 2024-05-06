package com.example.bot.entity;

//import jakarta.persistence.Embeddable;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Embeddable
@Table(name = "\"gif\"")
public class Gif {
    @Id
    private Long id;
    private String type;
    private String path;
}
