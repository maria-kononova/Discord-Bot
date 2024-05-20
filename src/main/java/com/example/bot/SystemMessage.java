package com.example.bot;

import com.example.bot.entity.Event;
import com.example.bot.entity.EventUser;
import com.example.bot.entity.EventUserId;
import com.example.bot.entity.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.internal.entities.WebhookImpl;
import net.dv8tion.jda.internal.entities.emoji.CustomEmojiImpl;
import net.dv8tion.jda.internal.interactions.component.StringSelectMenuImpl;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

import static com.example.bot.BotApplication.guild;
import static com.example.bot.VoiceRoomListener.CREATE_PRIVATE_VOICE_ROOM_ID;

public class SystemMessage {
    public static String WEBHOOK = "https://discord.com/api/webhooks/1240451733902069830/g39voSSBP0Os2XIMgcc95QwPiOTiwpFSZVsf81EF9XardRKgg3cg6jZ2YTPnhtcclZ_B";
    public static String INFO_CHANNEL = "1181342904279629864";
    public static String RULES_CHANNEL = "1181342967986921575";
    public static String YOUR_VOICE_ROOM_EDIT = "1181326695408619560";
    public static String CROCODILE_GAME = "1236431656395608074";
    public static String CREATE_TICKET_CHANNEL = "1181346855108493323";
    public static String CREATE_EVENTS_CHANNEL = "1240055022339752057";
    public static String EVENT_REPORT_CHANNEL = "1240991200597774356";
    public static String EVENT_RULE_CHANNEL = "1181343379104206958";
    public static String EMOJI_EDIT = "1238534141943873688";
    public static String EMOJI_CROWN = "1238535149218693200";
    public static String EMOJI_USERS = "1238534150483345449";
    public static String EMOJI_LOCK = "1238530420438597662";
    public static String EMOJI_OPEN = "1238530418915934320";
    public static String EMOJI_KICK_USER = "1238534143835377664";
    public static String EMOJI_BAN_USER = "1238592212556709938";
    public static String EMOJI_ADD_USER = "1238534146192834652";
    public static String EMOJI_START = "1241422164759154709";
    public static String EMOJI_END = "1241422163194548234";
    public static String EMOJI_NEXT = "1241420235693621319";
    public static String EMOJI_PREV = "1241420213082390568";
    public static String EMOJI_PRIZE = "1241420233407729846";
    public static String EMOJI_MICROPHONE = "1241420214470447115";
    public static String EMOJI_MICROPHONE_MUTE = "1241420216592760943";
    public static String EMOJI_DELETE = "1241420209387208777";
    public static String EMOJI_CHECK_MARK = "1241420207512354886";
    public static String EMOJI_CALENDAR = "1241420205628850327";
    public static String EMOJI_NOTIFICATION = "1241420218342051961";
    public static String EMOJI_PLUS = "1241420219608600578";


