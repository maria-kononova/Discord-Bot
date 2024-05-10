package com.example.bot;

import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
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

public class SystemMessage {
    public static String YOUR_VOICE_ROOM_EDIT = "1181326695408619560";
    public static String CROCODILE_GAME = "1236431656395608074";

    public static String EMOJI_EDIT = "1238476817132949645";
    public static  String EMOJI_CROWN = "1238478062748700672";
    public static String EMOJI_USERS = "1238479272063275058";
    public static String EMOJI_LOCK = "1238486183655374888";
    public static String EMOJI_OPEN = "1238487354830753863";
    public static String EMOJI_KICK_USER = "1238488184803823626";


    //сообщение в чат "твоя-комната", позволяет редактировать свой голосовой канал
    //идентификатор команды 1
    public static void sendVoiceControlMessage() {
        //deleteHistoryOnChannel(YOUR_VOICE_ROOM_EDIT);
        TextChannel chatChannel = guild.getTextChannelById(YOUR_VOICE_ROOM_EDIT);
        if (chatChannel != null) {

        Button changeNameButton = Button.primary("change-voice-name1", Objects.requireNonNull(guild.getEmojiById(EMOJI_EDIT)));//"Изменить название канала");
            Button limitUsersButton = Button.primary( "change-limit-users-voice1",Objects.requireNonNull(guild.getEmojiById(EMOJI_USERS))); //"Изменить количество пользователей");
            Button newHostButton = Button.primary("change-host-voice1",Objects.requireNonNull(guild.getEmojiById(EMOJI_CROWN))); //"Изменить владельца канала");
            Button openRoomButton = Button.secondary("open-room-voice1", Objects.requireNonNull(guild.getEmojiById(EMOJI_OPEN))); //"Открыть канал");
            Button closeRoomButton = Button.secondary( "close-room-voice1", Objects.requireNonNull(guild.getEmojiById(EMOJI_LOCK))); //"Закртыть канал");
            Button kickUserButton = Button.secondary("kick-user-voice1", Objects.requireNonNull(guild.getEmojiById(EMOJI_KICK_USER))); //"Выгнать пользователя");


            //Button btn = Button.primary("btn", Emoji.fromCustom(Objects.requireNonNull(guild.getEmojiById("1220809885059645470"))));
            List<ItemComponent> componentsPrimary = new ArrayList<>();
            componentsPrimary.add(changeNameButton);
            componentsPrimary.add(limitUsersButton);
            componentsPrimary.add(newHostButton);
            ActionRow actionRowPrimary = ActionRow.of(componentsPrimary);

            List<ItemComponent> componentsSecondary = new ArrayList<>();
            componentsSecondary.add(openRoomButton);
            componentsSecondary.add(closeRoomButton);
            componentsSecondary.add(kickUserButton);
            ActionRow actionRowSecondary = ActionRow.of(componentsSecondary);


            Collection<ActionRow> actionRows1 = new ArrayList<>();
            actionRows1.add(actionRowPrimary);
            actionRows1.add(actionRowSecondary);


            chatChannel.sendMessage("Выберите необходимое для управления своей комнатой:").addComponents(actionRows1).queue();
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

    public static void deleteHistoryOnChannel(String idChatChannel){
        TextChannel chatChannel = guild.getTextChannelById(idChatChannel);
        assert chatChannel != null;
        MessageHistory history = chatChannel.getHistory();
        chatChannel.deleteMessages(history.getRetrievedHistory());
    }
}
