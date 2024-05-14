package com.example.bot;

import com.example.bot.entity.*;
import com.example.bot.repository.GifRepository;
import com.example.bot.repository.UserRepository;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.example.bot.BotApplication.*;


@Service
public class BotCommands extends ListenerAdapter {
    //UserRepository userRepository = context.getBean(UserRepository.class);
    public static UserRepository userRepository;
    public static GifRepository gifRepository;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        SlashCommand slashCommand = slashCommandRepository.getByName(event.getName());
        switch (slashCommand.getType().getName()) {
            case ("Взаимодействие"): {
                event.deferReply().queue();
                long tagUserId = Objects.requireNonNull(event.getOption("пользователь")).getAsUser().getIdLong();
                long user = Objects.requireNonNull(event.getMember()).getIdLong();
                List<Gif> gif = gifRepository.findAllByType(slashCommand.getName());
                if (gif.size() != 0) {
                    //добавление gif
                    Random random = new Random();
                    File file = new File(gif.get(random.nextInt(0, gif.size())).getName());
                    FileUpload fileUpload = FileUpload.fromData(file);
                    event.getHook().sendMessage("<@" + user + "> " + slashCommand.getReply() + " <@" + tagUserId + ">").addFiles(fileUpload).setEphemeral(true).queue();
                } else {
                    event.getHook().sendMessage("<@" + user + "> " + slashCommand.getReply() + " <@" + tagUserId + ">").setEphemeral(true).queue();
                }
                break;
            }
            case ("Функциональная"): {
                switch (slashCommand.getName()) {
                    case ("монетки"): {
                        event.deferReply().queue();
                        if (event.getOption("пользователь", OptionMapping::getAsUser) == null) {
                            User user = userRepository.getUserById(Objects.requireNonNull(event.getMember()).getIdLong());
                            if (user.getId() == 839545273344851990L) {
                                event.getHook().sendMessage("У тебя ровно мильон").setEphemeral(true).queue();
                            } else {
                                event.getHook().sendMessage("У тебя ровно " + user.getCoins() + " монеток").setEphemeral(true).queue();
                            }
                        } else {
                            long tagUserId = (Objects.requireNonNull(event.getOption("пользователь"))).getAsUser().getIdLong();
                            User user = userRepository.getUserById(tagUserId);
                            if (user.getId() == 839545273344851990L) {
                                event.getHook().sendMessage("У <@" + tagUserId + "> ровно мильон. Прошу завидовать!").setEphemeral(true).queue();
                            } else
                                event.getHook().sendMessage("У <@" + tagUserId + "> ровно " + user.getCoins() + " монеток. Прошу не завидовать!").setEphemeral(true).queue();
                        }
                        break;
                    }
                    case ("прогресс"): {
                        event.deferReply().queue();
                        User user = userRepository.getUserById(Objects.requireNonNull(event.getMember()).getIdLong());
                        event.getHook().sendMessage("У тебя " + user.getLvl() + " лвл " + user.expOnLvl() + " / " + user.expToNextLvl(user.getLvl())).setEphemeral(true).queue();
                        break;
                    }
                    case ("профиль"): {
                        User user;
                        event.deferReply().queue();
                        if (event.getOption("пользователь", OptionMapping::getAsUser) == null) {
                            //получение своего профиля
                            user = userRepository.getUserById(Objects.requireNonNull(event.getMember()).getIdLong());
                        } else {
                            //получение профиль выбранного пользователя
                            long tagUserId = (Objects.requireNonNull(event.getOption("пользователь"))).getAsUser().getIdLong();
                            user = userRepository.getUserById(tagUserId);
                        }
                        TextChannel channel = (TextChannel) event.getChannel();
                        try {
                            /*BufferedImage image = new BufferedImage(600, 300, BufferedImage.TYPE_INT_ARGB);
                            Graphics2D graphics = image.createGraphics();
                            graphics.setColor(Color.BLACK);
                            graphics.fillRect(0, 0, 600, 300);
                            graphics.setColor(Color.WHITE);
                            graphics.drawString(user.getId().toString(), 150, 100);
                            graphics.dispose();*/
                            ImageGeneric imageGeneric = new ImageGeneric();
                            BufferedImage image = imageGeneric.genericImage(user.getId());
                            //изменить название файла для одного пользователя
                            File outputfile = new File("image.png");
                            ImageIO.write(image, "png", outputfile);
                            FileUpload fileUpload = FileUpload.fromData(outputfile);
                            event.getHook().sendMessage("").addFiles(fileUpload).queue();
                            //event.getHook().sendMessage("").addFiles().setEphemeral(true).queue();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
            case ("Системная"): {
                CheckServer checkServer = new CheckServer();
                switch (slashCommand.getName()) {
                    case ("проверка_пользователей"): {
                        checkServer.checkUserOnServer();
                        event.deferReply(true).setContent(slashCommand.getReply() + "\n" + checkServer.result).queue();
                        break;
                    }
                    case ("обновление_команд"): {
                        checkServer.updateSlashCommands();
                        event.deferReply(true).setContent(slashCommand.getReply() + "\n" + checkServer.result).queue();
                        break;
                    }
                    case ("создание_команды"): {
                        String nameCmd = (Objects.requireNonNull(event.getOption("команда"))).getAsString();
                        if (slashCommandRepository.getByName(nameCmd) != null) {
                            SlashCommand slCmd = slashCommandRepository.getByName(nameCmd);
                            checkServer.createSlashCommand(slCmd);
                            event.deferReply(true).setContent(slashCommand.getReply() + "\n" + checkServer.result).queue();
                        }
                        break;
                    }
                    case ("обновление_войсов"): {
                        checkServer.checkVoiceChannel();
                        event.deferReply(true).setContent(slashCommand.getReply() + "\n" + checkServer.result).queue();
                        break;
                    }
                    //лучше не использовать
                    case ("отчистить_чат"): {
                        if (event.getOption("код").getAsString().equals("кря")) {
                            TextChannel textChannel = (TextChannel) event.getChannel();
                            int countMessage = 0;
                            List<Message> messages = new ArrayList<>();
                            MessageHistory history = textChannel.getHistory();
                            messages.addAll(history.retrievePast(100).complete()); // Загрузите первые 100 сообщений

                            for (Message message : messages) {
                                message.delete().queue(); // Удалите каждое сообщение
                                countMessage++;
                            }

                            event.deferReply(true).setContent(slashCommand.getReply() + "\nБыло удалено сообщений: " + countMessage).queue();
                        } else {
                            event.deferReply(true).setContent("Код неверный").queue();
                        }
                        break;
                    }
                }
            }
            default:
                event.deferReply(true).setContent("Работаем над этим вопросом").setEphemeral(true).queue();
        }
        /*if (event.getOption("user", OptionMapping::getAsUser) != null) {
            event.deferReply().queue();
            long tagUserId = Objects.requireNonNull(event.getOption("user")).getAsUser().getIdLong();
            long user = Objects.requireNonNull(event.getMember()).getIdLong();
            event.getHook().sendMessage("<@" + user + "> укусил " + "<@" + tagUserId + ">" ).setEphemeral(true).queue();
        } else {
            event.deferReply(true).setContent("Необходимо выбрать кого кусать").setEphemeral(true).queue();
            //event.getHook().sendMessage("Необходимо выбрать кого кусать").setEphemeral(true).queue();
            // event.deferReply(true).setContent("Необходимо выбрать кого кусать").queue();
        }*/

       /*switch (event.getName()) {
            case ("крякни"): {
                event.deferReply().queue();
                event.getHook().sendMessage("КРЯ").setEphemeral(true).queue();
                break;
            }
            case ("монетки"): {
                event.deferReply().queue();
                if (event.getOption("user", OptionMapping::getAsString) == null) {
                    User user = userRepository.getUserById(Objects.requireNonNull(event.getMember()).getIdLong());
                    event.getHook().sendMessage("У тебя ровно " + user.getCoins() + " монеток").setEphemeral(true).queue();
                } else {
                    long tagUserId = getId((Objects.requireNonNull(event.getOption("user"))).getAsString());
                    User user = userRepository.getUserById(tagUserId);
                    event.getHook().sendMessage("У <@" + tagUserId + "> ровно " + user.getCoins() + " монеток. Прошу не завидовать!").setEphemeral(true).queue();
                }
                break;
            }
            case ("мяу"): {
                event.deferReply().queue();
                event.getHook().sendMessage("Виу-Виу!\n<@&1181323632639754330>, Кофка раскрыта, ловите её!! ").setEphemeral(true).queue();
                break;
            }
            case ("прогресс"): {
                event.deferReply().queue();
                User user = userRepository.getUserById(Objects.requireNonNull(event.getMember()).getIdLong());
                event.getHook().sendMessage("У тебя " + user.getLvl() + " лвл " + user.expOnLvl() + " / " + user.expToNextLvl(user.getLvl())).setEphemeral(true).queue();
                break;
            }
        }*/
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        changeUserActive(Objects.requireNonNull(event.getMember()).getIdLong(), false);
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        guild = event.getGuild();
        net.dv8tion.jda.api.entities.User user = event.getUser();
        BotApplication.bot = event.getJDA();

        if (checkUser(user.getIdLong())) {
            User newUser = new User(user.getIdLong());
            userRepository.save(newUser);
            TextChannel chatChannel = event.getJDA().getTextChannelById("1181324745212436583");
            if (chatChannel != null) {
                chatChannel.sendMessage("Кря-кря!!\n<@" + String.valueOf(user.getId()) + ">, с вылуплением на нашем сервере!").queue();
            }
        } else {
            changeUserActive(event.getMember().getIdLong(), true);
            TextChannel chatChannel = event.getJDA().getTextChannelById("1181324745212436583");
            if (chatChannel != null) {
                chatChannel.sendMessage("Что ж\n<@" + String.valueOf(user.getId()) + ">, с возвращением!").queue();
            }
        }
    }

    //проверка записи пользователя
    public boolean checkUser(Long id) {
        for (User user : userRepository.findAll()) {
            if (Objects.equals(user.getId(), id)) return false;
        }
        return true;
    }

    //изменение статуса актиности пользователя
    public void changeUserActive(Long id, Boolean status) {
        User user = userRepository.getUserById(id);
        System.out.println(user.getId());
        user.setActive(status);
        userRepository.save(user);
    }

    public long getId(String tag) {
        return Long.parseLong(tag.split("@")[1].split(">")[0]);
    }

    @Bean
    public CommandLineRunner demo(UserRepository repository, GifRepository gifRepository_) {
        return (args) -> {
            userRepository = repository;
            gifRepository = gifRepository_;
        };
    }
}
