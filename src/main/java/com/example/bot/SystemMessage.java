package com.example.bot;

import com.example.bot.entity.User;
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
import net.dv8tion.jda.internal.entities.emoji.CustomEmojiImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.example.bot.BotApplication.guild;
import static com.example.bot.VoiceRoomListener.CREATE_PRIVATE_VOICE_ROOM_ID;

public class SystemMessage {
    public static String YOUR_VOICE_ROOM_EDIT = "1181326695408619560";
    public static String CROCODILE_GAME = "1236431656395608074";
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
            //можно добавить что-то по прошлым играм


            Collection<ActionRow> actionRows = new ArrayList<>();
            actionRows.add(ActionRow.of(takeWordButton));
            actionRows.add(ActionRow.of(statisticsButton));
            actionRows.add(ActionRow.of(bestPlayersButton));

            chatChannel.sendMessage("\uD83D\uDC0A Давай сыграем в крокодила \uD83D\uDC0A").addComponents(actionRows).queue();
        }
    }

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