    //сообщение в чат "твоя-комната", позволяет редактировать свой голосовой канал
    //идентификатор команды 1
    public static void sendVoiceControlMessage() {
        //deleteHistoryOnChannel(YOUR_VOICE_ROOM_EDIT);
        TextChannel chatChannel = guild.getTextChannelById(YOUR_VOICE_ROOM_EDIT);
        if (chatChannel != null) {

            Button changeNameButton = Button.primary("change-name-voice1", Objects.requireNonNull(guild.getEmojiById(EMOJI_EDIT)));//"Изменить название канала");
            Button limitUsersButton = Button.primary("change-limit-users-voice1", Objects.requireNonNull(guild.getEmojiById(EMOJI_USERS))); //"Изменить количество пользователей");
            Button newHostButton = Button.primary("change-host-voice1", Objects.requireNonNull(guild.getEmojiById(EMOJI_CROWN))); //"Изменить владельца канала");
            Button addUserButton = Button.primary("add-user-voice1", Objects.requireNonNull(guild.getEmojiById(EMOJI_ADD_USER))); //"пригласить пользователя");
            Button openRoomButton = Button.secondary("open-room-voice1", Objects.requireNonNull(guild.getEmojiById(EMOJI_OPEN))); //"Открыть канал");
            Button closeRoomButton = Button.secondary("close-room-voice1", Objects.requireNonNull(guild.getEmojiById(EMOJI_LOCK))); //"Закртыть канал");
            Button kickUserButton = Button.secondary("kick-user-voice1", Objects.requireNonNull(guild.getEmojiById(EMOJI_KICK_USER))); //"Выгнать пользователя");
            Button banUserButton = Button.secondary("ban-user-voice1", Objects.requireNonNull(guild.getEmojiById(EMOJI_BAN_USER))); //"забанить пользователя");


            //Button btn = Button.primary("btn", Emoji.fromCustom(Objects.requireNonNull(guild.getEmojiById("1220809885059645470"))));
            List<ItemComponent> componentsPrimary = new ArrayList<>();
            componentsPrimary.add(changeNameButton);
            componentsPrimary.add(limitUsersButton);
            componentsPrimary.add(newHostButton);
            componentsPrimary.add(addUserButton);
            ActionRow actionRowPrimary = ActionRow.of(componentsPrimary);

            List<ItemComponent> componentsSecondary = new ArrayList<>();
            componentsSecondary.add(closeRoomButton);
            componentsSecondary.add(openRoomButton);
            componentsSecondary.add(kickUserButton);
            componentsSecondary.add(banUserButton);
            ActionRow actionRowSecondary = ActionRow.of(componentsSecondary);


            Collection<ActionRow> actionRows = new ArrayList<>();
            actionRows.add(actionRowPrimary);
            actionRows.add(actionRowSecondary);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setDescription("## <a:1907duckwow:1238511205316820992>   **Здесь ты можешь управлять своей комнаткой** <a:1907duckwow:1238511205316820992>  \n" +
                    "\n" +
                    "### Для того, чтобы получить личный голосовой канал, достаточно кликнуть сюда -> **<#" + CREATE_PRIVATE_VOICE_ROOM_ID + ">**\n" +
                    "### После успешного создания своей комнатки, можно воспользоваться следующими командами для управления:\n" +
                    "> <:edit:" + EMOJI_EDIT + ">  ** - Изменить имя канала**\n" +
                    "> <:users:" + EMOJI_USERS + ">  ** - Установить лимит пользователей в канале**\n" +
                    "> <:crown:" + EMOJI_CROWN + ">  ** - Передать права владельца канала**\n" +
                    "> <:add_user:" + EMOJI_ADD_USER + ">  ** - Дать доступ пользователю к каналу**\n" +
                    "> <:lock:" + EMOJI_LOCK + ">  ** - Закрыть канал от всех**\n" +
                    "> <:open:" + EMOJI_OPEN + ">  ** - Открыть канал для всеобщего доступа**\n" +
                    "> <:kick_user:" + EMOJI_KICK_USER + ">  ** - Исключить пользователя из канала**\n" +
                    "> <:ban_user:" + EMOJI_BAN_USER + ">  ** - Забрать доступ к каналу у пользователя**\n");
            embedBuilder.setColor(Color.decode("#5865f2"));

            chatChannel.sendMessageEmbeds(embedBuilder.build()).addComponents(actionRows).queue();
        }
    }

