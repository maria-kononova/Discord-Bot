package com.example.bot;

import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.ArrayList;
import java.util.Collection;

import static com.example.bot.BotApplication.guild;

public class SystemMessage {
    public static String YOUR_VOICE_ROOM_EDIT = "1181326695408619560";
    public static String CROCODILE_GAME = "1236431656395608074";

    //сообщение в чат "твоя-комната", позволяет редактировать свой голосовой канал
    //идентификатор команды 1
    public static void sendVoiceControlMessage() {
        //deleteHistoryOnChannel(YOUR_VOICE_ROOM_EDIT);
        TextChannel chatChannel = guild.getTextChannelById(YOUR_VOICE_ROOM_EDIT);
        if (chatChannel != null) {

            Button changeNameButton = Button.of(ButtonStyle.PRIMARY, "change-voice-name1", "Изменить название канала");
            Button limitUsersButton = Button.of(ButtonStyle.PRIMARY, "change-limit-users-voice1", "Изменить количество пользователей");
            Button newHostButton = Button.of(ButtonStyle.PRIMARY, "change-host-voice1", "Изменить владельца канала");
            Button closeOrOpenButton = Button.of(ButtonStyle.SECONDARY, "change-state-voice1", "Закртыть/открыть канал");
            Button kickUserButton = Button.of(ButtonStyle.SECONDARY, "kick-user-voice1", "Выгнать пользователя");

            Collection<ActionRow> actionRows = new ArrayList<>();
            actionRows.add(ActionRow.of(changeNameButton));
            actionRows.add(ActionRow.of(newHostButton));
            actionRows.add(ActionRow.of(limitUsersButton));
            actionRows.add(ActionRow.of(closeOrOpenButton));
            actionRows.add(ActionRow.of(kickUserButton));

            chatChannel.sendMessage("Выберите необходимое для управления своей комнатой:").addComponents(actionRows).queue();
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
