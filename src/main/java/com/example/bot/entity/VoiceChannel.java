package com.example.bot.entity;

//import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"voice_channel\"")
public class VoiceChannel {
    @Id
    private Long idChannel; //id созданного голосовго канала, который предоставляется Discord
    @OneToOne
    @JoinColumn(name="idHost")
    private User host; //хост голосового канала, только он может управлять каналом
}