    //идентификатор команды 2
    public static void sendCrocodileGameMessage() {
        //deleteHistoryOnChannel(YOUR_VOICE_ROOM_EDIT);
        TextChannel chatChannel = guild.getTextChannelById(CROCODILE_GAME);
        if (chatChannel != null) {

            Button takeWordButton = Button.of(ButtonStyle.SUCCESS, "take-word-crocodile-game2", "Взять слово");
            Button statisticsButton = Button.of(ButtonStyle.SECONDARY, "statistics-crocodile-game2", "Моя статистика");
            Button bestPlayersButton = Button.of(ButtonStyle.SECONDARY, "best-players-crocodile-game2", "Лучшие игроки");
            Button reportPlayersButton = Button.of(ButtonStyle.SECONDARY, "report-players-crocodile-game2", "Пожаловаться");
            //можно добавить что-то по прошлым играм
            List<ItemComponent> componentsPrimary = new ArrayList<>();
            componentsPrimary.add(takeWordButton);
            componentsPrimary.add(statisticsButton);
            componentsPrimary.add(bestPlayersButton);
            componentsPrimary.add(reportPlayersButton);
            ActionRow actionRow = ActionRow.of(componentsPrimary);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setDescription("## :crocodile:  Давай сыграем в крокодила :crocodile:\n" +
                    "### **Правила игры:**\n" +
                    "> **1.** Один из участников становится ведущим, нажав на кнопку  **«Взять слово»**, и Утёнок загадывает ему слово. \n" +
                    "> Задача ведущего объяснить это слово, не называя его (нельзя использовать разные формы этого слова и однокоренные к нему). \n" +
                    "> **2.** Другие участники пытаются отгадать загаданное слово по объяснению ведущего.\n" +
                    "> **3.** На загадывание слова ведущему даётся **5 минут**,  на его отгадывание — **10 минут** с начала раунда. \n" +
                    "> В случае истечения времени Утёнок подготавливает новое слово.\n" +
                    "> **4.** За успешно угаданное слово игроку начисляется **5 баллов** рейтинга, за загаданное - **10 баллов**.\n" +
                    "> **5.** Игроку выдаются штрафы за нарушения правил игры, которые понижают его рейтинг и отстраняют от возможности быть ведущим.\n" +
                    "\n" +
                    "### **Штрафы могут быть выданы, если:**\n" +
                    "> - Ведущий раскрыл загаданное слово.\n" +
                    "> - Ведущий не уложился в 5 минут на загадывание слова. \n" +
                    "> - Игрок угадал ещё не загаданное слово (подозрение на нечестную игру).\n" +
                    "\n" +
                    "*Любой может пожаловаться на игрока, который играет нечестно, тогда Утёнок самостоятельно проведёт проверку.*");
            embedBuilder.setColor(Color.decode("#248046"));

            chatChannel.sendMessageEmbeds(embedBuilder.build()).addComponents(actionRow).queue();
        }
    }

    //идентификатор конмады 3
    public static void sendTicketMessage() {
        TextChannel chatChannel = guild.getTextChannelById(CREATE_TICKET_CHANNEL);
        if (chatChannel != null) {

            Button createTicketButton = Button.of(ButtonStyle.PRIMARY, "create-ticket3", "Создать тикет").withEmoji(guild.getEmojiById("1238511202385002527"));
            //можно добавить что-то по прошлым играм
            List<ItemComponent> componentsPrimary = new ArrayList<>();
            componentsPrimary.add(createTicketButton);
            ActionRow actionRow = ActionRow.of(componentsPrimary);

            List<SelectOption> selectOptions = new ArrayList<>();
            selectOptions.add(SelectOption.of("Вопрос", "question").withEmoji(guild.getEmojiById("1238970002472833026")));
            selectOptions.add(SelectOption.of("Нарушение", "violation").withEmoji(guild.getEmojiById("1238969991093682186")));
            selectOptions.add(SelectOption.of("Ошибка Утёнка", "bag").withEmoji(guild.getEmojiById("1238968051190464605")));

            StringSelectMenuImpl selectMenu = new StringSelectMenuImpl("type-select-problem-ticket3", "Для начала выбери тип тикета", 1, 1, false, selectOptions);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setDescription("## <a:discordduck:1238862338384330773>  Создание тикета <a:discordduck:1238862338384330773> \n" +
                    "### **Тикет - обращение к администрации и модерации сервера через Утёнка.**\n" +
                    "### Создавать тикет следует в случае:\n" +
                    "> - Замеченного нарушения правил.\n" +
                    "> - Возникновения особых вопросов.\n" +
                    "> - Некорректной работы Утёнка.\n" +
                    "\n" +
                    "### **Перед созданием тикета следует убедиться, что он необходим.** \n" +
                    "*Утёнок советует проверить нет ли информации о назревшем вопросе в каналах  <#" + INFO_CHANNEL + ">  и  <#" + RULES_CHANNEL + ">*");
            embedBuilder.setColor(Color.decode("#e4e173"));

            chatChannel.sendMessageEmbeds(embedBuilder.build()).addActionRow(selectMenu).queue();
        }
    }

