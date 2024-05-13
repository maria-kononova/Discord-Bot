package com.example.bot;

import com.example.bot.entity.User;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.internal.entities.emoji.CustomEmojiImpl;
import net.dv8tion.jda.internal.interactions.component.StringSelectMenuImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.example.bot.BotApplication.guild;
import static com.example.bot.VoiceRoomListener.CREATE_PRIVATE_VOICE_ROOM_ID;

public class SystemMessage {
    public static String INFO_CHANNEL = "1181342904279629864";
    public static String RULES_CHANNEL = "1181342967986921575";
    public static String YOUR_VOICE_ROOM_EDIT = "1181326695408619560";
    public static String CROCODILE_GAME = "1236431656395608074";
    public static String CREATE_TICKET_CHANNEL = "1181346855108493323";
    public static String EMOJI_EDIT = "1238534141943873688";
    public static String EMOJI_CROWN = "1238535149218693200";
    public static String EMOJI_USERS = "1238534150483345449";
    public static String EMOJI_LOCK = "1238530420438597662";
    public static String EMOJI_OPEN = "1238530418915934320";
    public static String EMOJI_KICK_USER = "1238534143835377664";
    public static String EMOJI_BAN_USER = "1238592212556709938";
    public static String EMOJI_ADD_USER = "1238534146192834652";


    //сообщение в чат "твоя-комната", позволяет редактировать свой голосовой канал
    //идентификатор команды 1
    public static void sendVoiceControlMessage() {
        //deleteHistoryOnChannel(YOUR_VOICE_ROOM_EDIT);
        TextChannel chatChannel = guild.getTextChannelById(YOUR_VOICE_ROOM_EDIT);
        if (chatChannel != null) {

            Button changeNameButton = Button.primary("change-voice-name1", Objects.requireNonNull(guild.getEmojiById(EMOJI_EDIT)));//"Изменить название канала");
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


            chatChannel.sendMessage("## <a:1907duckwow:1238511205316820992>   **Здесь ты можешь управлять своей комнаткой** <a:1907duckwow:1238511205316820992>  \n" +
                    "\n" +
                    "Для того, чтобы получить личный голосовой канал, достаточно кликнуть сюда -> **<#" + CREATE_PRIVATE_VOICE_ROOM_ID + ">**\n" +
                    "После успешного создания своей комнатки, можно воспользоваться следующими командами для управления:\n" +
                    "> <:edit:" + EMOJI_EDIT + ">  **- Изменить имя канала**\n" +
                    "> <:users:" + EMOJI_USERS + ">  **- Установить лимит пользователей в канале**\n" +
                    "> <:crown:" + EMOJI_CROWN + ">  **- Передать права владельца канала**\n" +
                    "> <:add_user:" + EMOJI_ADD_USER + ">  **- Дать доступ пользователю к каналу**\n" +
                    "> <:lock:" + EMOJI_LOCK + ">  **- Закрыть канал от всех**\n" +
                    "> <:open:" + EMOJI_OPEN + ">  **- Открыть канал для всеобщего доступа**\n" +
                    "> <:kick_user:" + EMOJI_KICK_USER + ">  **- Исключить пользователя из канала**\n" +
                    "> <:ban_user:" + EMOJI_BAN_USER + ">  **- Забрать доступ к каналу у пользователя**\n").addComponents(actionRows).queue();
        }
    }

    //идентификатор команды 2
    public static void sendCrocodileGameMessage() {
        //deleteHistoryOnChannel(YOUR_VOICE_ROOM_EDIT);
        TextChannel chatChannel = guild.getTextChannelById(CROCODILE_GAME);
        if (chatChannel != null) {

            Button takeWordButton = Button.of(ButtonStyle.PRIMARY, "take-word-crocodile-game2", "Взять слово");
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

            chatChannel.sendMessage("## :crocodile:  Давай сыграем в крокодила :crocodile:\n" +
                    "**Правила игры:**\n" +
                    "> **1.** Один из участников становится ведущим, нажав на кнопку  **«Взять слово»**, и Утёнок загадывает ему слово. \n" +
                    "> Задача ведущего объяснить это слово, не называя его (нельзя использовать разные формы этого слова и однокоренные к нему). \n" +
                    "> **2.** Другие участники пытаются отгадать загаданное слово по объяснению ведущего.\n" +
                    "> **3.** На загадывание слова ведущему даётся **5 минут**,  на его отгадывание — **10 минут** с начала раунда. \n" +
                    "> В случае истечения времени Утёнок подготавливает новое слово.\n" +
                    "> **4.** За успешно угаданное слово игроку начисляется **5 баллов** рейтинга, за загаданное - **10 баллов**.\n" +
                    "> **5.** Игроку выдаются штрафы за нарушения правил игры, которые понижают его рейтинг и отстраняют от возможности быть ведущим.\n" +
                    "\n" +
                    "**Штрафы могут быть выданы, если:**\n" +
                    "> - Ведущий раскрыл загаданное слово.\n" +
                    "> - Ведущий не уложился в 5 минут на загадывание слова. \n" +
                    "> - Игрок угадал ещё не загаданное слово (подозрение на нечестную игру).\n" +
                    "\n" +
                    "*Любой может пожаловаться на игрока, который играет нечестно, тогда Утёнок самостоятельно проведёт проверку.*").addComponents(actionRow).queue();
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

            StringSelectMenuImpl selectMenu = new StringSelectMenuImpl("type-select-problem3", "Для начала выбери тип тикета", 1, 1, false, selectOptions);


            chatChannel.sendMessage("## <a:discordduck:1238862338384330773>  Создание тикета <a:discordduck:1238862338384330773> \n" +
                    "**Тикет - обращение к администрации и модерации сервера через Утёнка.**\n" +
                    "Создавать тикет следует в случае:\n" +
                    "> - Замеченного нарушения правил.\n" +
                    "> - Возникновения особых вопросов.\n" +
                    "> - Некорректной работы Утёнка.\n" +
                    "\n" +
                    "**Перед созданием тикета следует убедиться, что он необходим.** \n" +
                    "Утёнок советует проверить нет ли информации о назревшем вопросе в каналах  <#" + INFO_CHANNEL + ">  и  <#" + RULES_CHANNEL + ">").addActionRow(selectMenu).queue();
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
        Message message = textChannel.sendMessage(textMsg).addActionRow(components).complete();
        message.pin().queue();
    }

    //Сообщение при закрытии тикета
    public static void sendMsgCloseTicket(TextChannel textChannel, Long moderator) {
        Button solutionButton = Button.of(ButtonStyle.PRIMARY, "solution-button-ticket3", "Закончить обсуждение");
        textChannel.sendMessage("## <a:05e469d61100da5174d7628e11e8a8c5:1238969981094334538>   Итоги обсуждения\n" +
                "Поступило сообщение, что беседа подошла к концу. \n" +
                "<@" + moderator + "> нажав на кнопку расскажи Утёнку о принятом решении. \n" +
                "**Внимание!!** После того, как Утёнок успешно получит результат беседы, данный тикет будет удалён.").addActionRow(solutionButton).queue();
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
