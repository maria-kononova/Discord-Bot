package com.example.bot;

import com.example.bot.entity.*;
import com.example.bot.entity.Event;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageActivity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl;
import net.dv8tion.jda.internal.interactions.component.StringSelectMenuImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.Text;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.example.bot.BotApplication.*;
import static com.example.bot.BotCommands.userRepository;
import static com.example.bot.SystemMessage.*;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.TemporalAdjusters.nextOrSame;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

public class EventListener extends ListenerAdapter {
    public static String EVENT_VOICE_ROOM = "1181329973022314587";
    public static String NEWS_EVENT = "1181342931072847973";
    public static String ROLE_EVENT = "1240856138392670239";
    private static Event currnetEvent = null; //или брать из БД у которого запланированная дата сегодня и есть дата начала

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getButton().getId().contains("event4")) {
            if (event.getButton().getId().contains("idevent")) {
                Event ev = eventRepository.getEventById(Long.valueOf(event.getButton().getId().split("-")[3]));
                if (ev != null) {
                    //начать ивент
                    if (event.getButton().getId().contains("start")) {
                        if (ev.getDateStart() == null) {
                            if (currnetEvent == null) {
                                if (new Date().getDate() == ev.getDatePlan().getDate()) {
                                    ev.setDateStart(new Date());
                                    eventRepository.save(ev);
                                    System.out.println(ev);
                                    this.currnetEvent = ev;

                                    //event.deferReply(true).setContent("Событие началось. Перейди в <#" + EVENT_VOICE_ROOM + ">").queue();
                                    event.deferEdit().setEmbeds(getEmbedMsg(ev, event.getMember()).build()).setActionRow(getButtonForEventShow(ev)).queue();
                                    startTimer();
                                    TextChannel textChannel = guild.getTextChannelById(NEWS_EVENT);
                                    Member member = event.getMember();
                                    textChannel.sendMessage("||<@&" + ROLE_EVENT + ">|| Событие уже началось в -> <#" + EVENT_VOICE_ROOM + ">").setEmbeds(getEmbedMsg(ev, member).build()).queue();
                                } else
                                    event.deferReply(true).setContent("Данный ивент не запланирован на сегодня.").queue();
                            } else event.deferReply(true).setContent("В данный момент уже проводится ивент.").queue();
                        } else event.deferReply(true).setContent("Данный ивент уже начат или закончен.").queue();
                    }
                    //уведомление о ивенте
                    if (event.getButton().getId().contains("notification")) {
                        if (ev.getNotification() == 1) {
                            if (ev.getDateEnd() == null) {
                                //уведомление
                                ev.setNotification(0);
                                eventRepository.save(ev);
                                TextChannel textChannel = guild.getTextChannelById(NEWS_EVENT);
                                textChannel.sendMessage("||<@&" + ROLE_EVENT + ">|| Запланированно новое событие!").setEmbeds(getEmbedMsg(ev, event.getMember()).build()).queue();
                                //event.deferReply(true).setContent("Уведомление о событии успешно отправленно в -> <#" + NEWS_EVENT + ">").queue();
                                event.deferEdit().setEmbeds(getEmbedMsg(ev, event.getMember()).build()).setActionRow(getButtonForEventShow(ev)).queue();
                            } else event.deferReply(true).setContent("Данный ивент уже был закончен.").queue();
                        } else
                            event.deferReply(true).setContent("Уведомление о данном ивенте уже было. Следующее будет в момент начала события!").queue();
                    }
                    //закончить ивент
                    if (event.getButton().getId().contains("end")) {
                        if (ev.getDateStart() != null) {
                            if (ev.getDateEnd() == null) {
                                ev.setDateEnd(new Date());
                                eventRepository.save(ev);
                                for (EventUser eventUser : eventUserRepository.findEventUsersByEvent(ev)) {
                                    eventUser.setPrize(ev.getType().getDefaultCoins());
                                    eventUserRepository.save(eventUser);
                                }
                                stopTimer();
                                currnetEvent = null;
                                event.deferEdit().setEmbeds(getEmbedMsg(ev, event.getMember()).build()).setActionRow(getButtonForEventShow(ev)).queue();
                                //Button prizeEventButton = Button.of(ButtonStyle.SECONDARY, "prize-event4-idevent-" + ev.getId(), "Выдать награды");
                                //event.deferReply(true).setContent("Событие успешно закончено. Пора перейти к выдаче наград!").addActionRow(prizeEventButton).queue();
                            } else event.deferReply(true).setContent("Событие уже было завершено.").queue();
                        } else
                            event.deferReply(true).setContent("Событие не может быть закончено, поскольку не было начато.").queue();
                    }
                    //удалить ивент
                    if (event.getButton().getId().contains("delete")) {
                        if (ev.getDateStart() == null) {
                            eventRepository.delete(ev);
                            event.deferReply(true).setContent("Событие успешно удалено.").queue();
                        } else
                            event.deferReply(true).setContent("Событие не может быть удалено, поскольку уже было начато.").queue();
                    }
                    //раздать награды
                    if (event.getButton().getId().contains("prize")) {
                        if (ev.getDateStart() != null && ev.getDateEnd() != null) {
                            if (!ev.isPrized()) {
                                List<EventUser> eventUsers = getEventUserForPrize(ev);
                                if (eventUsers != null) {
                                    Button acceptPrizeButton = Button.success("accept-event4-idevent-" + ev.getId(), "Подтвердить").withEmoji(guild.getEmojiById(EMOJI_CHECK_MARK));
                                    Button editPrizeButton = Button.primary("edit-event4-idevent-" + ev.getId(), guild.getEmojiById(EMOJI_EDIT));

                                    EmbedBuilder embedBuilder = SystemMessage.sendMsgPrize(ev, eventUsers, event.getMember(), false);
                                    List<ItemComponent> components = new ArrayList<>();
                                    components.add(acceptPrizeButton);
                                    components.add(editPrizeButton);

                                    event.deferReply(true).setContent("## Можно выдать награду события!\n" +
                                            "Ивент успешно был проведён и закончен. Осталось дело за малым - выдать награду участникам!\n" +
                                            "> - Изначально в списке указана **награда за участие**. \n" +
                                            "> - Mожно **изменить** общее количество монеток, которые заработал участник.\n" +
                                            "> - Можно указать **0**, если участник не был активен на ивенте.\n" +
                                            "### Кнопки:\n" +
                                            "> <:check_mark:"+EMOJI_CHECK_MARK+"> Подтвердить - выдать указанные в списке награды всем участникам.\n" +
                                            "> <:edit:"+EMOJI_EDIT+">  - Отредактировать награду для участника (указывая его номер по списку).\n").setEmbeds(embedBuilder.build()).addActionRow(components).queue();
                                } else {
                                    SystemMessage.sendReportAboutEvent(ev, eventUsers, event.getMember());
                                    event.deferReply(true).setContent("Участники события не найдены. Ивент будет удалён :с").queue();
                                    eventRepository.delete(ev);
                                }
                            } else event.deferReply(true).setContent("Награды события уже выданы.").queue();
                        } else {
                            event.deferReply(true).setContent("Событие ещё не завершено или не начато.").queue();
                        }
                    }
                    if (event.getButton().getId().contains("accept")) {
                        if (!ev.isPrized()) {
                            List<EventUser> eventUsers = getEventUserForPrize(ev);
                            for (EventUser eventUser : eventUsers) {
                                eventUser.getId().getUser().updateCoins(eventUser.getPrize());
                                userRepository.save(eventUser.getId().getUser());
                            }
                            event.deferReply(true).setContent("Награда успешно выдана, отчёт отправлен в -> <#" + EVENT_REPORT_CHANNEL + ">").queue();
                            SystemMessage.sendReportAboutEvent(ev, eventUsers, event.getMember());
                            ev.setPrized(true);
                            eventRepository.save(ev);
                        } else event.deferReply(true).setContent("Награда события уже выдана.").queue();
                    }
                    if (event.getButton().getId().contains("edit")) {
                        //модал с выдачей наград
                        if (!ev.isPrized()) {
                            Modal.Builder modal = Modal.create("prize-modal4-" + event.getMessageId() + "-" + ev.getId(), "Награды ивента «" + ev.getType().getName() + "»");
                            TextInput.Builder idTextInput = TextInput.create("id-prize-event-modal4",
                                            "Номер (Число)", TextInputStyle.SHORT)
                                    .setMinLength(1).setMaxLength(3);
                            TextInput.Builder coinsTextInput = TextInput.create("coins-prize-event-modal4",
                                            "Награда пользователя за событие", TextInputStyle.SHORT)
                                    .setMinLength(1).setMaxLength(5);
                            modal.addActionRow(idTextInput.build()).addActionRow(coinsTextInput.build());
                            event.replyModal(modal.build()).queue();
                        } else event.deferReply(true).setContent("Награды данного события уже выданы.").queue();
                    }
                } else {
                    event.deferReply(true).setContent("Нет данного события. Лучше обратиться к актуальному списку:").setComponents(ActionRow.of(getSelectEvent(event.getMember()))).queue();
                }
            } else {
                switch (event.getButton().getId()) {
                    case ("create-event4"): {
                        List<SelectOption> selectOptions = new ArrayList<>();

                        for (EventType eventType : eventTypeRepository.findAll()) {
                            selectOptions.add(SelectOption.of(eventType.getName(), String.valueOf(eventType.getId())));
                        }
                        StringSelectMenuImpl selectMenu = new StringSelectMenuImpl("type-select-event4", "Для начала выбери тип ивента", 1, 1, false, selectOptions);
                        event.deferReply(true).setContent("Тип ивента:").setComponents(ActionRow.of(selectMenu)).queue();
                        break;
                    }
                    case ("show-msg-event4"): {
                        StringSelectMenuImpl selectMenu = getSelectEvent(event.getMember());
                        if (selectMenu != null) {
                            event.deferReply(true).setContent("Выбери события для управления").setComponents(ActionRow.of(selectMenu)).queue();
                        } else {
                            Button createEventButton = Button.of(ButtonStyle.PRIMARY, "create-event4", "Создать ивент").withEmoji(guild.getEmojiById(EMOJI_PLUS));
                            event.deferReply(true).setContent("Утёнок не нашёл твоих запланированных мероприятий. Можешь создать событие по кнопочке внизу!").addActionRow(createEventButton).queue();
                        }
                    }
                    //расписание событий на текущую неделю
                    case ("graphic-prev-event4"): {
                        Button nextGraphicEvent = Button.success("graphic-next-event4", "Следующая неделя").withEmoji(guild.getEmojiById(EMOJI_NEXT));
                        LocalDate today = LocalDate.now();
                        LocalDate monday = today.with(previousOrSame(MONDAY));
                        LocalDate sunday = today.with(nextOrSame(SUNDAY));
                        List<Event> events = new ArrayList<>();
                        for (Event ev : eventRepository.findAll()) {
                            LocalDate localDate = ev.getDatePlan().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            if (localDate.isAfter(monday) && localDate.isBefore(sunday)) {
                                events.add(ev);
                            }
                        }
                        event.deferEdit().setEmbeds(sendMsgGraphic(events, monday, sunday, today).build()).setActionRow(nextGraphicEvent).queue();
                    }
                    case ("graphic-event4"): {
                        Button nextGraphicEvent = Button.success("graphic-next-event4", "Следующая неделя").withEmoji(guild.getEmojiById(EMOJI_NEXT));
                        LocalDate today = LocalDate.now();
                        LocalDate monday = today.with(previousOrSame(MONDAY));
                        LocalDate sunday = today.with(nextOrSame(SUNDAY));
                        List<Event> events = new ArrayList<>();
                        for (Event ev : eventRepository.findAll()) {
                            LocalDate localDate = ev.getDatePlan().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            if (localDate.isAfter(monday) && localDate.isBefore(sunday)) {
                                events.add(ev);
                            }
                        }

                        event.deferReply(true).setEmbeds(sendMsgGraphic(events, monday, sunday, today).build()).addActionRow(nextGraphicEvent).queue();
                        break;
                    }
                    case ("graphic-next-event4"): {
                        Button nextGraphicEvent = Button.success("graphic-prev-event4", "Предыдущая неделя").withEmoji(guild.getEmojiById(EMOJI_PREV));
                        LocalDate today = LocalDate.now();
                        LocalDate monday = today.plusWeeks(1).with(previousOrSame(MONDAY));
                        LocalDate sunday = today.plusWeeks(1).with(nextOrSame(SUNDAY));
                        List<Event> events = new ArrayList<>();
                        for (Event ev : eventRepository.findAll()) {
                            LocalDate localDate = ev.getDatePlan().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            if (localDate.isAfter(monday) && localDate.isBefore(sunday)) {
                                events.add(ev);
                            }
                        }

                        event.deferEdit().setEmbeds(sendMsgGraphic(events, monday, sunday, today).build()).setActionRow(nextGraphicEvent).queue();

                        //event.deferReply(true).setEmbeds(sendMsgGraphic(events, monday, sunday, today).build()).queue();
                        break;
                    }
                    default:
                        event.deferReply(true).setContent("Работаем над этим вопросом!").queue();
                }
            }
        }
    }

    @Override
    public void onGenericSelectMenuInteraction(GenericSelectMenuInteractionEvent event) {
        if (event.getComponent().getId().contains("select-event4")) {
            switch (event.getComponent().getId()) {
                case ("type-select-event4"): {
                    String value = (String) event.getValues().get(0); //id выбранного ивента
                    EventType eventType = eventTypeRepository.getEventTypeById(Long.valueOf(value));

                    TextInput dateEvent = TextInput.create("date-event4", "Дата проведения ивента", TextInputStyle.SHORT)
                            .setPlaceholder("dd.mm.yyyy")
                            .setMinLength(10)
                            .setMaxLength(10)
                            .build();

                    TextInput timeEvent = TextInput.create("time-event4", "Время проведения ивента (МСК)", TextInputStyle.SHORT)
                            .setPlaceholder("hh:mm")
                            .setMinLength(5)
                            .setMaxLength(5)
                            .build();

                    Modal modal = Modal.create("create-event-modal4-" + eventType.getId(), "Создание ивента: " + eventType.getName())
                            .addActionRow(dateEvent)
                            .addActionRow(timeEvent)
                            .build();

                    event.replyModal(modal).queue();
                    break;
                }
                case ("event-select-event4"): {
                    String value = (String) event.getValues().get(0); //id выбранного ивента
                    Event newEvent = eventRepository.getEventById(Long.valueOf(value));
                    if (newEvent != null) {
                        Member member = event.getMember();
                        event.deferReply(true).setContent("## Управление ивентом\n" +
                                "Здесь можно выполнить всё необходимое по ивенту!\n" +
                                "**Важные детали:**\n" +
                                "> - Начать ивент можно только в **запланированную** дату.\n" +
                                "> - Отправить уведомление можно только **1 раз**. \n" +
                                "> - Удалить ивент можно только если он **не был начат**.\n" +
                                "> - После завершения ивента, даже в случае отсутствия на нём участников, \n" +
                                ">  нужно нажать на кнопку **«Выдать награду»**. Данное действие отправить отчёт о\n" +
                                ">  проведённом мероприятии куратору.\n" +
                                "\n**Кнопки для управления:**\n" +
                                "> <:start:"+EMOJI_START+">  - Начать ивент.\n" +
                                "> <:notification:"+EMOJI_NOTIFICATION+">  - Отправить уведомление об ивенте.\n" +
                                "> <:END:"+EMOJI_END+">  - Закончить ивент.\n" +
                                "> <:delete:"+EMOJI_DELETE+">  - Удалить ивент.\n" +
                                "> <:prize:"+EMOJI_PRIZE+">  - Выдать награду.").setEmbeds(getEmbedMsg(newEvent, member).build()).addActionRow(getButtonForEventShow(newEvent)).queue();
                    }
                    event.deferReply(true).setContent("Нет данного ивента.").queue();
                }
                default:
                    event.deferReply(true).setContent("Работаем над этим вопросом").queue();
            }
        }
    }

    public List<ItemComponent> getButtonForEventShow(Event newEvent) {
        Button startEventButton = Button.of(ButtonStyle.SUCCESS, "start-event4-idevent-" + newEvent.getId(), guild.getEmojiById(EMOJI_START)).withDisabled(newEvent.getDateStart() != null);
        Button notificationEventButton = Button.of(ButtonStyle.PRIMARY, "notification-event4-idevent-" + newEvent.getId(), guild.getEmojiById(EMOJI_NOTIFICATION)).withDisabled(newEvent.getNotification() == 0 || newEvent.getDateStart() != null);
        Button endEventButton = Button.of(ButtonStyle.PRIMARY, "end-event4-idevent-" + newEvent.getId(), guild.getEmojiById(EMOJI_END)).withDisabled(newEvent.getDateStart() == null || newEvent.getDateEnd() != null);//.withDisabled(!(newEvent.getDateStart() != null && newEvent.getDateEnd() == null));
        Button deleteEventButton = Button.of(ButtonStyle.SECONDARY, "delete-event4-idevent-" + newEvent.getId(), guild.getEmojiById(EMOJI_DELETE)).withDisabled(newEvent.getDateStart() != null);//.withDisabled(newEvent.getDateStart() != null); //guild.getEmojiById());
        Button prizeEventButton = Button.of(ButtonStyle.SECONDARY, "prize-event4-idevent-" + newEvent.getId(), guild.getEmojiById(EMOJI_PRIZE)).withDisabled(!newEvent.isPrized() && newEvent.getDateEnd() == null);//.withDisabled( newEvent.getDateEnd() != null);
        List<ItemComponent> components = new ArrayList<>();
        components.add(startEventButton);
        components.add(notificationEventButton);
        components.add(endEventButton);
        components.add(deleteEventButton);
        components.add(prizeEventButton);
        return components;
    }

