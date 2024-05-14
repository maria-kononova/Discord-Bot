package com.example.bot;


import com.example.bot.entity.User;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static com.example.bot.BotApplication.guild;
import static com.example.bot.BotCommands.userRepository;


@Service
@Configuration
@EnableScheduling
@EnableAsync
@ConditionalOnProperty(name="scheduler.enabled", matchIfMissing = true)
public class UpdateServer extends ListenerAdapter {
    //static UserRepository userRepository;
    JDA bot;
    static final Logger LOGGER = Logger.getLogger(UpdateServer.class.getName());

    @Scheduled(fixedRate = 60000)
    @Async
    public void refreshPricingParameters() {
        if (guild != null){

            List<VoiceChannel> channels = guild.getVoiceChannels();
            for(VoiceChannel channel : channels){
                List<Member> members = channel.getMembers();
                for(Member member : members){
                    User user = userRepository.getUserById(member.getIdLong());
                    if (member.isBoosting()) {
                        user.update(3);
                    } else if(Objects.requireNonNull(member.getVoiceState()).isDeafened()){

                    }
                    else {
                        user.update(1);
                    }
                    userRepository.save(user);
                }
            }
        }
    }

//    @Bean
//    public CommandLineRunner update(UserRepository repository) {
//        return (args) -> {
//            userRepository = repository;
//        };
//    }
}
