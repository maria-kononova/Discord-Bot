package com.example.bot.listeners;

import com.example.bot.SystemMessage;
import com.example.bot.entity.User;
import com.example.bot.entity.VoiceChannel;
import com.example.bot.repository.VoiceChannelRepository;
import jakarta.validation.constraints.NotNull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.example.bot.BotApplication.guild;
import static com.example.bot.listeners.BotCommands.userRepository;

@Transactional
@Service
public class VoiceRoomListener extends ListenerAdapter {
    static VoiceChannelRepository voiceChannelRepository;
    public static String CATEGORY_PRIVATE_VOICE_ROOM_ID = "1181326394874146948";
    public static String CREATE_PRIVATE_VOICE_ROOM_ID = "1191791786365042749";

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        //без понятия что это. возможно проверка работы ?
        if (event.getButton().getId().equals("yes-button")) {
            event.reply("ok").queue();
        }
        if (event.getButton().getId().contains("voice1")) {
            User user = userRepository.getUserById(event.getUser().getIdLong());
            if (checkUserForIntrButton(user)) {
                switch (event.getButton().getId()) {
                    //изменение название команты
                    case ("change-name-voice1"): {
                        TextInput nameRoom = TextInput.create("new-name-voice-channel1", "Название", TextInputStyle.SHORT)
                                .setMinLength(3)
                                .setMaxLength(20)
                                .setPlaceholder("Название для своей комнатки сюда")
                                .build();

                        Modal modal = Modal.create("name-voice-channel-modal1", "Изменение названия комнаты")
                                .addActionRows(ActionRow.of(nameRoom)).build();
                        event.replyModal(modal).queue();
                        break;
                    }
                    //изменение владельца команты
                    case ("change-host-voice1"): {
                        SelectMenu selectMenu = EntitySelectMenu.create("select-new-host-room1", EntitySelectMenu.SelectTarget.USER)
                                .setPlaceholder("Наш новый владелец...")
                                .setRequiredRange(1, 1)
                                .build();
                        event.deferReply(true).setContent("Выбери нового владельца своей комнатки из списка").setComponents(ActionRow.of(selectMenu)).queue();
                        break;
                    }
                    //пригласить пользователя в комнатку
                    case ("add-user-voice1"): {
                        SelectMenu selectMenu = EntitySelectMenu.create("select-add-user-room1", EntitySelectMenu.SelectTarget.USER)
                                .setPlaceholder("Кому мы высылаем приглашение?")
                                .setRequiredRange(1, 1)
                                .build();
                        event.deferReply(true).setContent("Выбери нового гостя своей комнатки").setComponents(ActionRow.of(selectMenu)).queue();
                        break;
                    }
                    //забрать доступ у пользователя на присоединения к комантке
                    case ("ban-user-voice1"): {
                        SelectMenu selectMenu = EntitySelectMenu.create("select-ban-user-room1", EntitySelectMenu.SelectTarget.USER)
                                .setPlaceholder("Кого же мы не хотим видеть..")
                                .setRequiredRange(1, 1)
                                .build();
                        event.deferReply(true).setContent("Выбери пользователя, у которого мне нужно забрать доступ к твоей комнатке").setComponents(ActionRow.of(selectMenu)).queue();
                        break;
                    }
                    //изменение количества пользователей в голосовом канале
                    case ("change-limit-users-voice1"): {
                        TextInput nameRoom = TextInput.create("limit-user-voice-channel1", "Количество пользователей (Число)", TextInputStyle.SHORT)
                                .setMinLength(1)
                                .setMaxLength(2)
                                .setPlaceholder("Сколько же нас будет...")
                                .build();

                        Modal modal = Modal.create("limit-user-channel-modal1", "Изменение количества пользователей")
                                .addActionRows(ActionRow.of(nameRoom)).build();
                        event.replyModal(modal).queue();
                        break;
                    }
                    //выганть пользователя
                    case ("kick-user-voice1"): {
                        SelectMenu selectMenu = EntitySelectMenu.create("select-kick-user-room1", EntitySelectMenu.SelectTarget.USER)
                                .setPlaceholder("Кто же себя плохо ведёт...")
                                .setRequiredRange(1, 1)
                                .build();
                        event.deferReply(true).setContent("Выбери пользователя для исключения из комнаты").setComponents(ActionRow.of(selectMenu)).queue();
                        break;
                    }
                    //закрыть комнату
                    case ("close-room-voice1"): {
                        User host = userRepository.getUserById(Objects.requireNonNull(event.getMember()).getIdLong());
                        net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel voiceChannel = guild.getVoiceChannelById(getIdChannelByHost(host));
                        Role everyoneRole = guild.getPublicRole();
                        long allow = 0; // нет разрешения
                        long deny = Permission.VOICE_CONNECT.getRawValue(); // запретить подключаться к войс-каналу
                        voiceChannel.getManager().putPermissionOverride(everyoneRole, allow, deny).queue();

                        event.deferReply(true).setContent("Команатка успешно закрыта").queue();
                        break;
                    }
                    //открыть команту
                    case ("open-room-voice1"): {
                        User host = userRepository.getUserById(Objects.requireNonNull(event.getMember()).getIdLong());
                        net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel voiceChannel = guild.getVoiceChannelById(getIdChannelByHost(host));
                        Role everyoneRole = guild.getPublicRole();
                        long allow = Permission.VOICE_CONNECT.getRawValue(); // разрешить подключаться к войс-каналу
                        long deny = 0; // нет запрещений
                        voiceChannel.getManager().putPermissionOverride(everyoneRole, allow, deny).queue();
                        event.deferReply(true).setContent("Комнатка успешно открыта. Встречай гостей!").queue();
                        break;
                    }
                    default:
                        event.deferReply(true).setContent("Работаем над этим вопросом").queue();
                }
            } else {
                event.deferReply(true).setContent("Вы не являетесь владельцем комнаты :с").queue();
            }
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if(event.getModalId().contains("modal1")){
            switch (event.getModalId()) {
                //форма изменение название комнаты
                case ("name-voice-channel-modal1"): {
                    ModalMapping nameRoomValue = event.getValue("new-name-voice-channel1");
                    assert nameRoomValue != null;
                    String nameRoom = nameRoomValue.getAsString();

                    User host = userRepository.getUserById(Objects.requireNonNull(event.getMember()).getIdLong());
                    Long idChannel = getIdChannelByHost(host);
                    AudioChannel audioChannel = guild.getVoiceChannelById(idChannel);
                    audioChannel.getManager().setName(nameRoom).queue();
                    event.deferReply(true).setContent("Название комнатки успешно изменено!!").queue();
                    break;
                }
                //форма изменения количесвта пользователей
                case ("limit-user-channel-modal1"): {
                    ModalMapping limitUserValue = event.getValue("limit-user-voice-channel1");
                    assert limitUserValue != null;
                    try {
                        int limit = Integer.parseInt(limitUserValue.getAsString());
                        User host = userRepository.getUserById(Objects.requireNonNull(event.getMember()).getIdLong());
                        Long idChannel = getIdChannelByHost(host);
                        net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel voiceChannel = guild.getVoiceChannelById(idChannel);
                        //AudioChannel audioChannel = guild.getVoiceChannelById(idChannel);
                        if (limit >= 0) {
                            System.out.println(voiceChannel.getUserLimit());
                            voiceChannel.getManager().setUserLimit(limit).queue();
                            if (limit == 0)
                                event.deferReply(true).setContent("Нолик снимает все ограничения. Встречай гостей!").queue();
                            else
                                event.deferReply(true).setContent("Лимит пользователей твоей комнаты ограничен до " + limit).queue();
                        } else event.deferReply(true).setContent("Могу и клюнуть. Какое ещё отрицательное число?").queue();
                    } catch (NumberFormatException e) {
                        event.deferReply(true).setContent("Кому-то пора выучить, что такое цифры...").queue();
                    }
                    break;
                }
                default:
                    event.deferReply(true).setContent("Работаем над этим вопросом").queue();
            }
        }
    }

