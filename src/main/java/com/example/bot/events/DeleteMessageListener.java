package com.example.bot.events;

import jakarta.validation.constraints.NotNull;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DeleteMessageListener extends ListenerAdapter {
    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        //event.getChannel().sendMessage("<@" + event.getMessage().getAuthor().getId() + ">, не руговайся тут").queue();

        TextChannel logChannel = event.getJDA().getTextChannelById("1182227799646416926");
        String messageDeletedId = event.getMessageId();
       if (logChannel != null){
           logChannel.sendMessage("Сообщение " + messageDeletedId + " было удалено").queue();
        }
    }
}