    //сообщение создания инвета идентификатор 4
    public static void sendCreateEventMsg() {
        TextChannel chatChannel = guild.getTextChannelById(CREATE_EVENTS_CHANNEL);
        if (chatChannel != null) {

            Button createEventButton = Button.of(ButtonStyle.PRIMARY, "create-event4", "Создать ивент").withEmoji(guild.getEmojiById(EMOJI_PLUS));
            Button graphicButton = Button.of(ButtonStyle.PRIMARY, "graphic-event4", "Расписание ивентов").withEmoji(guild.getEmojiById(EMOJI_CALENDAR));
            Button showEventMsgButton = Button.of(ButtonStyle.SECONDARY, "show-msg-event4", "Управление ивентами");

            List<ItemComponent> componentsPrimary = new ArrayList<>();
            componentsPrimary.add(createEventButton);
            componentsPrimary.add(graphicButton);
            componentsPrimary.add(showEventMsgButton);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setDescription("# <a:9881duckvibe:1238511208563216485>  Создание ивента <a:9881duckvibe:1238511208563216485>  \n" +
                    "### **Ивент - игровое и развлекательное мероприяте (событие), которое проводится ивентёрами на сервере.**\n" +
                    "\n" +
                    "### **Необходимая информация для создания и проведения ивента:**\n" +
                    "> **1.**  В день возможно провести только **1 ивент**. \n" +
                    "> Перед созданием события следует убедиться, что желаемая дата проведения свободна в расписании ивентов.\n" +
                    "> \n" +
                    "> **2.** Создать ивент можно только по представленным играм. \n" +
                    "> При желании провести особенный ивент можно оформить **тикет** в ->  <#"+CREATE_TICKET_CHANNEL+">\n" +
                    "> \n" +
                    "> **3.** После выбора типа ивента необходимо ввести запланированную **дату** и **время** события. \n" +
                    "> \n" +
                    "> **4.** После успешного создания события будет доступен просмотр его сообщения, где указана вся  необходимая информация об ивенте с возможностью управления им. \n" +
                    "> Отредактировать эту информацию нельзя, но можно **пересоздать ивент**, если он ещё **не был начат**. \n" +
                    "> \n" +
                    "> **5.** Уведомление о событии **желательно** сделать за **2 часа** до запланированного начала ивента, чтобы участники успели собраться. \n" +
                    "> \n" +
                    "> **6.** После успешного проведения ивента его необходимо **закончить** в управлении, а также выдать **награду** участникам.\n" +
                    "> Награда может быть выдана **автоматически**, а также **отредактирована** для конкретного участника в соответствии с его заслугами на ивенте.\n" +
                    "> **Данный отчёт о выдаче награды будет направлен куратору.** \n" +
                    "\n" +
                    "*С подробным описанием конкретных ивентов и базовых правил можно ознакомиться в -> <#"+EVENT_RULE_CHANNEL+">*");
            embedBuilder.setColor(Color.orange);

            chatChannel.sendMessageEmbeds(embedBuilder.build()).addActionRow(componentsPrimary).queue();
        }
    }

    //сообщение ивента webhook
    public static void sendWebhookAboutEvent() throws Exception {
        DiscordWebhook webhook = new DiscordWebhook(WEBHOOK);
        webhook.addEmbed(new DiscordWebhook.EmbedObject().setColor(Color.orange).setTitle("Ивент").setImage("https://i.pinimg.com/736x/fa/81/3b/fa813b98ec12fb30bb2ace9e754120fa.jpg")
                .setDescription("## Забегайте на ивент"));
        webhook.execute();
    }