/*    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (event.getChannelJoined() != null) {
            if (event.getChannelJoined().getId().equals(EVENT_VOICE_ROOM)) {
                if (currnetEvent != null) {
                    Member member = event.getMember();
                    EventUserId eventUserId = new EventUserId(currnetEvent, userRepository.getUserById(member.getIdLong()));
                    //пользователь зашёл на ивент
                    if (eventUserRepository.findById(eventUserId) == null) {
                        EventUser eventUser = new EventUser(eventUserId);
                        eventUserRepository.save(eventUser);
                    }
                }
            }
        }
    }*/

    //меню событий
    public StringSelectMenuImpl getSelectEvent(Member member) {
        List<SelectOption> selectOptions = new ArrayList<>();
        int i = 1;
        for (Event events : eventRepository.findAll()) {
            if (events.getEventer().getId().equals(member.getIdLong())) {
                if (events.getDateEnd() == null) {
                    selectOptions.add(SelectOption.of(i + ". " + events.getType().getName() + " " + events.getDatePlan().toString().substring(0, 16), String.valueOf(events.getId())));
                    i++;
                } else if (!events.isPrized())
                    selectOptions.add(SelectOption.of("~Награда!~ " + events.getType().getName() + " " + events.getDatePlan().toString().substring(0, 16), String.valueOf(events.getId())));
            }

        }
        if (selectOptions.size() == 0) return null;
        StringSelectMenuImpl selectMenu = new StringSelectMenuImpl("event-select-event4", "Выбери нужный ивент", 1, 1, false, selectOptions);
        return selectMenu;
    }

    public Date getDateFromString(String date, String time) { //dd.mm.yyyy, hh:mm
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
        try {
            Date dateTime = dateFormat.parse(date + " " + time);
            System.out.println(dateTime);
            return dateTime;
        } catch (ParseException e) {
            return null;
        }
    }


    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().contains("modal4")) {
            if (event.getModalId().contains("create-event-modal4")) {
                String date = event.getValue("date-event4").getAsString();
                String time = event.getValue("time-event4").getAsString();
                Date dateTime = getDateFromString(date, time);
                if (dateTime != null) {
                    if (dateTime.compareTo(new Date()) > 0) {
                        if (checkDateForEvent(dateTime)) {
                            Member member = event.getMember();
                            EventType eventType = eventTypeRepository.getEventTypeById(Long.valueOf(event.getModalId().split("-")[3]));
                            Event newEvent = new Event(dateTime, eventType, userRepository.getUserById(member.getIdLong()));
                            eventRepository.save(newEvent);

                            event.deferReply(true).setContent("## Управление ивентом\n" +
                                    "Здесь можно выполнить всё необходимое по ивенту!\n" +
                                    "**Важные детали:**\n" +
                                    "> - Начать ивент можно только в **запланированную** дату.\n" +
                                    "> - Отправить уведомление можно только **1 раз**. \n" +
                                    "> - Удалить ивент можно только если он **не был начат**.\n" +
                                    "> - После завершения ивента, даже в случае отсутствия на нём участников, \n" +
                                    ">  нужно нажать на кнопку **«Выдать награду»**. Данное действие отправить отчёт о\n" +
                                    ">  проведённом мероприятии куратору.\n" +
                                    "\n**Кнопки для управления:**\n" +
                                    "> <:start:"+EMOJI_START+">  - Начать ивент.\n" +
                                    "> <:notification:"+EMOJI_NOTIFICATION+">  - Отправить уведомление об ивенте.\n" +
                                    "> <:END:"+EMOJI_END+">  - Закончить ивент.\n" +
                                    "> <:delete:"+EMOJI_DELETE+">  - Удалить ивент.\n" +
                                    "> <:prize:"+EMOJI_PRIZE+">  - Выдать награду.").setEmbeds(getEmbedMsg(newEvent, member).build()).addActionRow(getButtonForEventShow(newEvent)).queue();
                        } else {
                            Button graphicButton = Button.of(ButtonStyle.PRIMARY, "graphic-event4", "Расписание ивентов").withEmoji(guild.getEmojiById(EMOJI_CALENDAR));
                            event.deferReply(true).setContent("Ошибка создания мероприятия: на данную дату уже запланированно событие!\nУтёнок советует ознакомиться с акктуальным расписанием:")
                                    .addActionRow(graphicButton)
                                    .queue();
                        }
                    } else
                        event.deferReply(true).setContent("Ошибка создания мероприятия: дата не может быть раньше текущей!").queue();
                } else event.deferReply(true).setContent("Ошибка создания мероприятия: не валидная дата!").queue();
            }
            if (event.getModalId().contains("prize")) {
                try {
                    int index = Integer.parseInt(event.getValue("id-prize-event-modal4").getAsString());
                    int coins = Integer.parseInt(event.getValue("coins-prize-event-modal4").getAsString());
                    Event ev = eventRepository.getEventById(Long.valueOf(event.getModalId().split("-")[3]));
                    List<EventUser> eventUsers = getEventUserForPrize(ev); //!!!!
                    if (index > 0 && index <= eventUsers.size()) {
                        if (coins > 0) {
                            eventUsers.get(index - 1).setPrize(coins);
                            eventUserRepository.save(eventUsers.get(index - 1));
                            EmbedBuilder embedBuilder = SystemMessage.sendMsgPrize(ev, getEventUserForPrize(ev), event.getMember(), false);
                            event.deferEdit().setEmbeds(embedBuilder.build()).queue();
                        }
                    }
                } catch (Exception e) {
                    event.deferReply(true).setContent("Некорректно введённые данные").queue();
                }
            }
        }
    }

    public List<EventUser> getEventUserForPrize(Event ev) {
        List<EventUser> eventUsersAll = eventUserRepository.findEventUsersByEvent(ev);
        List<EventUser> eventUsersCorrect = new ArrayList<>();
        for (EventUser eventUser : eventUsersAll) {
            if (eventUser.getSecondMute() / 60 > 5 || eventUser.getSecondActive() / 60 > 5) {
                eventUsersCorrect.add(eventUser);
            } else eventUserRepository.delete(eventUser);
        }
        Collections.sort(eventUsersCorrect, new Comparator<EventUser>() {
            @Override
            public int compare(EventUser o1, EventUser o2) {
                return o1.getId().getUser().getId().compareTo(o2.getId().getUser().getId());
            }
        });
        if (eventUsersCorrect.size() == 0) return null;
        return eventUsersCorrect;
    }


    //сообщение об ивенте
    public EmbedBuilder getEmbedMsg(Event event, Member member) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        ///!!!
        embedBuilder.setImage(event.getType().getURL());
        embedBuilder.setTitle("Событие «" + event.getType().getName() + "»");
        embedBuilder.addField("Описание", event.getType().getDescription(), true);
        embedBuilder.setColor(Color.decode(event.getType().getColor()));
        if (event.getDateStart() != null) {
            embedBuilder.addField("<:calendar:" + EMOJI_CALENDAR + "> Дата начала события", "<t:" + event.getDateStart().getTime() / 1000 + ":f>", false);
        }
        if (event.getDateEnd() != null) {
            embedBuilder.addField("<:calendar:" + EMOJI_CALENDAR + "> Дата окончания события", "<t:" + event.getDateEnd().getTime() / 1000 + ":f> ", false);
        }
        if (event.getDateStart() == null) {
            embedBuilder.addField("<:calendar:" + EMOJI_CALENDAR + "> Запланированная дата проведения", "<t:" + event.getDatePlan().getTime() / 1000 + ":f> " + "(<t:" + event.getDatePlan().getTime() / 1000 + ":R>)", false);
        }
        embedBuilder.addField("Монетки за участие", String.valueOf(event.getType().getDefaultCoins()) + " <:prize:" + EMOJI_PRIZE + ">" , false);
        embedBuilder.addField("Монетки за победу", "+"+ String.valueOf(event.getType().getWinCoins()) + " <:prize:" + EMOJI_PRIZE + ">" , false);
        embedBuilder.addField("Организатор события", "<@" + event.getEventer().getId() + ">", false);
        embedBuilder.setFooter(member.getEffectiveName(), member.getUser().getAvatarUrl());
        return embedBuilder;
    }


    public boolean checkDateForEvent(Date date) {
        for (Event event : eventRepository.findAll()) {
            //не равна
            if (event.getDatePlan().getDate() == date.getDate()) return false;
            //не позже чем какая-то уже запланированная дата + час
            /*if(!date.before(new Date(event.getDatePlan().getTime() + 1 * 60 * 60 * 1000)) || !date.after(new Date(event.getDatePlan().getTime() - 1 * 60 * 60 * 1000)))
                return false;*/
        }
        return true;
    }

    private static Timer timer;
    private static TimerTask task;

    //таймер обновления статистики войса
    private static void startTimer() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                if (currnetEvent != null) {
                    VoiceChannel voiceChannel = guild.getVoiceChannelById(EVENT_VOICE_ROOM);
                    for (Member member : voiceChannel.getMembers()) {
                        User user = userRepository.getUserById(member.getIdLong());
                        EventUser eventUser = eventUserRepository.findById(new EventUserId(currnetEvent, user));
                        if (eventUser == null) {
                            eventUser = new EventUser(new EventUserId(currnetEvent, user));
                        }
                        eventUser.update(member.getVoiceState().isMuted());
                        eventUserRepository.save(eventUser);
                    }
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    private static void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

}
