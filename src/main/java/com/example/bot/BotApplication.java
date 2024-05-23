package com.example.bot;

import com.example.bot.entity.Penalty;
import com.example.bot.entity.Warning;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public static ViolationRepository violationRepository;
    public static WarningRepository warningRepository;
    public static PenaltyRepository penaltyRepository;
    public static SalaryRepository salaryRepository;
    public static SalaryDefaultRepository salaryDefaultRepository;
    public static MessageCommandRepository messageCommandRepository;
    public static RolesRepository rolesRepository;
    public static ShopRepository shopRepository;


    public static void main(String[] args) throws Exception {
        SpringApplication.run(BotApplication.class, args);
        guild = bot.getGuildById("1181323256612003880");
        //BotConfiguration botConfiguration = new BotConfiguration();
        //SystemMessage.sendMsgAboutEvent();
        //SystemMessage.sendVoiceControlMessage();
        //SystemMessage.sendCrocodileGameMessage();
        //SystemMessage.sendTicketMessage();
        //SystemMessage.sendCreateEventMsg();
        //SystemMessage.sendMsgRole();
        //CheckServer checkServer = new CheckServer();
        //checkServer.checkVoiceChannel();
        //System.out.println(checkServer.result);
        //guild.updateCommands().queue();
        //ответы бота на какие-то слова в соо. пока удалено

    }

    public static HashMap<String, String> setData(List<String> command, List<String> message) {
        HashMap<String, String> values = new HashMap<>();
        for (int i = 0; i < command.size(); i++) {
            values.put(command.get(i), message.get(i));
        }
        return values;
    }

    @Bean
    public CommandLineRunner update(UserRepository repository, SlashCommandRepository slashCommandRepository_,
                                    SlashCommandOptionRepository slashCommandOptionRepository_, CrocodileRepository crocodileRepository_,
                                    ResourceLoader resourceLoader_, TicketRepository ticketRepository_,
                                    EventRepository eventRepository_, EventTypeRepository eventTypeRepository_,
                                    EventUserRepository eventUserRepository_, ViolationRepository violationRepository_,
                                    WarningRepository warningRepository_, PenaltyRepository penaltyRepository_,
                                    SalaryRepository salaryRepository_, SalaryDefaultRepository salaryDefaultRepository_,
                                    MessageCommandRepository messageCommandRepository_, RolesRepository rolesRepository_,
                                    ShopRepository shopRepository_) {
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
            violationRepository = violationRepository_;
            penaltyRepository = penaltyRepository_;
            warningRepository = warningRepository_;
            salaryRepository = salaryRepository_;
            salaryDefaultRepository = salaryDefaultRepository_;
            messageCommandRepository = messageCommandRepository_;
            rolesRepository = rolesRepository_;
            shopRepository = shopRepository_;
        };
    }
}