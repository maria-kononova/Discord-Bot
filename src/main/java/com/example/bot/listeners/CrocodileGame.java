package com.example.bot.listeners;

import com.example.bot.entity.Crocodile;
import com.example.bot.entity.EventUser;
import com.example.bot.entity.User;
import com.example.bot.entity.Warning;
import jakarta.validation.constraints.NotNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.springframework.core.io.ClassPathResource;


import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.bot.BotApplication.*;
import static com.example.bot.SystemMessage.sendMsgFormPost;
import static com.example.bot.listeners.BotCommands.userRepository;
import static com.example.bot.SystemMessage.CROCODILE_GAME;

public class CrocodileGame extends ListenerAdapter {
    private String word = "что-то";
    private static Crocodile userPlay;
    private Boolean hasMessageFromUserPlay = false;

    private static Timer timer;
    private static TimerTask task;

    private static void startTimer() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                stopTimer();
                TextChannel textChannel = guild.getTextChannelById(CROCODILE_GAME);
                Button takeWordButton = Button.of(ButtonStyle.PRIMARY, "take-word-crocodile-game2", "Взять новое слово");
                Collection<ActionRow> actionRows = new ArrayList<>();
                actionRows.add(ActionRow.of(takeWordButton));
                textChannel.sendMessage("<@" + userPlay.getUser().getId() + "> слишком долго думаешь и получаешь штраф! Не отвлекайтесь от игры и не мешайте другим :с").addComponents(actionRows).queue();
                userPlay.fine();
                crocodileRepository.save(userPlay);
                userPlay = null;
            }
        };

        timer.scheduleAtFixedRate(task, 300000, 60000);
    }

    private static void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    //идентификатор команд 2
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getButton().getId().contains("crocodile-game2")) {
            switch (event.getButton().getId()) {
                case ("take-word-crocodile-game2"): {
                    if (userPlay == null) {
                        //присоединение пользователя к игре крокодила
                        if (crocodileRepository.findByUser(userRepository.getUserById(event.getMember().getIdLong())) == null) {
                            createNewPlayer(event.getUser().getIdLong());
                        }
                        userPlay = crocodileRepository.findByUser(userRepository.getUserById(event.getMember().getIdLong()));
                    }
                    if (userPlay.getUser().getId() == Objects.requireNonNull(event.getMember()).getIdLong()) {
                        if (userPlay.getPenalty() == 0) {
                            Button updateWordButton = Button.of(ButtonStyle.PRIMARY, "update-word-crocodile-game2", "Обновить слово");
                            Button endGameButton = Button.of(ButtonStyle.SECONDARY, "end-crocodile-game2", "Выйти из игры");
                            List<ItemComponent> components = new ArrayList<>();
                            components.add(updateWordButton);
                            components.add(endGameButton);
                            ActionRow actionRow = ActionRow.of(components);
                            getNewWord();
                            //отправка сообщения о том, кто же загадывает слово
                            TextChannel textChannel = guild.getTextChannelById(CROCODILE_GAME);
                            if (event.getMember().getIdLong() == 839545273344851990L) {
                                textChannel.sendMessage("<@" + event.getMember().getIdLong() + "> думает над словом по-жомски").queue();
                            }
                            textChannel.sendMessage("<@" + event.getMember().getIdLong() + "> " + getPhraseStartGame()).queue();
                            event.deferReply(true).setContent("Твоё слово: " + word).addComponents(actionRow).queue();
                            startTimer();
                        } else {
                            event.deferReply(true).setContent("Тебя наказали за косяки в игре :с\nТы не можешь загадывать слова, пока у тебя есть штрафы. Чтобы снять штраф необходимо отгадать слово.\nНеобходимое количество: " + userPlay.getPenalty()).queue();
                            userPlay = null;
                        }
                    } else
                        event.deferReply(true).setContent("У-упс, кто-то успел до тебя. Попробуй в следующий раз, а пока отгадывай!").queue();
                    break;
                }
                case ("update-word-crocodile-game2"): {
                    if (userPlay.getUser().getId() == event.getMember().getIdLong()) {
                        getNewWord();
                        Button updateWordButton = Button.of(ButtonStyle.PRIMARY, "update-word-crocodile-game2", "Обновить слово");
                        Button endGameButton = Button.of(ButtonStyle.SECONDARY, "end-crocodile-game2", "Выйти из игры");
                        List<ItemComponent> components = new ArrayList<>();
                        components.add(updateWordButton);
                        components.add(endGameButton);
                        ActionRow actionRow = ActionRow.of(components);
                        event.deferReply(true).setContent("Твоё новое слово: " + word).addComponents(actionRow).queue();
                    } else {
                        event.deferReply(true).setContent("Жулик, не воруй! Не твоё слово, попробуй в следующий раз!!").queue();
                    }
                    break;
                }
                case ("end-crocodile-game2"): {
                    stopTimer();
                    Button takeWordButton = Button.of(ButtonStyle.PRIMARY, "take-word-crocodile-game2", "Взять новое слово");
                    Collection<ActionRow> actionRows = new ArrayList<>();
                    actionRows.add(ActionRow.of(takeWordButton));
                    event.deferReply(false).setContent("<@" + userPlay.getUser().getId() + "> выходит из игры. Слово обновлено, можете брать кто хочет!").addComponents(actionRows).queue();
                    userPlay = null;
                    break;
                }
                case ("statistics-crocodile-game2"): {
                    EmbedBuilder embedBuilder = getStatisticsOfUser(event.getMember());
                    if(embedBuilder != null){
                        Button button = Button.success("update-statistics-crocodile-game2", "Обновить");
                        event.deferReply(true).setEmbeds(getStatisticsOfUser(event.getMember()).build())
                                .addActionRow(button)
                                .queue();
                    }
                    else event.deferReply(true).setContent("Статистика отсутствует. Скорее присоединяйся к игре!").queue();
                }
                case ("update-statistics-crocodile-game2"): {
                    event.deferEdit().setEmbeds(getStatisticsOfUser(event.getMember()).build()).queue();
                }
                case("best-players-crocodile-game2"):{
                    EmbedBuilder embedBuilder = getRatingsBestPlayers();
                    if(embedBuilder != null){
                        Button button = Button.success("update-rating-crocodile-game2", "Обновить");
                        event.deferReply(true).setEmbeds(getRatingsBestPlayers().build())
                                .addActionRow(button)
                                .queue();
                    }
                    else event.deferReply(true).setContent("Рейтинг отсутствует. Скорее начинайте игру!").queue();
                }
                case("update-rating-crocodile-game2"):{
                    event.deferEdit().setEmbeds(getRatingsBestPlayers().build()).queue();
                }
            }
        }
    }

    public EmbedBuilder getRatingsBestPlayers(){
        List<Crocodile> crocodiles = getListCrocodileSortedByScore();
        if(crocodiles.size() != 0){
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Color.decode("#248046"));
            embedBuilder.setTitle("Рейтинг игроков");
            int i = 1;
            for(Crocodile crocodile : crocodiles){
                if(i<=10){
                    embedBuilder.addField(i + ". " + guild.getMemberById(crocodile.getUser().getId()).getEffectiveName(), "**Набранно очков:  " + crocodile.getScore() + "**", false );
                    i++;
                }else break;
            }
            return embedBuilder;
        }
        return null;
    }

    public List<Crocodile> getListCrocodileSortedByScore(){
        List<Crocodile> crocodiles = crocodileRepository.findAll();
        Comparator<Crocodile> comparator = Comparator.comparing(Crocodile::getScore);
        crocodiles.sort(comparator);
        Collections.reverse(crocodiles);
        return crocodiles;
    }

    public EmbedBuilder getStatisticsOfUser(Member member) {
        User user = userRepository.getUserById(member.getIdLong());
        Crocodile crocodile = crocodileRepository.findByUser(user);
        if (crocodile != null) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Color.decode("#248046"));
            embedBuilder.setTitle("Статистика  " + member.getEffectiveName());
            embedBuilder.setThumbnail("https://media.tenor.com/B-h20huQWAYAAAAi/hide-water.gif");
            embedBuilder.setDescription("### Топ:   **" + 1 + "**  |||  Рейтинг:   **" + crocodile.getScore() + "**");
            embedBuilder.addField("Отгадано слов:", "> **" + String.valueOf(crocodile.getGuessedWords()) + "**", true);
            embedBuilder.addField("Загадано слов:", "> **" + String.valueOf(crocodile.getSetWords()) + "**", true);
            embedBuilder.addField("Штрафы:", "> **" + String.valueOf(crocodile.getPenalty()) + "**", true);
            return embedBuilder;
        }
        return null;
    }

    //проверка угаданного слова
    //можно придумать что-то более детальное
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannel().getId().equals(CROCODILE_GAME) && !event.getAuthor().isBot()) {
            //добавление пользователя в БД
            if (crocodileRepository.findByUser(userRepository.getUserById(event.getAuthor().getIdLong())) == null) {
                createNewPlayer(event.getAuthor().getIdLong());
            }
            if (event.getAuthor().getIdLong() == userPlay.getUser().getId()) {
                hasMessageFromUserPlay = true;
                stopTimer();
            }
            //сообщение содержит отгаданное слово
            if (event.getMessage().getContentRaw().equalsIgnoreCase(word)) {
                Button takeWordButton = Button.of(ButtonStyle.PRIMARY, "take-word-crocodile-game2", "Взять новое слово");
                Collection<ActionRow> actionRows = new ArrayList<>();
                actionRows.add(ActionRow.of(takeWordButton));
                //в сообщении есть загаданное слово
                if (event.getAuthor().getIdLong() != userPlay.getUser().getId()) {
                    Crocodile crocodileGuess = crocodileRepository.findByUser(userRepository.getUserById(event.getAuthor().getIdLong()));
                    if (hasMessageFromUserPlay) {
                        String penaltyString = "";
                        if (crocodileGuess.getPenalty() != 0) {
                            if (crocodileGuess.getPenalty() - 1 > 0)
                                penaltyString = "\nОставшиеся штрафы: **" + (crocodileGuess.getPenalty() - 1) + "**";
                            else penaltyString = "\nСо штрафами покончено. Можешь загадывать слова!";
                        }
                        event.getMessage().reply("<@" + event.getAuthor().getIdLong() + "> " + getPhraseGuessWord() + penaltyString).addComponents(actionRows).queue();
                        crocodileGuess.guessWord();
                        userPlay.setWord();
                    } else {
                        userPlay.fine();
                        crocodileGuess.fine();
                        event.getMessage().reply("Не загадано = не отгадано. Вы кого тут обмануть пытаетесь? -50 рейтинга каждому и штрафы до кучи =^=").addComponents(actionRows).queue();
                    }
                    crocodileRepository.save(crocodileGuess);
                }
                //загаданное слово написал вода = ему штраф и новое слово
                else {
                    event.getMessage().reply("Плохо играть не честно. -50 рейтинга и 3 штрафа (снимаются отгадыванием слов)").addComponents(actionRows).queue();
                    userPlay.fine();
                }
                crocodileRepository.save(userPlay);
                userPlay = null;
            }
        }
    }

    //обавление нового пользователя в БД Крокодила
    public void createNewPlayer(Long idUser) {
        Crocodile newPlayer = new Crocodile(userRepository.getUserById(idUser));
        crocodileRepository.save(newPlayer);
    }


    //получение фразы о выигрыше
    public String getPhraseGuessWord() {
        List<String> phrases = Arrays.asList(" угадывает слово!", "определённо говорит правильно!!", "как всегда харош!", "ура победа", "я всегда в тебя верил!",
                "догадывается!!", "достойно угадывает слово", "ой ты головатстый, правильно!", "ты ж моя уточка, всё правильно!", "зёрнышко тебе в карман, угадашка"); //додумать
        Random random = new Random();
        return phrases.get(random.nextInt(0, phrases.size()));
    }

    //получение фразы о том, кто закадывает слово
    public String getPhraseStartGame() {
        List<String> phrases = Arrays.asList("загадывает нам словечко", "усердно думает над словом!", "думает-думает", "водит этот кон",
                "берёт слово, время пошло!", "переходит в наступление"); //додумать
        Random random = new Random();
        return phrases.get(random.nextInt(0, phrases.size()));
    }

    //обновление слова рандомное из файла
    public void getNewWord() {
        try {
            Random random = new Random();
           /* Random random = new Random();
            Resource resource = resourceLoader.getResource("classpath:static/words.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            word = reader.lines().toList().get(random.nextInt(0, reader.lines().toList().size()));
            reader.close();*/
            ClassPathResource resource = new ClassPathResource("static/words.txt");
            InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            List<String> lines = reader.lines().toList();
            word = lines.get(random.nextInt(0, lines.size()));


            //word = Files.readAllLines(Path.of(this.getClass().getClassLoader().getResource("static/words.txt").getPath())).get(lineNumber);
            //crocodileGame.setRound(crocodileGame.getRound() + 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
