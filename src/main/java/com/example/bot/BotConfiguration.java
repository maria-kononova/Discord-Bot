package com.example.bot;

import com.example.bot.listeners.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.example.bot.BotApplication.bot;
import static com.example.bot.BotApplication.guild;

@Configuration
public class BotConfiguration {

    //private static final Logger log = LoggerFactory.getLogger( BotConfiguration.class );

    @Value("${bot.token}")
    private String token;
    @Bean
    public JDA gatewayDiscordClient() throws InterruptedException {
        bot = JDABuilder.createDefault(token)
                .setActivity(Activity.competing("кряканье"))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setChunkingFilter(ChunkingFilter.ALL) // enable member chunking for all guilds
                .addEventListeners(new BotCommands(), new DeleteMessageListener(),
                        new VoiceRoomListener(), new MessageListener(), new CrocodileGame(), new Tickets(),
                        new EventListener(), new ControlListener(), new ModerationListener(),
                        new ShopListener())
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build().awaitReady();
        return bot;
    }
}
