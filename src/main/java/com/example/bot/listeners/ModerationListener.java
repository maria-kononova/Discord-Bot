package com.example.bot.listeners;

import com.example.bot.SystemMessage;
import com.example.bot.entity.EventType;
import com.example.bot.entity.User;
import com.example.bot.entity.Violation;
import com.example.bot.entity.Warning;
import jakarta.validation.Validator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.internal.entities.UserImpl;
import net.dv8tion.jda.internal.interactions.component.StringSelectMenuImpl;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.example.bot.BotApplication.*;
import static com.example.bot.listeners.BotCommands.userRepository;
import static com.example.bot.listeners.ControlListener.*;


public class ModerationListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getButton().getId().contains("moderation6")) {
            switch (event.getButton().getId()) {
                case ("warning-moderation6"): {
                    SelectMenu selectMenu = EntitySelectMenu.create("select-user-warn-moderation6", EntitySelectMenu.SelectTarget.USER)
                            .setPlaceholder("Кто себя плохо ведёт?")
                            .setRequiredRange(1, 1)
                            .build();
                    event.deferReply(true).setContent("Выбери нарушителя правил").setComponents(ActionRow.of(selectMenu)).queue();
                    break;
                }
                case ("info-moderation6"): {
                    event.deferReply(true).setContent("Тип нарушения:").setComponents(ActionRow.of(getSelectionMenuOfViolation())).queue();
                }
                default:
                    event.deferReply(true).setContent("Работаем над этим вопросом!").queue();
            }
        }
    }

    public static StringSelectMenuImpl getSelectionMenuOfViolation(){
        List<SelectOption> selectOptions = new ArrayList<>();

        for (Violation violation : violationRepository.findAll()) {
            if (!violation.getName().equals("Предупреждение стаффа")) {
                selectOptions.add(SelectOption.of(violation.getName(), String.valueOf(violation.getId())));
            }
        }
        return new StringSelectMenuImpl("type-select-moderation6", "Выбери тип нарушения", 1, 1, false, selectOptions);
    }


    @Override
    public void onGenericSelectMenuInteraction(GenericSelectMenuInteractionEvent event) {
        if (event.getComponent().getId().contains("moderation6")) {
            switch (event.getComponent().getId()) {
                case ("select-user-warn-moderation6"): {
                    List values = event.getValues();
                    Member member = (Member) values.get(0);
                    if (!member.getUser().isBot()) {
                        TextInput violation = TextInput.create("violation-warn-moderation6", "Зафиксированное нарушение", TextInputStyle.SHORT)
                                .setMinLength(1)
                                .setMaxLength(50)
                                .setPlaceholder("Нарушение")
                                .build();

                        TextInput comment = TextInput.create("comment-moderation6", "Комментарий", TextInputStyle.PARAGRAPH)
                                .setMinLength(1)
                                .setMaxLength(500)
                                .setPlaceholder("Описание ситуации")
                                .build();

                        Modal modal = Modal.create("warning-modal6-" + member.getId(), "Нарушение " + member.getEffectiveName())
                                .addActionRow(violation).addActionRow(comment).build();
                        event.replyModal(modal).queue();
                    } else
                        event.deferReply(true).setContent("Утёнок такого не прощает... <a:57de398324336d3b64953eefd50a9398:1238969992884523080>").queue();
                    break;
                }
                case ("type-select-moderation6"): {
                    String value = (String) event.getValues().get(0); //id выбранного ивента
                    Violation violation = violationRepository.getViolationById(Long.valueOf(value));
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setColor(Color.orange);
                    embedBuilder.setThumbnail("https://psv4.userapi.com/c237131/u572432973/docs/d23/c7eb5d26a3cd/moderation.gif?extra=lFq6bLI8SNDETb9yjAK7rnVK6Ye8zZvULqYhdwgHRSEbHMjegPMwab7Qn3bVY4IBlX3CnRWApZh8fcY4asekrD0pVjip-4ZCz9Tv3t854FSQGW4CinpDxCDWAF_Y9nYd1vIDzIAw7uxcGaEfom_JP9Hp");
                    embedBuilder.setTitle("«" + violation.getName() + "»");
                    embedBuilder.setDescription(violation.getDescription());
                    embedBuilder.addField("Наказание:", "> " + violation.getPenalty().getType() + " на " + violation.getPenalty().getTime() + " мин.", true);
                    embedBuilder.addField("Штрафные очки:", "> " + String.valueOf(violation.getPenalty().getFineValue()), true);
                    event.deferEdit().setEmbeds(embedBuilder.build()).queue();
                }
                default:
                    event.deferReply(true).setContent("Работаем над этим вопросом!").queue();
            }
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().contains("modal6")) {
            if (event.getModalId().contains("warning-modal6")) {
                Member userWarn = guild.getMemberById(event.getModalId().split("-")[2]);
                ModalMapping violationValue = event.getValue("violation-warn-moderation6");
                ModalMapping commentValue = event.getValue("comment-moderation6");
                String violationString = violationValue.getAsString();
                String commentString = commentValue.getAsString();
                Violation violation = null;
                for (Violation v : violationRepository.findAll()) {
                    if (v.getName().equalsIgnoreCase(violationString)) {
                        violation = v;
                        break;
                    }
                }
                if (violation != null) {
                    try {
                        warningUser(userWarn, event.getMember(), violation, commentString);
                    } catch (ExecutionException e) {
                        event.deferReply(true).setContent("Что-то пошло не так, попробуй позже или используй слэш-комнады.").queue();
                    } catch (InterruptedException e) {
                        event.deferReply(true).setContent("Что-то пошло не так, попробуй позже или используй слэш-комнады.").queue();
                    }
                    event.deferReply(true).setContent("Нарушение пользователя успешно зафиксированно").queue();
                } else {

                    event.deferReply(true).setContent("Утёнок не нашёл такого нарушения. Загляни ещё раз разок в информацию для модераторов.")
                            .addActionRow(getSelectionMenuOfViolation())
                            .queue();
                }
               /* break;
            }
            default:
                event.deferReply(true).setContent("Работаем над этим вопросом!").queue();*/
            }
        }

    }

    public static void warningUser(Member user, Member moderator, Violation violation, String comment) throws ExecutionException, InterruptedException {
        Warning warn = new Warning(userRepository.getUserById(moderator.getIdLong()), userRepository.getUserById(user.getIdLong()), violation, comment);
        warningRepository.save(warn);
        guild.timeoutFor(user, warn.getViolation().getPenalty().getTime(), TimeUnit.MINUTES).queue();
        // Открыть частный канал
        PrivateChannel channel = user.getUser().openPrivateChannel().complete();
        channel.sendMessage("Утёнок сообщает о выданном нарушении!").setEmbeds(getMsgWarn(warn, moderator, false).build()).queue();
        SystemMessage.sendReportModeration(getMsgWarn(warn, moderator, true));
    }

    public static void warningUserStaff(Member user, Member moderator, Violation violation, String comment) {
        Warning warn = new Warning(userRepository.getUserById(moderator.getIdLong()), userRepository.getUserById(user.getIdLong()), violation, comment);
        warningRepository.save(warn);
        // Открыть частный канал
        PrivateChannel channel = user.getUser().openPrivateChannel().complete();
        channel.sendMessage("Утёнок сообщает о выданном предупреждении!").setEmbeds(getMsgWarn(warn, moderator, true).build()).queue();
        SystemMessage.sendReportWarnControl(getMsgWarn(warn, moderator, true));
    }

    public static EmbedBuilder getMsgWarn(Warning warn, Member moderator, boolean isReport) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.orange);
        embedBuilder.setTitle("Зафиксировано нарушение!");
        embedBuilder.setDescription("<t:" + warn.getDateTime().getTime() / 1000 + ":f>");
        embedBuilder.addField("Тип нарушения:", "> " + warn.getViolation().getName(), true);
        String warnString = "";
        if (warn.getViolation().getPenalty().getTime() != 0)
            warnString = "> " + warn.getViolation().getPenalty().getType() + " на " + warn.getViolation().getPenalty().getTime() + " мин.";
        else
            warnString = "> " + warningRepository.findWarningsByViolatorAndViolation(warn.getViolator(), warn.getViolation()).size() + " / 3";
        embedBuilder.addField("Наказание:", warnString, true);
        embedBuilder.addField("Комментарий:", "> " + warn.getComment(), false);
        String who = "Модератор";
        if (warn.getViolation().getName().equals("Предупреждение стаффа")) who = "Куратор";
        embedBuilder.addField(who, "> " + "<@" + moderator.getId() + ">", true);
        if (!isReport) embedBuilder.setFooter("Старайтесь не нарушать правила сервера, с уважением,\nМодерация!");
        return embedBuilder;
    }

}
