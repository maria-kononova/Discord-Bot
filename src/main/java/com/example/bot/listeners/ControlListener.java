package com.example.bot.listeners;

import com.example.bot.SystemMessage;
import com.example.bot.entity.*;
import com.example.bot.entity.Event;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.cglib.core.Local;

import java.awt.*;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.bot.BotApplication.*;
import static com.example.bot.SystemMessage.*;
import static com.example.bot.listeners.BotCommands.userRepository;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.TemporalAdjusters.nextOrSame;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

public class ControlListener extends ListenerAdapter {
    //Роли
    public static String EVENTER_CONTROL_ROLE = "1181336223260889099";
    public static String MODERATOR_CONTROL_ROLE = "1181335102790303854";
    public static String SUPPORT_CONTROL_ROLE = "1181336220811403294";
    public static String EVENTER_ROLE = "1181335110671413249";
    public static String MODERATOR_ROLE = "1181335111652880467";
    public static String SUPPORT_ROLE = "1181335350526889984";
    public static String FORM_POST_CHANNEL = "1242350717487026268";

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getButton().getId().contains("control5"))
            if (event.getButton().getId().contains("-statistics-control5-")) {
                int index = Integer.parseInt(event.getButton().getId().split("-")[0]);
                Member user = guild.getMemberById(event.getButton().getId().split("-")[4]);
                long maxWeeks = getCountWeekEventOfUser(userRepository.getUserById(user.getIdLong()), LocalDate.now());
                Button nextStatisticsEvent = Button.primary(index - 1 + "-next-statistics-control5-" + user.getId(), guild.getEmojiById(EMOJI_NEXT)).withDisabled(index == 0);
                Button tecStatisticsEvent = Button.secondary("0-tec-statistics-control5-" + user.getId(), "К текущей неделе").withDisabled(index == 0);
                Button prevStatisticsEvent = Button.primary(index + 1 + "-prev-statistics-control5-" + user.getId(), guild.getEmojiById(EMOJI_PREV)).withDisabled(index == maxWeeks - 1);
                List<ItemComponent> components = new ArrayList<>();
                components.add(prevStatisticsEvent);
                components.add(tecStatisticsEvent);
                components.add(nextStatisticsEvent);
                ActionRow actionRow = ActionRow.of(components);
                if (event.getButton().getId().contains("-next-statistics-control5-")) {
                    Role role = getRoleMemberStaff(user);
                    event.deferEdit().setEmbeds(getMsgStatistic(role, user, LocalDate.now().minusWeeks(index)).build()).setComponents(actionRow).queue();
                } else if (event.getButton().getId().contains("-prev-statistics-control5-")) {
                    Role role = getRoleMemberStaff(user);
                    event.deferEdit().setEmbeds(getMsgStatistic(role, user, LocalDate.now().minusWeeks(index)).build()).setComponents(actionRow).queue();
                } else if (event.getButton().getId().contains("-tec-statistics-control5-")) {
                    Role role = getRoleMemberStaff(user);
                    event.deferEdit().setEmbeds(getMsgStatistic(role, user, LocalDate.now()).build()).setComponents(actionRow).queue();
                }
            } else if (event.getButton().getId().contains("answer-form-post-control5-")) {
                String messageId = event.getInteraction().getMessage().getId();
                Message message = guild.getTextChannelById(FORM_POST_CHANNEL).retrieveMessageById(messageId).complete();
                if (event.getMember().getRoles().contains(guild.getRoleById(message.getContentRaw().split("&")[1].split(">")[0]))) {
                    Member member = guild.getMemberById(event.getButton().getId().split("-")[4]);
                    TextInput answer = TextInput.create("answer", "Ответ на заявку", TextInputStyle.PARAGRAPH)
                            .setMinLength(5)
                            .setMaxLength(250)
                            .setPlaceholder("Ответ будет направлен пользователю")
                            .build();
                    Modal modal = Modal.create("answer-modal6-" + event.getMessageId() + "-" + member.getId(), "Ответ на заявку " + member.getEffectiveName()).addActionRow(answer).build();
                    event.replyModal(modal).queue();
                } else event.deferReply(true).setContent("Можно принимать заявки только своей ветки.").queue();
            } else if (event.getButton().getId().contains("control5-prize-")) {
                Role role = guild.getRoleById(event.getButton().getId().split("-")[3]);
                if (event.getButton().getId().contains("accept-control5-prize-")) {
                    String type = "";
                    if (role == guild.getRoleById(EVENTER_ROLE)) {
                        type = "Ивентёр";
                    } else if (role == guild.getRoleById(MODERATOR_ROLE)) {
                        type = "Модератор";
                    } else {
                        type = "Саппорт";
                    }
                    List<Salary> salaryOfStaffRole = salaryRepository.getSalariesByType(type);
                    if (salaryOfStaffRole.size() != 0) {
                        if (!salaryOfStaffRole.get(0).isGiven()) {
                            for (Salary salary : getSalaryOnWeek(LocalDate.now(), salaryOfStaffRole)) {
                                User user = salary.getStaff();
                                user.updateCoins(salary.getCoins());
                                userRepository.save(user);
                                salary.setGiven(true);
                                salary.setControl(userRepository.getUserById(event.getMember().getIdLong()));
                                salaryRepository.save(salary);
                            }
                            event.deferReply(true).setContent("Награда успешно выдана, отчёт отправлен в -> <#" + CONTROL_REPORT_CHANNEL + ">").queue();
                            SystemMessage.sendReportControl(getMsgSalary(role), event.getMember());
                        }
                        event.deferReply(true).setContent("Награда на этой неделе уже была выдана!").queue();
                    }
                    event.deferReply(true).setContent("Нет доступной награды.").queue();
                } else if (event.getButton().getId().contains("edit-control5-prize-")) {
                    String type = "";
                    if (event.getButton().getId().split("-")[3].equals(EVENTER_ROLE)) type = "Ивентёр";
                    else if (event.getButton().getId().split("-")[3].equals(MODERATOR_ROLE)) type = "Модератор";
                    else type = "Саппорт";
                    List<Salary> salaryOnWeek = getSalaryOnWeek(LocalDate.now(), salaryRepository.getSalariesByType(type));
                    //модал с выдачей наград
                    if (salaryOnWeek.size() != 0) {
                        if (!salaryOnWeek.get(0).isGiven()) {
                            Modal.Builder modal = Modal.create("prize-modal5-" + type, "Награды стаффа");
                            TextInput.Builder idTextInput = TextInput.create("id-modal5",
                                            "Номер (Число)", TextInputStyle.SHORT)
                                    .setMinLength(1).setMaxLength(3);
                            TextInput.Builder coinsTextInput = TextInput.create("coins-modal5",
                                            "Награда стаффа", TextInputStyle.SHORT)
                                    .setMinLength(1).setMaxLength(5);
                            modal.addActionRow(idTextInput.build()).addActionRow(coinsTextInput.build());
                            event.replyModal(modal.build()).queue();
                        } else event.deferReply(true).setContent("Награды уже были выданы.").queue();
                    } else event.deferReply(true).setContent("Нет доступных наград.").queue();
                }
            } else {
                switch (event.getButton().getId()) {
                    case ("add-role-control5"): {
                        String placeholder = "";
                        Role role = getRoleStaffFromControl(event.getMember());
                        if (role == guild.getRoleById(EVENTER_ROLE)) {
                            placeholder = "Кто же нам теперь будет проводить ивенты...";
                        } else if (role == guild.getRoleById(MODERATOR_ROLE)) {
                            placeholder = "Кто же будет следить за участниками...";
                        } else if (role == guild.getRoleById(SUPPORT_ROLE)) {
                            placeholder = "Кто же будет поддерживать активность сервера...";
                        }
                        if (role != null) {
                            SelectMenu selectMenu = EntitySelectMenu.create("add-user-select-control5", EntitySelectMenu.SelectTarget.USER)
                                    .setPlaceholder(placeholder)
                                    .setRequiredRange(1, 1)
                                    .build();
                            event.deferReply(true).setContent("Выбери пользователя для назначения на роль **" + role.getAsMention() + "**:").setComponents(ActionRow.of(selectMenu)).queue();
                        } else event.deferReply(true).setContent("У тебя нет подходящей роли.").queue();
                        break;
                    }
                    case ("remove-role-control5"): {
                        String placeholder = "";
                        Role role = getRoleStaffFromControl(event.getMember());
                        if (role == guild.getRoleById(EVENTER_ROLE)) {
                            placeholder = "Кто больше не будет проводить ивенты...";
                        } else if (role == guild.getRoleById(MODERATOR_ROLE)) {
                            placeholder = "Кто больше не будет следить за участниками...";
                        } else if (role == guild.getRoleById(SUPPORT_ROLE)) {
                            placeholder = "Кто больше не будет поддерживать активность сервера...";
                        }
                        if (role != null) {
                            SelectMenu selectMenu = EntitySelectMenu.create("remove-user-select-control5", EntitySelectMenu.SelectTarget.USER)
                                    .setPlaceholder(placeholder)
                                    .setRequiredRange(1, 1)
                                    .build();
                            event.deferReply(true).setContent("Выбери пользователя для снятия его с роли **" + role.getAsMention() + "**:").setComponents(ActionRow.of(selectMenu)).queue();
                        } else event.deferReply(true).setContent("У тебя нет подходящей роли.").queue();
                        break;
                    }
                    case ("user-control5"): {
                        String placeholder = "";
                        Role role = getRoleStaffFromControl(event.getMember());
                        if (role == guild.getRoleById(EVENTER_ROLE)) {
                            placeholder = "За чьими ивентами подсмотрим?";
                        } else if (role == guild.getRoleById(MODERATOR_ROLE)) {
                            placeholder = "Интересующий нас модератор..";
                        } else if (role == guild.getRoleById(SUPPORT_ROLE)) {
                            placeholder = "На чью активность саппорта поглядим?";
                        }
                        if (role != null) {
                            SelectMenu selectMenu = EntitySelectMenu.create("statistics-user-select-control5", EntitySelectMenu.SelectTarget.USER)
                                    .setPlaceholder(placeholder)
                                    .setRequiredRange(1, 1)
                                    .build();
                            event.deferReply(true).setContent("Выбери пользователя для просмотра его рабочей статистики на роли **" + role.getAsMention() + "**:").setComponents(ActionRow.of(selectMenu)).queue();
                        } else event.deferReply(true).setContent("У тебя нет подходящей роли.").queue();
                    }
                    case ("pay-control5"): {
                        LocalDate today = LocalDate.now();
                        LocalDate sunday = today.with(nextOrSame(SUNDAY));
                        if (today.isEqual(sunday)) {
                            String id = "";
                            Role role = getRoleStaffFromControl(event.getMember());
                            Button acceptPrizeButton = Button.success("accept-control5-prize-" + role.getId(), "Подтвердить").withEmoji(guild.getEmojiById(EMOJI_CHECK_MARK));
                            Button editPrizeButton = Button.primary("edit-control5-prize-" + role.getId(), guild.getEmojiById(EMOJI_EDIT));
                            List<ItemComponent> components = new ArrayList<>();
                            components.add(acceptPrizeButton);
                            components.add(editPrizeButton);
                            String type = "";
                            if (role == guild.getRoleById(EVENTER_ROLE)) {
                                type = "Ивентёр";
                            } else if (role == guild.getRoleById(MODERATOR_ROLE)) {
                                type = "Модератор";
                            } else {
                                type = "Саппорт";
                            }
                            List<Salary> salaryOnWeek = getSalaryOnWeek(LocalDate.now(), salaryRepository.getSalariesByType(type));
                            //модал с выдачей наград
                            if (salaryOnWeek.size() != 0) {
                                if (!salaryOnWeek.get(0).isGiven()) {
                                    List<Member> members = guild.getMembersWithRoles(role);
                                    for (Member member : members) {
                                        createSalaryOfStaff(member, event.getMember());
                                    }
                                    event.deferReply(true).setEmbeds(getMsgSalary(role).build()).addActionRow(acceptPrizeButton, editPrizeButton).queue();
                                } else event.deferReply(true).setContent("Награды уже были выданы.").queue();
                            } else event.deferReply(true).setContent("Нет доступных наград.").queue();
                        } else
                            event.deferReply(true).setContent("Сегодня не воскресенье. Дождитесь конца недели чтобы выдать награду стаффу.").queue();
                        break;
                    }
                    default:
                        event.deferReply(true).setContent("Работаем над этим вопросом!").queue();
                }
            }
    }


    public EmbedBuilder getMsgSalary(Role role) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.orange);
        embedBuilder.setTitle("Выдача награды стаффу!");
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(previousOrSame(MONDAY));
        LocalDate sunday = today.with(nextOrSame(SUNDAY));
        embedBuilder.setDescription("<t:" + monday.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() + ":d> - <t:" + sunday.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() + ":d>");
        int i = 1;
        String type = "";
        if (role == guild.getRoleById(EVENTER_ROLE)) {
            type = "Ивентёр";
        } else if (role == guild.getRoleById(MODERATOR_ROLE)) {
            type = "Модератор";
        } else {
            type = "Саппорт";
        }
        List<Salary> salaries = getSalaryOnWeek(LocalDate.now(), salaryRepository.getSalariesByType(type));
        for (Salary salary : salaries) {
            if (role == guild.getRoleById(EVENTER_ROLE)) {
                String s = "";
                List<Event> eventsOnWeekOfUser = getEventInWeek(today, getCompleteEventOfUser(salary.getStaff()));
                embedBuilder.addField(i + ". Награда ивентёра - " + salary.getCoins() + "<:prize:" + EMOJI_PRIZE + ">", "<@" + salary.getStaff().getId() + "> **Проведено ивентов:** " + eventsOnWeekOfUser.size(), false);
                i++;
            } else if (role == guild.getRoleById(MODERATOR_ROLE)) {
                List<Warning> warningsOnWeekOfUser = getWarningsOnWeek(today, warningRepository.findWarningsByModerator(salary.getStaff()));
                List<Ticket> ticketsOnWeekOfUser = getTicketsOnWeek(today, getCompleteTicketsOfUser(salary.getStaff()));
                embedBuilder.addField(i + ". Награда модератора - " + salary.getCoins() + "<:prize:" + EMOJI_PRIZE + ">", "<@" + salary.getStaff().getId() + ">  Тикеты: " + ticketsOnWeekOfUser.size() + "  Предупреждения: " + warningsOnWeekOfUser.size(), false);
                i++;

            } else if (role == guild.getRoleById(SUPPORT_ROLE)) {
                List<Ticket> ticketsOnWeekOfUser = getTicketsOnWeek(today, getCompleteTicketsOfUser(salary.getStaff()));
                embedBuilder.addField(i + ". Награда саппорта - " + salary.getCoins() + "<:prize:" + EMOJI_PRIZE + ">", "<@" + salary.getStaff().getId() + ">  Тикеты: " + ticketsOnWeekOfUser.size(), false);
                i++;
            }
        }

        return embedBuilder;
    }

    public void createSalaryOfStaff(Member member, Member control) {
        Role role = getRoleStaffFromControl(control);
        String type = "";
        if (role == guild.getRoleById(EVENTER_ROLE)) {
            type = "Ивентёр";
        } else if (role == guild.getRoleById(MODERATOR_ROLE)) {
            type = "Модератор";
        } else {
            type = "Саппорт";
        }
        if (!checkSalaryByStaffAndDate(member, type)) {
            Salary salary = new Salary(userRepository.getUserById(member.getIdLong()), userRepository.getUserById(control.getIdLong()), type, getCoinsOfStaff(member, role), false);
            salaryRepository.save(salary);
        }
    }

    public boolean checkSalaryByStaffAndDate(Member member, String type) {
        for (Salary salary : getSalaryOnWeek(LocalDate.now(), salaryRepository.getSalariesByType(type))) {
            if (salary.getStaff().getId().equals(member.getIdLong())) return true;
        }
        return false;
    }


    public int getCoinsOfStaff(Member member, Role role) {
        LocalDate today = LocalDate.now();
        int coins = 500;
        String type = "";
        if (role == guild.getRoleById(EVENTER_ROLE)) {
            type = "Ивентёр";
            List<Event> eventsOnWeekOfUser = getEventInWeek(today, getCompleteEventOfUser(userRepository.getUserById(member.getIdLong())));
            SalaryDefault salaryDefault = salaryDefaultRepository.getSalaryDefaultByType("Ивент");
            coins += (salaryDefault.getCoins() * eventsOnWeekOfUser.size());
        } else if (role == guild.getRoleById(MODERATOR_ROLE)) {
            type = "Модератор";
            List<Warning> warningsOnWeekOfUser = getWarningsOnWeek(today, warningRepository.findWarningsByModerator(userRepository.getUserById(member.getIdLong())));
            List<Ticket> ticketsOnWeekOfUser = getTicketsOnWeek(today, getCompleteTicketsOfUser(userRepository.getUserById(member.getIdLong())));
            SalaryDefault salaryDefaultWarning = salaryDefaultRepository.getSalaryDefaultByType("Нарушение");
            SalaryDefault salaryDefaultTicket = salaryDefaultRepository.getSalaryDefaultByType("Тикет");
            coins += (salaryDefaultWarning.getCoins() * warningsOnWeekOfUser.size()) + (salaryDefaultTicket.getCoins() * ticketsOnWeekOfUser.size());
        } else {
            type = "Саппорт";
            List<Ticket> ticketsOnWeekOfUser = getTicketsOnWeek(today, getCompleteTicketsOfUser(userRepository.getUserById(member.getIdLong())));
            SalaryDefault salaryDefaultTicket = salaryDefaultRepository.getSalaryDefaultByType("Тикет");
            coins += (salaryDefaultTicket.getCoins() * ticketsOnWeekOfUser.size());
        }
        return coins;
    }


    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().contains("form-post-modal5-")) {
            String role = event.getModalId().split("-")[3];
            String roleString = "";
            Role roleForTag = null;
            String nameAndAge = event.getValue("name-and-age").getAsString();
            String aboutMe = event.getValue("about-me").getAsString();
            String experience = event.getValue("experience").getAsString();
            String active = event.getValue("active").getAsString();
            String lore = event.getValue("lore").getAsString();
            if (role.equals("moderator")) {
                roleString = "модератора";
                roleForTag = guild.getRoleById(MODERATOR_CONTROL_ROLE);
            } else if (role.equals("eventer")) {
                roleString = "ивентёра";
                roleForTag = guild.getRoleById(EVENTER_CONTROL_ROLE);
            } else {
                roleString = "саппорта";
                roleForTag = guild.getRoleById(SUPPORT_CONTROL_ROLE);
            }
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Новая заявка на роль " + roleString);
            embedBuilder.addField("Участник", event.getMember().getAsMention(), true);
            embedBuilder.addField("Имя, возраст", nameAndAge, true);
            embedBuilder.addField("О себе", aboutMe, false);
            embedBuilder.addField("Опыт", experience, false);
            embedBuilder.addField("Заявленная активность", active, false);
            embedBuilder.addField("Оценка знаний", lore, false);
            TextChannel textChannel = guild.getTextChannelById(FORM_POST_CHANNEL);
            Button answerBtn = Button.primary("answer-form-post-control5-" + event.getMember().getId(), "Дать ответ").withEmoji(guild.getEmojiById(EMOJI_CHECK_MARK));
            textChannel.sendMessage(roleForTag.getAsMention()).setEmbeds(embedBuilder.build()).addActionRow(answerBtn).queue();
            event.deferReply(true).setContent("Заявка на роль " + roleString + " успешно отправлена.\nОжидай ответа куратора или Утёнка у себя в личных сообщениях!").queue();
        } else if (event.getModalId().contains("answer-modal6-")) {
            Member member = guild.getMemberById(event.getModalId().split("-")[3]);
            String answer = event.getValue("answer").getAsString();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Ответ куратора");
            embedBuilder.setColor(Color.orange);
            embedBuilder.setDescription("### " + event.getMember().getAsMention() + "\n>>> " + answer);

            // Открыть частный канал
            PrivateChannel channel = member.getUser().openPrivateChannel().complete();
            channel.sendMessage("Утёнок передаёт тебе ответ куратора на твою заявку!").setEmbeds(embedBuilder.build()).queue();

            Button answerBtn = Button.primary("answer-form-post-control5-" + event.getMember().getId(), "Дать ответ").withEmoji(guild.getEmojiById(EMOJI_CHECK_MARK)).asDisabled();
            event.deferEdit().setActionRow(answerBtn).queue();
        } else if (event.getModalId().contains("prize-modal5-")) {
            System.out.println(event.getModalId().split("-")[2]);
            try {
                int index = Integer.parseInt(event.getValue("id-modal5").getAsString());
                int coins = Integer.parseInt(event.getValue("coins-modal5").getAsString());
                List<Salary> salaries = getSalaryOnWeek(LocalDate.now(), salaryRepository.getSalariesByType(event.getModalId().split("-")[2]));
                if (index > 0 && index <= salaries.size()) {
                    if (coins > 0) {
                        salaries.get(index - 1).setCoins(coins);
                        salaryRepository.save(salaries.get(index - 1));
                        event.deferEdit().setEmbeds(getMsgSalary(getRoleStaffFromControl(event.getMember())).build()).queue();
                    }
                }
            } catch (Exception e) {
                event.deferReply(true).setContent("Некорректно введённые данные").queue();
            }
        }
    }

    @Override
    public void onGenericSelectMenuInteraction(GenericSelectMenuInteractionEvent event) {
        if (event.getComponent().getId().contains("select-control5")) {
            switch (event.getComponent().getId()) {
                case ("add-user-select-control5"): {
                    List values = event.getValues();
                    Member userForRole = (Member) values.get(0);
                    Member control = event.getMember();
                    if (getRoleMemberStaff(userForRole) == null) {
                        Role role = getRoleStaffFromControl(control);
                        guild.addRoleToMember(userForRole, role).queue();
                        event.deferReply(true).setContent("Роль <@&" + role.getId() + "> успешно **выдана** пользователю <@" + userForRole.getId() + ">.").queue();
                    } else {
                        event.deferReply(true).setContent("У данного пользователя уже есть роль стаффа.").queue();
                    }
                    break;
                }
                case ("remove-user-select-control5"): {
                    List values = event.getValues();
                    Member userForRole = (Member) values.get(0);
                    Role role = getRoleMemberStaff(userForRole);
                    Member control = event.getMember();
                    if (role != null) {
                        if (role == getRoleStaffFromControl(control)) {
                            guild.removeRoleFromMember(userForRole, role).queue();
                            event.deferReply(true).setContent("Пользователь <@" + userForRole.getId() + "> успешно **снят** с роли  <@&" + role.getId() + ">.").queue();
                        } else
                            event.deferReply(true).setContent("У выбранного пользователя нет роли " + getRoleStaffFromControl(control).getAsMention() + ".").queue();
                    } else event.deferReply(true).setContent("У данного пользователя нет роли стаффа.").queue();
                    break;
                }
                case ("statistics-user-select-control5"): {
                    List values = event.getValues();
                    Member user = (Member) values.get(0);
                    Role role = getRoleMemberStaff(user);
                    Member control = event.getMember();
                    if (role != null) {
                        if (role == getRoleStaffFromControl(control)) {
                            Button nextStatisticsEvent = Button.primary("0-next-statistics-control5-" + user.getId(), guild.getEmojiById(EMOJI_NEXT)).asDisabled();
                            Button tecStatisticsEvent = Button.secondary("0-tec-statistics-control5-" + user.getId(), "К текущей неделе").asDisabled();
                            Button prevStatisticsEvent = Button.primary("1-prev-statistics-control5-" + user.getId(), guild.getEmojiById(EMOJI_PREV));
                            List<ItemComponent> components = new ArrayList<>();
                            components.add(prevStatisticsEvent);
                            components.add(tecStatisticsEvent);
                            components.add(nextStatisticsEvent);
                            ActionRow actionRow = ActionRow.of(components);
                            event.deferReply(true).setEmbeds(getMsgStatistic(role, user, LocalDate.now()).build()).addComponents(actionRow).queue();
                        } else
                            event.deferReply(true).setContent("У выбранного пользователя нет роли " + getRoleStaffFromControl(control).getAsMention() + ".").queue();
                    } else event.deferReply(true).setContent("У данного пользователя нет роли стаффа.").queue();
                    break;
                }
                case ("form-post-select-control5"): {
                    if (getRoleMemberAll(event.getMember()) == null) {
                        String value = (String) event.getValues().get(0);
                        String title = "Заявка на должность ";
                        String id = "form-post-modal5-";

                        if (value.equals("moderator")) {
                            title += "модератора";
                            id += "moderator";

                        } else if (value.equals("eventer")) {
                            title += "ивентёра";
                            id += "eventer";
                        } else {
                            title += "саппорта";
                            id += "support";
                        }
                        TextInput nameAndAge = TextInput.create("name-and-age", "Твоё имечко и возраст", TextInputStyle.SHORT)
                                .setPlaceholder("Я - Утёнок, всего годик с:")
                                .setMaxLength(100)
                                .setMinLength(3).build();
                        TextInput aboutMe = TextInput.create("about-me", "Расскажи о себе побольше!", TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Я сплошной код, но об этом пока мало кто догадывается... \nА вообще помогаю в управлении сервера!")
                                .setMaxLength(250)
                                .setMinLength(3).build();
                        TextInput experience = TextInput.create("experience", "Был ли подобный опыт и какой?", TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Где я?...\nОпыта не было, но всему быстро учусь!")
                                .setMaxLength(250)
                                .setMinLength(3).build();
                        TextInput active = TextInput.create("active", "Насколько активным готов(а) быть?", TextInputStyle.SHORT)
                                .setPlaceholder("24 часа в сутки...")
                                .setMaxLength(50)
                                .setMinLength(3).build();
                        TextInput lore = TextInput.create("lore", "Как оцениваешь свои знания в этой ветке?", TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Я знаю всё-всё!")
                                .setMaxLength(250)
                                .setMinLength(3).build();

                        Modal modal = Modal.create(id, title)
                                .addActionRow(nameAndAge)
                                .addActionRow(aboutMe)
                                .addActionRow(experience)
                                .addActionRow(active)
                                .addActionRow(lore)
                                .build();
                        event.replyModal(modal).queue();
                        break;
                    } else
                        event.deferReply(true).setContent("Нельзя подать заявку, будучи уже стаффом сервера.").queue();
                }
                default:
                    event.deferReply(true).setContent("Работаем над этим вопросом").queue();
            }
        }

    }

    //возвращает число недель с первой даты первого проведённого ивента пользователя
    public int getCountWeekEventOfUser(User user, LocalDate date) {
        LocalDate dateMin = date;
        for (Event event : getCompleteEventOfUser(user)) {
            LocalDate dateEvent = event.getDateStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (dateEvent.isBefore(dateMin)) {
                dateMin = dateEvent;
            }
        }
        int weeksBetween = (int) ChronoUnit.WEEKS.between(dateMin.with(previousOrSame(MONDAY)), date.with(previousOrSame(MONDAY)));
        return weeksBetween + 1;
    }

    public int getCountWeekWarningOfUser(User user, LocalDate date) {
        LocalDate dateMin = date;
        for (Warning warning : warningRepository.findWarningsByModerator(user)) {
            LocalDate dateWarning = warning.getDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (dateWarning.isBefore(dateMin)) {
                dateMin = dateWarning;
            }
        }
        int weeksBetween = (int) ChronoUnit.WEEKS.between(dateMin.with(previousOrSame(MONDAY)), date.with(previousOrSame(MONDAY)));
        return weeksBetween + 1;
    }

    public int getCountWeekTicketsOfUser(User user, LocalDate date) {
        LocalDate dateMin = date;
        for (Ticket ticket : getCompleteTicketsOfUser(user)) {
            LocalDate dateTicket = ticket.getDateClose().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (dateTicket.isBefore(dateMin)) {
                dateMin = dateTicket;
            }
        }
        int weeksBetween = (int) ChronoUnit.WEEKS.between(dateMin.with(previousOrSame(MONDAY)), date.with(previousOrSame(MONDAY)));
        return weeksBetween + 1;
    }

    public static Role getRoleStaffFromControl(Member member) {
        if (member.getRoles().contains(guild.getRoleById(EVENTER_CONTROL_ROLE))) return guild.getRoleById(EVENTER_ROLE);
        if (member.getRoles().contains(guild.getRoleById(MODERATOR_CONTROL_ROLE)))
            return guild.getRoleById(MODERATOR_ROLE);
        if (member.getRoles().contains(guild.getRoleById(SUPPORT_CONTROL_ROLE))) return guild.getRoleById(SUPPORT_ROLE);
        return null;
    }

    public static Role getRoleMemberStaff(Member member) {
        if (member.getRoles().contains(guild.getRoleById(EVENTER_ROLE))) return guild.getRoleById(EVENTER_ROLE);
        if (member.getRoles().contains(guild.getRoleById(MODERATOR_ROLE))) return guild.getRoleById(MODERATOR_ROLE);
        if (member.getRoles().contains(guild.getRoleById(SUPPORT_ROLE))) return guild.getRoleById(SUPPORT_ROLE);
        else return null;
    }


    public Role getRoleMemberAll(Member member) {
        if (member.getRoles().contains(guild.getRoleById(EVENTER_CONTROL_ROLE)))
            return guild.getRoleById(EVENTER_CONTROL_ROLE);
        if (member.getRoles().contains(guild.getRoleById(MODERATOR_CONTROL_ROLE)))
            return guild.getRoleById(MODERATOR_CONTROL_ROLE);
        if (member.getRoles().contains(guild.getRoleById(SUPPORT_CONTROL_ROLE)))
            return guild.getRoleById(SUPPORT_CONTROL_ROLE);
        if (member.getRoles().contains(guild.getRoleById(EVENTER_ROLE))) return guild.getRoleById(EVENTER_ROLE);
        if (member.getRoles().contains(guild.getRoleById(MODERATOR_ROLE))) return guild.getRoleById(MODERATOR_ROLE);
        if (member.getRoles().contains(guild.getRoleById(SUPPORT_ROLE))) return guild.getRoleById(SUPPORT_ROLE);
        else return null;
    }

    public EmbedBuilder getMsgStatistic(Role role, Member member, LocalDate today) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Рабочая статистика " + member.getEffectiveName());
        embedBuilder.setColor(Color.orange);
        User user = userRepository.getUserById(member.getIdLong());
        if (role == guild.getRoleById(EVENTER_ROLE)) {
            List<Event> eventsOfUser = getCompleteEventOfUser(user);
            LocalDate monday = today.with(previousOrSame(MONDAY));
            LocalDate sunday = today.with(nextOrSame(SUNDAY));

            embedBuilder.addField("Проведено ивентов", String.valueOf(eventsOfUser.size()), true);
            embedBuilder.addField("Выдано наград на", getCountCoins(generateListEventUserByEvents(eventRepository.findEventsByEventer(user))) + "<:prize:" + EMOJI_PRIZE + ">", true);
            embedBuilder.addField("Предупреждений", warningRepository.findWarningsByViolatorAndViolation(user, violationRepository.getViolationByName("Предупреждение стаффа")).size() + " / 3", false);
            embedBuilder.addField("Недельный период просмотра", "<t:" + monday.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() + ":d> - <t:" + sunday.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() + ":d>", false);
            int i = 1;
            List<Event> eventOnWeek = getEventInWeek(today, eventsOfUser);
            if (eventOnWeek.size() != 0) {
                for (Event event : eventOnWeek) {
                    if (getEventInWeek(today, eventsOfUser).size() != 0) {
                        embedBuilder.addField(i + ". Ивент «" + event.getType().getName() + "»", "> Дата <t:" + event.getDateStart().getTime() / 1000 + ":f>", true);
                        embedBuilder.addField("Участники", "> " + eventUserRepository.findEventUsersByEvent(event).size() + "<:users:" + EMOJI_USERS + ">", true);
                        embedBuilder.addField("Выдано монеток", "> " + getCountCoins(eventUserRepository.findEventUsersByEvent(event)) + "<:prize:" + EMOJI_PRIZE + ">", true);
                        embedBuilder.addField("---------------------------------", "", false);
                        i++;
                    }
                }
            } else embedBuilder.addField("Нет проведённых ивентов на этой неделе", "", false);
            embedBuilder.setFooter("Страница " + getCountWeekEventOfUser(user, today) + " / " + getCountWeekEventOfUser(user, LocalDate.now()));
        } else if (role == guild.getRoleById(MODERATOR_ROLE)) {
            List<Warning> warningsOfUser = warningRepository.findWarningsByModerator(user);
            List<Ticket> ticketsOfUser = ticketRepository.findTicketsByModerator(user);
            LocalDate monday = today.with(previousOrSame(MONDAY));
            LocalDate sunday = today.with(nextOrSame(SUNDAY));

            embedBuilder.addField("Выдано нарушений", String.valueOf(warningsOfUser.size()), true);
            embedBuilder.addField("Закрыто тикетов", String.valueOf(ticketsOfUser.size()), true);
            embedBuilder.addField("Предупреждений", warningRepository.findWarningsByViolatorAndViolation(user, violationRepository.getViolationByName("Предупреждение стаффа")).size() + " / 3", false);
            embedBuilder.addField("Недельный период просмотра", "<t:" + monday.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() + ":d> - <t:" + sunday.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() + ":d>", false);
            int i = 1;
            List<Warning> warningsOnWeek = getWarningsOnWeek(today, warningsOfUser);
            List<Ticket> ticketsOnWeek = getTicketsOnWeek(today, ticketsOfUser);
            if (warningsOnWeek.size() != 0) {
                for (Warning warning : warningsOnWeek) {
                    String s = "";
                    s += "**Нарушитель:** <@" + warning.getModerator().getId() + ">\n";
                    s += "**Дата:** <t:" + warning.getDateTime().getTime() / 1000 + ":f>\n";
                    s += "**Тип:** " + warning.getViolation().getName() + "\n";
                    s += "**Комментарий:** " + warning.getComment();
                    embedBuilder.addField(i + ". Нарушение", s, false);
                    i++;
                }
            } else embedBuilder.addField("Нет пойманных нарушений на этой неделе", "", false);
            embedBuilder.addField("------------------------------------------------", "", false);
            embedBuilder = addTicketOfUserOnEmbed(embedBuilder, ticketsOnWeek);
            int max = getCountWeekWarningOfUser(user, LocalDate.now());
            int min = getCountWeekWarningOfUser(user, today);
            if (max < getCountWeekTicketsOfUser(user, LocalDate.now())) {
                max = getCountWeekTicketsOfUser(user, LocalDate.now());
                min = getCountWeekTicketsOfUser(user, today);
            }
            embedBuilder.setFooter("Страница " + min + " / " + max);
        } else {
            List<Ticket> ticketsOfUser = ticketRepository.findTicketsByModerator(user);
            LocalDate monday = today.with(previousOrSame(MONDAY));
            LocalDate sunday = today.with(nextOrSame(SUNDAY));

            embedBuilder.addField("Активность", String.valueOf(0), true);
            embedBuilder.addField("Закрыто тикетов", String.valueOf(ticketsOfUser.size()), true);
            embedBuilder.addField("Предупреждений", warningRepository.findWarningsByViolatorAndViolation(user, violationRepository.getViolationByName("Предупреждение стаффа")).size() + " / 3", false);
            embedBuilder.addField("Недельный период просмотра", "<t:" + monday.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() + ":d> - <t:" + sunday.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() + ":d>", false);
            List<Ticket> ticketsOnWeek = getTicketsOnWeek(today, ticketsOfUser);
            embedBuilder = addTicketOfUserOnEmbed(embedBuilder, ticketsOnWeek);
            embedBuilder.setFooter("Страница " + getCountWeekTicketsOfUser(user, today) + " / " + getCountWeekTicketsOfUser(user, LocalDate.now()));
        }
        return embedBuilder;
    }

    public EmbedBuilder addTicketOfUserOnEmbed(EmbedBuilder embedBuilder, List<Ticket> ticketsOnWeek) {
        int i = 1;
        if (ticketsOnWeek.size() != 0) {
            for (Ticket ticket : ticketsOnWeek) {
                String s = "";
                s += "**Создатель:** <@" + ticket.getUser().getId() + ">\n";
                s += "**Обращение:** " + ticket.getDescriptionProblem() + "\n";
                s += "**Время решения:** <t:" + ticket.getDateCreate().getTime() / 1000 + ":f> - <t:" + ticket.getDateClose().getTime() / 1000 + ":f>\n";
                s += "**Результат беседы:** " + ticket.getSolution();
                embedBuilder.addField(i + ". Тикет «" + ticket.getTypeProblem() + "-" + guild.getMemberById(ticket.getUser().getId()).getEffectiveName() + "»", s, false);
                i++;
            }
        } else embedBuilder.addField("Нет закрытых тикетов на этой неделе", "", false);
        return embedBuilder;
    }


    //берёт все законченные ивенты пользователя
    public List<Event> getCompleteEventOfUser(User user) {
        List<Event> eventsOfUser = new ArrayList<>();
        for (Event event : eventRepository.findEventsByEventer(user)) {
            if (event.getDateEnd() != null) eventsOfUser.add(event);
        }
        return eventsOfUser;
    }

    //берёт закрытые тикеты модераторов
    public List<Ticket> getCompleteTicketsOfUser(User user) {
        List<Ticket> ticketsOfUser = new ArrayList<>();
        for (Ticket ticket : ticketRepository.findTicketsByModerator(user)) {
            if (ticket.getDateClose() != null) ticketsOfUser.add(ticket);
        }
        return ticketsOfUser;
    }

    //берёт все предупреждения пользователя на пепреданной неделе
    public List<Warning> getWarningsOnWeek(LocalDate today, List<Warning> warningsOfUser) {
        LocalDate todayWeek = LocalDate.now();
        LocalDate monday = today.with(previousOrSame(MONDAY));
        LocalDate sunday = today.with(nextOrSame(SUNDAY));
        List<Warning> warnings = new ArrayList<>();
        for (Warning warning : warningsOfUser) {
            if (warning.getDateTime() != null) {
                LocalDate localDate = warning.getDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if ((localDate.isAfter(monday) || localDate.isEqual(monday)) && (localDate.isBefore(sunday) || localDate.isEqual(sunday))) {
                    warnings.add(warning);
                }
            }
        }
        return warnings;
    }

    //берёт вся запралата стаффа на пепреданной неделе
    public List<Salary> getSalaryOnWeek(LocalDate today, List<Salary> salariesOfRole) {
        LocalDate todayWeek = LocalDate.now();
        LocalDate monday = today.with(previousOrSame(MONDAY));
        LocalDate sunday = today.with(nextOrSame(SUNDAY));
        List<Salary> salaries = new ArrayList<>();
        for (Salary salary : salariesOfRole) {
            if (salary.getDateTime() != null) {
                LocalDate localDate = salary.getDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if ((localDate.isAfter(monday) || localDate.isEqual(monday)) && (localDate.isBefore(sunday) || localDate.isEqual(sunday))) {
                    salaries.add(salary);
                }
            }
        }
        Collections.sort(salaries, new Comparator<Salary>() {
            @Override
            public int compare(Salary o1, Salary o2) {
                return o1.getStaff().getId().compareTo(o2.getStaff().getId());
            }
        });
        return salaries;
    }


    //берёт все тикиты пользователя на переданной неделе
    public List<Ticket> getTicketsOnWeek(LocalDate today, List<Ticket> ticketsOfUser) {
        LocalDate todayWeek = LocalDate.now();
        LocalDate monday = today.with(previousOrSame(MONDAY));
        LocalDate sunday = today.with(nextOrSame(SUNDAY));
        List<Ticket> tickets = new ArrayList<>();
        for (Ticket ticket : ticketsOfUser) {
            if (ticket.getDateClose() != null) {
                LocalDate localDate = ticket.getDateClose().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if ((localDate.isAfter(monday) || localDate.isEqual(monday)) && (localDate.isBefore(sunday) || localDate.isEqual(sunday))) {
                    tickets.add(ticket);
                }
            }
        }
        return tickets;
    }

    public List<Event> getEventInWeek(LocalDate today, List<Event> eventsOfUser) {
        LocalDate monday = today.with(previousOrSame(MONDAY));
        LocalDate sunday = today.with(nextOrSame(SUNDAY));
        List<Event> events = new ArrayList<>();
        for (Event event : eventsOfUser) {
            if (event.getDateStart() != null) {
                LocalDate localDate = event.getDateStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if ((localDate.isAfter(monday) || localDate.isEqual(monday)) && (localDate.isBefore(sunday) || localDate.isEqual(sunday))) {
                    events.add(event);
                }
            }
        }
        return events;
    }

    //берёт всех ивентопользователей у всех ивентов
    public List<EventUser> generateListEventUserByEvents(List<Event> events) {
        List<EventUser> eventUsers = new ArrayList<>();
        for (Event event : events) {
            eventUsers.addAll(eventUserRepository.findEventUsersByEvent(event));
        }
        return eventUsers;
    }

    //суммарное число монет за всех переданных инвентопользователей
    public int getCountCoins(List<EventUser> eventUsers) {
        int countCoins = 0;
        for (EventUser eventUser : eventUsers) {
            countCoins += eventUser.getPrize();
        }
        return countCoins;
    }

}