    //сообщение ивента
    public static void sendMsgAboutEvent(Event newEvent, InteractionHook hook) throws Exception {
        Button startEventButton = Button.of(ButtonStyle.SUCCESS, "start-event4", "Начать"); //guild.getEmojiById());
        Button notificationEventButton = Button.of(ButtonStyle.PRIMARY, "notification-event4", "Уведомление");// guild.getEmojiById());
        Button endEventButton = Button.of(ButtonStyle.PRIMARY, "end-event4", "Закончить");
        Button deleteEventButton = Button.of(ButtonStyle.SECONDARY, "delete-event4", "Удалить"); //guild.getEmojiById());
        List<ItemComponent> components = new ArrayList<>();
        components.add(startEventButton);
        components.add(notificationEventButton);
        components.add(endEventButton);
        components.add(deleteEventButton);
        String msg = "## " + newEvent.getType().getName() + " \n" +
                "\n" +
                ">>> " + newEvent.getType().getDescription() + "\n" +
                "\n" +
                "   Собираемся в <t:" + newEvent.getDateStart().getTime() + ":F>";
        hook.sendMessage(msg).addActionRow(components).queue();
    }

    //сообщение при выдаче наград
    public static EmbedBuilder sendMsgPrize(Event event, List<EventUser> eventUsers, Member member, Boolean isReport) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Награды события «" + event.getType().getName() + "»");
        embedBuilder.setDescription(" <:calendar:" + EMOJI_CALENDAR + "> Даты проведения мероприятия:\n " + "<t:" + event.getDateStart().getTime() / 1000 + ":d> <t:" + event.getDateStart().getTime() / 1000 + ":t>-<t:" + event.getDateEnd().getTime() / 1000 + ":t>");
        embedBuilder.setColor(Color.decode(event.getType().getColor()));
        ///!!!
        int index = 1;
        if(eventUsers == null){
            embedBuilder.addField("Нет участников", "", false);
        }
        else{
            for (EventUser eventUser : eventUsers) {
                User user = eventUser.getId().getUser();
                embedBuilder.addField(index + ". Награда участника - " + eventUser.getPrize(),
                        " <@" + user.getId() + ">  <:microphone:" + EMOJI_MICROPHONE + "> " + eventUser.getSecondActive() / 60 + "мин. - <:microphonemute:" + EMOJI_MICROPHONE_MUTE + "> " + eventUser.getSecondMute() / 60 + "мин.", false);
                index++;
            }
        }

