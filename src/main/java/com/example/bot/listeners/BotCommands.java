package com.example.bot.listeners;

import com.example.bot.BotApplication;
import com.example.bot.ImageGeneric;
import com.example.bot.entity.*;
import com.example.bot.repository.GifRepository;
import com.example.bot.repository.UserRepository;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.stage.update.StageInstanceUpdatePrivacyLevelEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static com.example.bot.BotApplication.*;
import static com.example.bot.listeners.ControlListener.*;


@Service
public class BotCommands extends ListenerAdapter {
    public static UserRepository userRepository;
    public static GifRepository gifRepository;

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

    @Override
    public void onGenericCommandInteraction(GenericCommandInteractionEvent event) {
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
                        event.getHook().sendMessage("У тебя " + user.getLvl() + " уровень " + user.expOnLvl() + " / " + user.expToNextLvl(user.getLvl())).setEphemeral(true).queue();
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
                            ImageGeneric imageGeneric = new ImageGeneric();
                            BufferedImage image = imageGeneric.genericImage(user.getId());
                            //изменить название файла для одного пользователя
                            File outputfile = new File("image.png");
                            ImageIO.write(image, "png", outputfile);
                            FileUpload fileUpload = FileUpload.fromData(outputfile);
                            event.getHook().sendMessage("").addFiles(fileUpload).queue();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    case ("тайм-аут"): {
                        String type = (Objects.requireNonNull(event.getOption("тип"))).getAsString();
                        String comment = (Objects.requireNonNull(event.getOption("комментарий"))).getAsString();
                        Member tagUserId = guild.getMemberById(Objects.requireNonNull(event.getOption("пользователь")).getAsUser().getIdLong());
                        Violation violation = null;
                        for (Violation v : violationRepository.findAll()) {
                            if (v.getName().equalsIgnoreCase(type)) {
                                violation = v;
                                break;
                            }
                        }
                        if (violation != null) {
                            try {
                                ModerationListener.warningUser(tagUserId, event.getMember(), violation, comment);
                            } catch (ExecutionException e) {
                                event.deferReply(true).setContent("Что-то пошло не так, попробуй позже или используй слэш-комнады.").queue();
                            } catch (InterruptedException e) {
                                event.deferReply(true).setContent("Что-то пошло не так, попробуй позже или используй слэш-комнады.").queue();
                            }
                            event.deferReply(true).setContent("Нарушение пользователя успешно зафиксированно.").queue();
                        } else {
                            //selectmenu
                            event.deferReply(true).setContent("Утёнок не нашёл такого нарушения. Загляни ещё раз разок в информацию для модераторов.")
                                    .addActionRow(ModerationListener.getSelectionMenuOfViolation())
                                    .queue();
                        }
                        break;
                    }

                    case ("предупреждение-стафф"): {
                        String comment = (Objects.requireNonNull(event.getOption("комментарий"))).getAsString();
                        Member tagUserId = guild.getMemberById(Objects.requireNonNull(event.getOption("пользователь")).getAsUser().getIdLong());
                        if (ControlListener.getRoleStaffFromControl(event.getMember()) == ControlListener.getRoleMemberStaff(tagUserId)) {
                            Violation violation = violationRepository.getViolationByName("Предупреждение стаффа");
                                ModerationListener.warningUserStaff(tagUserId, event.getMember(), violation, comment);
                            event.deferReply(true).setContent("Пользователю успешно выдано предупреждение.").queue();

                        } else
                            event.deferReply(true).setContent("Данный пользовтель не является частью стаффа или у него нет роли " + ControlListener.getRoleStaffFromControl(event.getMember()).getAsMention()).queue();
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
                        } else event.deferReply(true).setContent("Утёнок не нашёл такой команды в базе.").queue();
                        break;
                    }
                    case ("удаление_команды"): {
                        String nameCmd = (Objects.requireNonNull(event.getOption("команда"))).getAsString();
                        if (slashCommandRepository.getByName(nameCmd) != null) {
                            SlashCommand slCmd = slashCommandRepository.getByName(nameCmd);
                            try {
                                checkServer.deleteSlashCommand(slCmd);
                                event.deferReply(true).setContent(slashCommand.getReply() + "\n" + checkServer.result).queue();
                            } catch (ExecutionException e) {
                                event.deferReply(true).setContent("Что-то пошло не так.").queue();
                            } catch (InterruptedException e) {
                                event.deferReply(true).setContent("Что-то пошло не так.").queue();
                            }
                        } else event.deferReply(true).setContent("Утёнок не нашёл такой команды.").queue();
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
                    case ("баннер_ивента"): {
                        event.deferReply().queue();
                        if (event.getOption("ивент", OptionMapping::getAsString) != null) {
                            EventType ev = eventTypeRepository.getEventTypeByName(event.getOption("ивент", OptionMapping::getAsString));
                            if (ev != null) {
                                try {
                                    ImageGeneric imageGeneric = new ImageGeneric();
                                    BufferedImage image = imageGeneric.genericImageEvent(ev);
                                    File outputfile = new File("image.png");
                                    ImageIO.write(image, "png", outputfile);
                                    FileUpload fileUpload = FileUpload.fromData(outputfile);
                                    event.getHook().sendMessage("").addFiles(fileUpload).queue();
                                    //event.getHook().sendMessage("").addFiles().setEphemeral(true).queue();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            } else event.deferReply(true).setContent("Ивент не найден.").queue();
                        }
                        break;
                    }
                }
            }
            default:
                event.deferReply(true).setContent("Работаем над этим вопросом").setEphemeral(true).queue();
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