    @Override
    public void onGenericSelectMenuInteraction(GenericSelectMenuInteractionEvent event) {
        if(event.getComponent().getId().contains("room1")){
            switch (event.getComponent().getId()) {
                case ("select-new-host-room1"): {
                    User oldHost = userRepository.getUserById(event.getMember().getIdLong());
                    List values = event.getValues();
                    Member newHost = (Member) values.get(0);
                    long IdChannel = getIdChannelByHost(oldHost);
                    User newUserHost = userRepository.getUserById(newHost.getIdLong());
                    if (newHost.getIdLong() != oldHost.getId()) {
                        if (!newHost.getUser().isBot()) {
                            if (checkVoiceMember(IdChannel, newHost.getIdLong())) {
                                VoiceChannel voiceChannel = voiceChannelRepository.getByIdChannel(IdChannel);
                                voiceChannel.setHost(newUserHost);
                                voiceChannelRepository.save(voiceChannel);
                                //уведомление пользователя, о том, что он стал хозяином
                                net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel voiceChannel1 = guild.getVoiceChannelById(voiceChannel.getIdChannel());
                                SystemMessage systemMessage = new SystemMessage();
                                systemMessage.sendMsgAboutNewHost(voiceChannel1, newUserHost);
                                event.deferReply(true).setContent("Владелец комнатки успешно изменён!").queue();
                            } else
                                event.deferReply(true).setContent("Не нашёл такого среди гостей твоей комнатки!").queue();
                        } else
                            event.deferReply(true).setContent("У-у-у, вот это ты загнул. Ботам не до владения комнатками.\nНайди кого-то более подходящего и свободного :')").queue();
                    } else event.deferReply(true).setContent("Себя переназначить хочешь?").queue();
                    break;
                }
                case ("select-kick-user-room1"): {
                    User host = userRepository.getUserById(event.getMember().getIdLong());
                    List values = event.getValues();
                    Member userForKick = (Member) values.get(0);
                    long idChannel = getIdChannelByHost(host);
                    AudioChannel audioChannel = guild.getVoiceChannelById(idChannel);
                    List<Member> membersInVC = audioChannel.getMembers();
                    if (membersInVC.contains(userForKick)) {
                        audioChannel.getGuild().kickVoiceMember(userForKick).queue();
                        event.deferReply(true).setContent("Утёнок исключил " + userForKick.getAsMention() + " из твоей комнатки").queue();
                    } else event.deferReply(true).setContent("Не нашёл такого среди гостей твоей комнатки!").queue();
                    break;
                }
                case ("select-add-user-room1"): {
                    User host = userRepository.getUserById(event.getMember().getIdLong());
                    List values = event.getValues();
                    Member userForAdd = (Member) values.get(0);
                    long idChannel = getIdChannelByHost(host);
                    AudioChannel audioChannel = guild.getVoiceChannelById(idChannel);
                    List<Member> membersInVC = audioChannel.getMembers();
                    if (!membersInVC.contains(userForAdd)) {
                        long allow = Permission.VOICE_CONNECT.getRawValue(); // разрешить подключаться к войс-каналу
                        long deny = 0; // нет запрещений
                        audioChannel.getManager().putPermissionOverride(userForAdd, allow, deny).queue();
                        event.deferReply(true).setContent("Утёнок пригласил " + userForAdd.getAsMention() + " в твою комнатку").queue();
                    } else event.deferReply(true).setContent("Гость уже приглашён!").queue();
                    break;
                }
                case ("select-ban-user-room1"): {
                    User host = userRepository.getUserById(event.getMember().getIdLong());
                    List values = event.getValues();
                    Member userForBan = (Member) values.get(0);
                    long idChannel = getIdChannelByHost(host);
                    net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel voiceChannel = guild.getVoiceChannelById(idChannel);
                    // Проверяем, находится ли пользователь в голосовом канале
                    if (voiceChannel != null) {
                        long allow = 0; // нет разрешения
                        long deny = Permission.VOICE_CONNECT.getRawValue(); // запретить подключаться к войс-каналу
                        voiceChannel.getManager().putPermissionOverride(userForBan, allow, deny).queue();
                        List<Member> membersInVC = voiceChannel.getMembers();
                        if(membersInVC.contains(userForBan))
                        {
                            voiceChannel.getGuild().kickVoiceMember(userForBan).queue();
                        }
                        event.deferReply(true).setContent("Утёнок забрал доступ к комнатке у  " + userForBan.getAsMention()).queue();
                    }
                    break;
                }
                default:
                    event.deferReply(true).setContent("Работаем над этим вопросом").queue();
            }
        }
    }

