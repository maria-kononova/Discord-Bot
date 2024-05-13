package com.example.bot;

import com.example.bot.entity.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.RestAction;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.bot.BotApplication.*;
import static com.example.bot.BotApplication.guild;
import static com.example.bot.BotCommands.userRepository;

@Service
@Configuration
public class CheckServer {
    public String result;

    public CheckServer() {
    }

    //Обновление людей на сервере (добавление в бд тех, кто не добавлен)
    public void checkUserOnServer() {
        int countNewUser = 0;
        for (Member userServer : guild.getMembers()) {
            if (!userServer.getUser().isBot()) {
                if (userRepository.findById(userServer.getIdLong()).isEmpty()) {
                    User newUser = new com.example.bot.entity.User(userServer.getIdLong());
                    userRepository.save(newUser);
                    countNewUser++;
                }
            }
        }
        result = "Добавлено пользователей : " + countNewUser;
    }

    //Обновление слэш-команд на сервере
    public void updateSlashCommands() {
        int count = 0;
        if (guild != null) {
            for (SlashCommand slCmd : slashCommandRepository.findAll()) {
                if (!checkCommandOnServer(slCmd.getName())) {
                    createSlashCommand(slCmd);
                    count++;
                }
            }
            result = "Было обновлено команд: " + count;
            //guild.updateCommands().queue();
//            guild.upsertCommand("крякни", "Утёнку нужно учиться говорить").queue();
//            guild.upsertCommand("монетки", "Узнай своё состояние")
//                    .addOption(OptionType.STRING, "user", "Выбери пользователя").queue();
//            guild.upsertCommand("мяу", "Попробуй, попробуй мяукни").queue();
//            guild.upsertCommand("прогресс", "Утёнок покажет твой уровень").queue();
//            guild.upsertCommand("проверка_пользователей", "Синхронизация пользователей сервера")
//                    .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL, Permission.ADMINISTRATOR, )).queue();
            //guild.updateCommands().queue();
        }
    }

    //обновление одной команды на сервере
    public void createSlashCommand(SlashCommand slCmd) {
        List<OptionData> optionsList = new ArrayList<>();
        //добавление опций
        if (slCmd.getHasOptions()) {
            List<SlashCommandOption> slashCommandOptions = slashCommandOptionRepository.findSlashCommandOptionsBySlashCommand(slCmd);
            for (SlashCommandOption slashCommandOption : slashCommandOptions) {
                OptionSlashCommand option = slashCommandOption.getId().getOptionSlashCommand();
                OptionData optionData = new OptionData(OptionType.valueOf(option.getType()), option.getName(), option.getDescription()).setRequired(slashCommandOption.isRequired());
                optionsList.add(optionData);
            }
        }
        if (!slCmd.getPermissions().equals("ADMINISTRATOR")) {
            Command command = guild.upsertCommand(slCmd.getName(), slCmd.getDescription()).addOptions(optionsList).complete();
        } else {
            Command command = guild.upsertCommand(slCmd.getName(), slCmd.getDescription()).addOptions(optionsList).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL, Permission.ADMINISTRATOR)).complete();
        }
        //.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL, Permission.ADMINISTRATOR))
        result = "";
    }

    //проверяет, есть ли команда на сервере по её названию
    public static Boolean checkCommandOnServer(String commandName) {
        RestAction<List<Command>> commands = guild.retrieveCommands();
        AtomicBoolean foundMatch = new AtomicBoolean(false);
        commands.queue(
                list -> {
                    for (Command command : list) {
                        if (command.getName().equals(commandName)) {
                            foundMatch.set(true);
                            break;
                        }
                    }
                },
                Throwable::printStackTrace
        );
        return foundMatch.get();
    }

    //возвращает id пользователя по его имени
    public static String getIdUserOnServer(String nameUser) {
        String result = "";
        List<Member> members = guild.getMembers();
        for (Member member : members) {
            if (member.getUser().getName().equals(nameUser)) {
                result = member.getId();
                break;
            }
        }
        return result;
    }

    //возвращает id команды по её названию
    public static String getIdCommandOnServer(String commandName) throws ExecutionException, InterruptedException {
        CompletableFuture<String> result1 = new CompletableFuture<>();

        guild.retrieveCommands().queue(commands -> {
            for (Command command : commands) {
                if (command.getName().equals(commandName)) {
                    result1.complete(command.getId());
                    break;
                }
            }
        });
        return result1.get();
    }

    //синхронизация голосовых каналов с БД для исключения ошибок и проверки правильности работы бота
    public void checkVoiceChannel() {
        //result = "Ошибки в БД: " + VoiceRoomListener.checkVoiceChannelFromBD();
        result = "Ошибки в БД: " + VoiceRoomListener.checkVoiceChannelFromServer();
    }

    public void getCommandOnServer() {
        RestAction<List<Command>> commands = guild.retrieveCommands();
        commands.queue(
                list -> list.forEach(
                        command -> result += "Command: " + command.getName()
                ),
                Throwable::printStackTrace
        );
    }
}