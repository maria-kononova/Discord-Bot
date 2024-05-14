package com.example.bot.events;

import com.example.bot.entity.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static com.example.bot.BotCommands.userRepository;


public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            //System.out.println(event.getMessage());
            User user = userRepository.getUserById(event.getAuthor().getIdLong());
            user.sendMsg();
            userRepository.save(user);
        }
    }
}