    public boolean checkVoiceMember(long voiceChannel, long user) {
        List<Member> membersInVoiceChannel = Objects.requireNonNull(guild.getVoiceChannelById(voiceChannel)).getMembers();
        for (Member member : membersInVoiceChannel) {
            if (member.getIdLong() == user) return true;
        }
        return false;
    }

    public Long getIdChannelByHost(User host) {
        for (VoiceChannel voiceChannel : voiceChannelRepository.findAll()) {
            if (voiceChannel.getHost().equals(host)) return voiceChannel.getIdChannel();
        }
        return null;
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (event.getChannelJoined() != null) {
            if (event.getChannelJoined().getId().equals(CREATE_PRIVATE_VOICE_ROOM_ID)) {
                User user = new User(Objects.requireNonNull(event.getMember()).getUser().getIdLong());
                createVoiceChannel(user);
            }
        }
        if (event.getChannelLeft() != null) {
            leaveOrMoveFromAudioChannel(event.getChannelLeft());
        }
    }

    public Boolean checkUserForIntrButton(User user) {
        for (VoiceChannel voiceChannel : voiceChannelRepository.findAll()) {
            if (voiceChannel.getHost().equals(user)) return true;
        }
        return false;
    }

    //кто-то покинул голосовой чат
    public void leaveOrMoveFromAudioChannel(AudioChannel audioChannel) {
        //проверка категории личных войсов
        if (checkVoiceChannelInCategory(audioChannel.getId())) {
            //проверка не являтеся ли этот войс для создания канала по id
            if (!audioChannel.getId().equals(CREATE_PRIVATE_VOICE_ROOM_ID)) {
                //проверка остались ли люди в войсе, если нет, то войс удаляется
                if (audioChannel.getMembers().size() == 0) {
                    audioChannel.delete().queue();
                    voiceChannelRepository.delete(voiceChannelRepository.getByIdChannel(audioChannel.getIdLong()));
                }
                //если люди остали, проверка остался ли в войсе его владелец, если нет, то рандомно выбирается новый и ему отправляется скрытое сообщение в канал с редактированием войса
                else if (!checkLeaveHost(audioChannel)) {
                    List<Member> userOnVoice = audioChannel.getMembers();
                    Random random = new Random();
                    User newHost = userRepository.getUserById(userOnVoice.get(random.nextInt(0, userOnVoice.size())).getIdLong());
                    VoiceChannel voiceChannel = voiceChannelRepository.getByIdChannel(audioChannel.getIdLong());
                    voiceChannel.setHost(newHost);
                    voiceChannelRepository.save(voiceChannel);
                    //сделать уведомление пользователя, о том, что он стал хозяином
                    net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel voiceChannel1 = guild.getVoiceChannelById(audioChannel.getIdLong());
                    SystemMessage systemMessage = new SystemMessage();
                    systemMessage.sendMsgAboutNewHost(voiceChannel1, newHost);
                }
            }
        }
    }

