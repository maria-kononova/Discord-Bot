package com.example.bot.listeners;

import com.example.bot.SystemMessage;
import com.example.bot.entity.MessageCommand;
import com.example.bot.entity.User;
import com.example.bot.enums;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.bot.BotApplication.guild;
import static com.example.bot.BotApplication.messageCommandRepository;
import static com.example.bot.listeners.BotCommands.gifRepository;
import static com.example.bot.listeners.BotCommands.userRepository;
import static com.example.bot.listeners.ControlListener.*;


public class MessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            List<MessageCommand> messageCommands = messageCommandRepository.findAll();
            for (MessageCommand messageCommand : messageCommands) {
                String message = messageCommand.check(event.getMessage().getContentRaw());
                if (message != null) {

                    if (message.contains("<:id_member:>")) {
                        System.out.println("ok");
                        message = message.replace("<:id_member:>", event.getMember().getAsMention());
                    }
                    if (message.contains("<:moderator:>")) {
                        message = message.replace("<:moderator:>", "<@&" + MODERATOR_ROLE + ">");
                    }
                    if (message.contains("<:support:>")) {
                        message = message.replace("<:support:>", "<@&" + SUPPORT_ROLE + ">");
                    }
                    event.getChannel().sendMessage(message).queue();
                }
            }
            User user = userRepository.getUserById(event.getAuthor().getIdLong());
            user.sendMsg();
            userRepository.save(user);
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getMessageId().equals("1243015089448292363")) {
            Long white = 1219669119834656878L;
            Long yellow = 1238511202385002527L;
            Role roleWhite = guild.getRoleById(1222908231811207238L);
            Role roleYellow = guild.getRoleById(1222908121702465727L);
            Message message = event.getChannel().asTextChannel().retrieveMessageById(event.getMessageId()).complete();
            if (event.getReaction().getEmoji().asCustom().getIdLong() == white) {
                message.removeReaction(guild.getEmojiById(yellow),event.getUser()).queue();
                guild.addRoleToMember(event.getMember(), roleWhite).queue();
                guild.removeRoleFromMember(event.getMember(), roleYellow).queue();
            }
            if (event.getReaction().getEmoji().asCustom().getIdLong() == yellow) {
                message.removeReaction(guild.getEmojiById(white),event.getUser()).queue();
                guild.addRoleToMember(event.getMember(), roleYellow).queue();
                guild.removeRoleFromMember(event.getMember(), roleWhite).queue();
            }
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if (event.getMessageId().equals("1243015089448292363")) {
            Long white = 1219669119834656878L;
            Long yellow = 1238511202385002527L;
            Role roleWhite = guild.getRoleById(1222908231811207238L);
            Role roleYellow = guild.getRoleById(1222908121702465727L);
            if (event.getReaction().getEmoji().asCustom().getIdLong() == white) {
                guild.removeRoleFromMember(event.getMember(), roleWhite).queue();
            }
            if (event.getReaction().getEmoji().asCustom().getIdLong() == yellow) {
                guild.removeRoleFromMember(event.getMember(), roleYellow).queue();
            }
        }
    }
}