        if (isReport) embedBuilder.setFooter(member.getEffectiveName(), member.getUser().getAvatarUrl());
        return embedBuilder;
    }

    //сообщение отчёта о выдаче наград ивента
    public static void sendReportAboutEvent(Event ev, List<EventUser> eventUsers, Member member) {
        TextChannel textChannel = guild.getTextChannelById(EVENT_REPORT_CHANNEL);
        assert textChannel != null;
        textChannel.sendMessage("### Отчёт о проведённом ивенте").setEmbeds(sendMsgPrize(ev,eventUsers, member, true).build()).queue();
    }

    //сообщение о расписании ивента на неделю
    public static EmbedBuilder sendMsgGraphic(List<Event> events, LocalDate monday, LocalDate sunday, LocalDate current) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        StringBuilder description = new StringBuilder();
        description.append("# Недельное расписание мероприятий\n");
        description.append("## <:calendar:" + EMOJI_CALENDAR + "> <t:").append(monday.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()).append(":d> - <t:").append(sunday.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()).append(":d>\n");
        embedBuilder.setColor(Color.decode("#5865f2"));
        LocalDate date = monday;
        for (int i = 0; i < 7; i++) {
            String currentString1 = "";
            String currentString2 = "";
            if (date.isEqual(current)) {
                currentString1 = "**";
                currentString2 = "-> ";
            }

            description.append("## ").append(currentString2).append(date.getDayOfMonth())
                    .append(" ").append(getNameDayOfWeek(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.UK))).append("\n");
            StringBuilder eventOnDay = new StringBuilder();
            for (Event event : events) {
                LocalDate localDate = event.getDatePlan().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (localDate.isEqual(date)) {
                    eventOnDay.append(currentString1).append("«").append(event.getType().getName()).append("»  <t:").append(event.getDatePlan().getTime() / 1000).append(":t>\n")
                            .append("Организатор <@").append(event.getEventer().getId()).append(">").append(currentString1).append("\n");
                    break;
                }
            }
            if (eventOnDay.isEmpty())
                eventOnDay.append(currentString1).append("Нет запланированных мероприятий").append(currentString1).append("\n");
            description.append(eventOnDay.toString());

            date = date.plusDays(1);
        }
        embedBuilder.setDescription(description.toString());
        embedBuilder.setImage("https://sun9-66.userapi.com/impg/7-jzX3Spye75xSC4SFU-mdH1SYsBJ-FHSHfT8Q/YVbnePVe7VQ.jpg?size=2000x1000&quality=96&sign=e2e051996dad201ac2b1fa703bacf5b4&type=album");
        return embedBuilder;
    }

    //получение названия недели
    public static String getNameDayOfWeek(String day) {
        switch (day) {
            case ("Mon"):
                return "Понедельник";
            case ("Tue"):
                return "Вторник";
            case ("Wed"):
                return "Среда";
            case ("Thu"):
                return "Четверг";
            case ("Fri"):
                return "Пятница";
            case ("Sat"):
                return "Суббота";
            case ("Sun"):
                return "Воскресенье";
            default:
                return "День";
        }
    }


    //Сообщение при создании тикета
    public static void sendMessageInTicket(TextChannel textChannel, String textMsg) {
        Button addUserInTicketButton = Button.of(ButtonStyle.PRIMARY, "add-user-in-ticket3", "Добавить участника").withEmoji(guild.getEmojiById(EMOJI_ADD_USER));
        Button closeTicketButton = Button.of(ButtonStyle.SECONDARY, "close-ticket3", "Закрыть тикет").withEmoji(guild.getEmojiById(1238511202385002527L));
        Button acceptTicketButton = Button.of(ButtonStyle.PRIMARY, "accept-ticket3", "Принять тикет").withEmoji(guild.getEmojiById(1238969983338418197L));
        List<ItemComponent> components = new ArrayList<>();
        components.add(addUserInTicketButton);
        components.add(acceptTicketButton);
        components.add(closeTicketButton);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription(textMsg);
        embedBuilder.setColor(Color.decode("#5865f2"));

        Message message = textChannel.sendMessageEmbeds(embedBuilder.build()).addActionRow(components).complete();
        message.pin().queue();
    }

    //Сообщение при закрытии тикета
    public static void sendMsgCloseTicket(TextChannel textChannel, Long moderator) {
        Button solutionButton = Button.of(ButtonStyle.PRIMARY, "solution-button-ticket3", "Закончить обсуждение");
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription("## <a:05e469d61100da5174d7628e11e8a8c5:1238969981094334538>   Итоги обсуждения\n" +
                "### Поступило сообщение, что беседа подошла к концу. \n" +
                "### <@" + moderator + "> нажав на кнопку расскажи Утёнку о принятом решении. \n" +
                "**Внимание!!** *После того, как Утёнок успешно получит результат беседы, данный тикет будет удалён.*");
        embedBuilder.setColor(Color.decode("#5865f2"));
        textChannel.sendMessageEmbeds(embedBuilder.build()).addActionRow(solutionButton).queue();
    }

    //Сообщение о новом владельце голосового канала
    public void sendMsgAboutNewHost(VoiceChannel voiceChannel, User newHost) {
        voiceChannel.sendMessage("## <a:clapclap:1193322433206308984>  Новый владелец комнаты - <@" + newHost.getId() + ">\n" +
                "**Для управления комнаткой сюда -> <#" + YOUR_VOICE_ROOM_EDIT + "> **").queue();
    }

    public static void deleteHistoryOnChannel(String idChatChannel) {
        TextChannel chatChannel = guild.getTextChannelById(idChatChannel);
        assert chatChannel != null;
        MessageHistory history = chatChannel.getHistory();
        chatChannel.deleteMessages(history.getRetrievedHistory());
    }
}
