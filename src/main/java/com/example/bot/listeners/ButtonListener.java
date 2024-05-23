package com.example.bot.listeners;

import jakarta.validation.constraints.NotNull;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;


public class ButtonListener extends ListenerAdapter {
    private final String[] BAD_WORDS = {"блять"};


    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        for (String badWord : BAD_WORDS) {
            if (event.getMessage().getContentRaw().contains(badWord)) {
                event.getChannel().sendMessage("<@" + event.getMessage().getAuthor().getId() + ">, не руговайся тут").queue();
                TextChannel warnChannel = event.getJDA().getTextChannelById("1181334948679004180");
                if (warnChannel != null) {
                    Button removeButton = Button.danger("remove-message", "Удалить плохое сообщение");
                    /*Message message = new MessageBuilder()
                            .append("<@")
                            .append(event.getAuthor().getId())
                            .append("> руговается. Нажмите на кнопку, чтобы удалить его сообщение\n id: " + event.getMessageId()
                                    + "\nКанал id: " + event.getChannel().getId())
                            .setActionRows(ActionRow.of(removeButton))
                            .build();
                    warnChannel.sendMessage(message).queue();*/
                }
            }
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getButton().getId().equals("remove-message")) {
            String[] content = event.getMessage().getContentRaw().split("id: ");
            String channelID = content[2];
            String messageID = content[1].split("\n")[0];

          event.getGuild().getTextChannelById(channelID).deleteMessageById(messageID).queue();

            event.reply("Сообщение: " + messageID + " удалено").queue();

            TextChannel logChannel = event.getJDA().getTextChannelById("1182227799646416926");
            if (logChannel != null) {
                /*Message message = new MessageBuilder()
                        .append("Сообщение ")
                        .append(messageID)
                        .append(" было удалено Утёнком")
                        .build();
                logChannel.sendMessage(message).queue();*/
        }
        }
    }
}
