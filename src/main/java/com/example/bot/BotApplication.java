package com.example.bot;

import com.example.bot.events.*;
import com.example.bot.repository.*;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class BotApplication {
    public static Guild guild;
    public static JDA bot;
    public UserRepository userRepository;
    public static SlashCommandRepository slashCommandRepository;
    public static SlashCommandOptionRepository slashCommandOptionRepository;
    public static CrocodileRepository crocodileRepository;
    public static TicketRepository ticketRepository;
    public static ResourceLoader resourceLoader;
    public static EventRepository eventRepository;
    public static EventTypeRepository eventTypeRepository;

    public static EventUserRepository eventUserRepository;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(BotApplication.class, args);
        create();
        //SystemMessage.sendMsgAboutEvent();
        //SystemMessage.sendVoiceControlMessage();
        //SystemMessage.sendCrocodileGameMessage();
        //SystemMessage.sendTicketMessage();
        //SystemMessage.sendCreateEventMsg();
        CheckServer checkServer = new CheckServer();
        checkServer.checkVoiceChannel();
        System.out.println(checkServer.result);
        //guild.updateCommands().queue();
        //ответы бота на какие-то слова в соо. пока удалено
//        List<String> command = new ArrayList<>();
//        List<String> message = new ArrayList<>();
//        for (String elem : enums.commands.TEXT_COMMAND.getTitles()) {
//            command.add(elem);
//        }
//        for (String elem : enums.commands.TEXT_MESSAGE.getTitles()) {
//            message.add(elem);
//        }
//        MessageListener messageListener = new MessageListener();
//        messageListener.values = setData(command, message);

    }

    public static HashMap<String, String> setData(List<String> command, List<String> message) {
        HashMap<String, String> values = new HashMap<>();
        for (int i = 0; i < command.size(); i++) {
            values.put(command.get(i), message.get(i));
        }
        return values;
    }

    public static void create() throws LoginException, InterruptedException {
        bot = JDABuilder.createDefault("MTE4MTMyMjcxOTU0ODE0NTY3NA.G6f0-T.EodpJURisNTEKtB-PI4vN1Vnxrxy3WHMMFngx4")
                .setActivity(Activity.competing("кряканье"))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setChunkingFilter(ChunkingFilter.ALL) // enable member chunking for all guilds
                .addEventListeners(new BotCommands(), new ButtonListener(), new DeleteMessageListener(),
                        new VoiceRoomListener(), new MessageListener(), new CrocodileGame(), new Tickets(),
                        new EventListener())
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build().awaitReady();

        guild = bot.getGuildById("1181323256612003880");

    }


    @Bean
    public CommandLineRunner update(UserRepository repository, SlashCommandRepository slashCommandRepository_,
                                    SlashCommandOptionRepository slashCommandOptionRepository_, CrocodileRepository crocodileRepository_,
                                    ResourceLoader resourceLoader_, TicketRepository ticketRepository_,
                                    EventRepository eventRepository_, EventTypeRepository eventTypeRepository_,
                                    EventUserRepository eventUserRepository_) {
        return (args) -> {
            userRepository = repository;
            slashCommandRepository = slashCommandRepository_;
            slashCommandOptionRepository = slashCommandOptionRepository_;
            crocodileRepository = crocodileRepository_;
            resourceLoader = resourceLoader_;
            ticketRepository = ticketRepository_;
            eventRepository = eventRepository_;
            eventTypeRepository = eventTypeRepository_;
            eventUserRepository = eventUserRepository_;
        };
    }
}