    public boolean checkLeaveHost(AudioChannel audioChannel) {
        List<Member> membersInVoice = audioChannel.getMembers();
        VoiceChannel voiceChannel = voiceChannelRepository.getByIdChannel(audioChannel.getIdLong());
        return membersInVoice.contains(guild.getMemberById(voiceChannel.getHost().getId()));
    }

    public Boolean checkVoiceChannelInCategory(String audioChannelId) {
        Category category = guild.getCategoryById(CATEGORY_PRIVATE_VOICE_ROOM_ID);
        assert category != null;
        for (AudioChannel audioChannel : category.getVoiceChannels()) {
            if (audioChannel.getId().equals(audioChannelId)) return true;
        }
        return false;
    }

    public void createVoiceChannel(User user) {
        Category category = guild.getCategoryById(CATEGORY_PRIVATE_VOICE_ROOM_ID);
        assert category != null;
        category.createVoiceChannel("Комната " + Objects.requireNonNull(guild.getMemberById(user.getId())).getEffectiveName()).queue(channel -> {
            AudioChannel audioChannel = guild.getVoiceChannelById(channel.getIdLong());
            guild.moveVoiceMember(Objects.requireNonNull(guild.getMemberById(user.getId())), audioChannel).queue();
            VoiceChannel voiceChannel = new VoiceChannel(channel.getIdLong(), user);
            voiceChannelRepository.save(voiceChannel);
        });
    }

    //првоерка есть ли на сервере не удалённые войсы из категорий личных комант
    public static int checkVoiceChannelFromServer() {
        int countBag = 0;
        Category category = guild.getCategoryById(CATEGORY_PRIVATE_VOICE_ROOM_ID);
        assert category != null;
        for (AudioChannel audioChannel : category.getVoiceChannels()) {
            if (audioChannel.getMembers().size() == 0 && !audioChannel.getId().equals(CREATE_PRIVATE_VOICE_ROOM_ID)) {
                voiceChannelRepository.delete(voiceChannelRepository.getByIdChannel(audioChannel.getIdLong()));
                audioChannel.delete().queue();
                countBag++;
            }
        }
        return countBag;
    }

    //проверка есть ли на сервере те войсы, которые не сохранены в БД
    public static int checkVoiceChannelFromBD() {
        int countBag = 0;
        for (VoiceChannel vc : voiceChannelRepository.findAll()) {
            if (guild.getVoiceChannelById(vc.getIdChannel()) == null) {
                voiceChannelRepository.delete(vc);
                countBag++;
            }
        }
        return countBag;
    }

    @Bean
    public CommandLineRunner voice(VoiceChannelRepository repositoryVC) {
        return (args) -> {
            voiceChannelRepository = repositoryVC;
        };
    }
}

