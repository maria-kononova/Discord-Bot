package com.example.bot.listeners;

import com.example.bot.SystemMessage;
import com.example.bot.entity.Ticket;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.bot.BotApplication.*;
import static com.example.bot.listeners.BotCommands.userRepository;
import static com.example.bot.listeners.ControlListener.*;

public class Tickets extends ListenerAdapter {
    public String TICKET_CATEGORY = "1238866706156355614";

    public String ROLE_MODERATOR = "1181335111652880467";
    public String ROLE_MODERATOR_CONTROL = "1181335102790303854";
    public String ROLE_ADMINISTRATOR = "1181323632639754330";
    public String ROLE_DEVELOPER = "1181347862781644821";

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getButton().getId().contains("ticket3")) {
            switch (event.getButton().getId()) {
                case ("close-ticket3"): {
                    TextChannel textChannel = event.getChannel().asTextChannel();
                    Ticket ticket = ticketRepository.getTicketById(textChannel.getIdLong());
                    if (ticket.getModerator() != null) {
                        System.out.println(ticket.getModerator());
                        System.out.println(userRepository.getUserById(event.getMember().getIdLong()));
                        if (ticket.getModerator().getId() == event.getMember().getIdLong()) {
                            SystemMessage.sendMsgCloseTicket(textChannel, ticket.getModerator());
                        } else event.deferReply(true).setContent("Ты не можеть закончить обсуждение тикета :с").queue();
                    } else event.deferReply(true).setContent("Ты не можеть закончить обсуждение тикета :с").queue();
                    break;
                }
                case ("add-user-in-ticket3"): {
                    SelectMenu selectMenu = EntitySelectMenu.create("select-add-user-in-ticket3", EntitySelectMenu.SelectTarget.USER)
                            .setPlaceholder("Кто нам необходим для обсуждения?")
                            .setRequiredRange(1, 1)
                            .build();
                    event.deferReply(true).setContent("Выбери пользователя для добавления его к решению проблемы").setComponents(ActionRow.of(selectMenu)).queue();
                    break;
                }
                case ("solution-button-ticket3"): {
                    TextChannel textChannel = event.getChannel().asTextChannel();
                    Ticket ticket = ticketRepository.getTicketById(textChannel.getIdLong());
                    if (ticket.getModerator().getId() == event.getMember().getIdLong()) {
                        TextInput textInput = TextInput.create("solution-ticket3", "Описание решения", TextInputStyle.PARAGRAPH)
                                .setMaxLength(500)
                                .build();
                        Modal modal = Modal.create("solution-ticket-modal3", "Расскажи Утёнку о принятом решении").addActionRow(textInput).build();
                        event.replyModal(modal).queue();
                    } else event.deferReply(true).setContent("Ты не можеть закончить обсуждение тикета :с").queue();
                    break;
                }
                case ("accept-ticket3"): {
                    TextChannel textChannel = event.getChannel().asTextChannel();
                    Ticket ticket = ticketRepository.getTicketById(textChannel.getIdLong());
                    if ((event.getMember().getRoles().contains(guild.getRoleById(ROLE_MODERATOR)) && ticket.getTypeProblem().equals("Нарушение")) || (event.getMember().getRoles().contains(guild.getRoleById(ROLE_MODERATOR_CONTROL)) && ticket.getTypeProblem().equals("Нарушение")) ||
                            event.getMember().getRoles().contains(guild.getRoleById(ROLE_ADMINISTRATOR)) || (event.getMember().getRoles().contains(guild.getRoleById(SUPPORT_CONTROL_ROLE)) && ticket.getTypeProblem().equals("Вопрос")) || (event.getMember().getRoles().contains(guild.getRoleById(SUPPORT_ROLE)) && ticket.getTypeProblem().equals("Вопрос"))) {
                        if (ticket.getModerator() == null) {
                            ticket.setModerator(userRepository.getUserById(event.getMember().getIdLong()));
                            ticketRepository.save(ticket);
                            textChannel.sendMessage("<@" + event.getMember().getId() + "> будет решать данный вопрос").queue();
                            event.deferReply(true).setContent("Тикет принят в работу!\nУтёнок настоятельно просит соблюдать в обсуждении все правила Дискорда и сервера." +
                                    "\nНе забудь после решения вопроса отправить отчёт. <a:1907duckwow:1238511205316820992> ").queue();
                        } else {
                            event.deferReply(true).setContent("Над данным тикетом уже работает <@" + ticket.getModerator().getId() + ">").queue();
                        }
                    } else {
                        String role = "";
                        if (Objects.equals(ticket.getTypeProblem(), "Нарушение")) role = "модератора";
                        else role = "саппорта";
                        event.deferReply(true).setContent("Принять тикет может только участник с ролью " + role + " и выше.\nПожалуйста, дождитесь, пока данным вопросом займутся").queue();
                    }
                    break;
                }
                default:
                    event.deferReply(true).setContent("Работаем над этим вопросом!").queue();
            }
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().contains("modal3")) {
            switch (event.getModalId()) {
                case ("create-ticket-question-modal3"): {
                    ModalMapping descriptionModal = event.getValue("description-problem3");
                    assert descriptionModal != null;
                    String description = descriptionModal.getAsString();
                    Member member = event.getMember();
                    TextChannel newChannel = guild.createTextChannel("❔Вопрос-" + member.getUser().getName(), guild.getCategoryById(TICKET_CATEGORY)).complete();
                    viewTicketForUser(member, newChannel);
                    String message = "## <a:9082443297d2ff80151793324186bc42:1238970002472833026> Появился вопрос\n" +
                            "### **<@" + member.getIdLong() + "> хочет задать вопросик:**\n" +
                            ">>> " + description + "\n " +
                            "\n";
                    SystemMessage.sendMessageInTicket(newChannel, message, SUPPORT_ROLE);
                    ticketRepository.save(new Ticket(newChannel.getIdLong(), userRepository.getUserById(member.getIdLong()), "Вопрос", description));
                    event.deferReply(true).setContent("Перейди в -> <#" + newChannel.getId() + "> для обсуждения вопросика").queue();
                    break;
                }
                case ("create-ticket-violation-modal3"): {
                    ModalMapping descriptionModal = event.getValue("description-problem3");
                    assert descriptionModal != null;
                    String description = descriptionModal.getAsString();
                    Member member = event.getMember();
                    TextChannel newChannel = guild.createTextChannel("❗Нарушение-" + member.getUser().getName(), guild.getCategoryById(TICKET_CATEGORY)).complete();
                    viewTicketForUser(member, newChannel);
                    String message = "## <a:030e86761180bea617468b23054fb357:1238969991093682186>  Поймано нарушение\n" +
                            "### **<@" + member.getIdLong() + "> сообщает о проблеме с пользователем:**\n" +
                            ">>> " + description + "\n " +
                            "\n";
                    ModalMapping userProblemModal = event.getValue("user-problem3");
                    String userProblem = userProblemModal.getAsString();
                    if (!userProblem.equals("")) {
                        String idUserProblem = CheckServer.getIdUserOnServer(userProblem);
                        if (!idUserProblem.equals("")) {
                            message += "### Указанный нарушитель:\n" +
                                    "<@" + idUserProblem + ">";
                        }
                    }
                    SystemMessage.sendMessageInTicket(newChannel, message, ROLE_MODERATOR);
                    ticketRepository.save(new Ticket(newChannel.getIdLong(), userRepository.getUserById(member.getIdLong()), "Нарушение", description));
                    event.deferReply(true).setContent("Перейди в -> <#" + newChannel.getId() + "> для обсуждения нарушения").queue();
                    break;
                }
                case ("create-ticket-bag-modal3"): {
                    ModalMapping descriptionModal = event.getValue("description-problem3");
                    assert descriptionModal != null;
                    String description = descriptionModal.getAsString();
                    Member member = event.getMember();
                    TextChannel newChannel = guild.createTextChannel("\uD83D\uDC7EБаг-" + member.getUser().getName(), guild.getCategoryById(TICKET_CATEGORY)).complete();
                    ModalMapping commandModal = event.getValue("command-problem3");
                    String command = commandModal.getAsString();
                    //разрешение для роли разработчика
                    //newChannel.getManager().putRolePermissionOverride(1181347862781644821L, Permission.VIEW_CHANNEL.getRawValue(), 0).queue();
                    viewTicketForUser(member, newChannel);
                    String message = "## <a:ffd9b46366e14141790a80d4922485bf:1238968051190464605> Обнаружился баг\n" +
                            "### **<@" + member.getIdLong() + "> сообщает об ошибке в работе Утёнка или сервера:**\n" +
                            ">>> " + description + "\n " +
                            "\n";
                    if (!commandModal.getAsString().isEmpty()) {
                        if (!CheckServer.checkCommandOnServer(command)) {
                            message += "### Команда, с которой возникла проблема:\n" +
                                    "/" + command;
                        }
                    }
                    SystemMessage.sendMessageInTicket(newChannel, message, ROLE_DEVELOPER);
                    ticketRepository.save(new Ticket(newChannel.getIdLong(), userRepository.getUserById(member.getIdLong()), "Баг", description));
                    event.deferReply(true).setContent("Перейди в -> <#" + newChannel.getId() + "> для обсуждения бага").queue();
                    break;
                }
                case ("solution-ticket-modal3"): {
                    TextChannel textChannel = event.getChannel().asTextChannel();
                    Ticket ticket = ticketRepository.getTicketById(textChannel.getIdLong());
                    ModalMapping solutionModal = event.getValue("solution-ticket3");
                    String solution = solutionModal.getAsString();
                    ticket.setSolution(solution);
                    ticket.setDateClose(new Date());
                    ticketRepository.save(ticket);
                    if (ticket.getTypeProblem().equals("Нарушение")) {
                        SystemMessage.sendReportModerationTicket(getEmbedTicket(ticket, "Модератор"));
                    } else if (ticket.getTypeProblem().equals("Вопрос")) {
                        SystemMessage.sendReportSupportTicket(getEmbedTicket(ticket, "Саппорт"));
                    }
                    event.deferReply(false).setContent("Тикет успешно закрыт").queue();
                    textChannel.delete().queue();
                    break;
                }
                default:
                    event.deferReply(true).setContent("Работаем над этим вопросом!").queue();
            }
        }
    }

    public EmbedBuilder getEmbedTicket(Ticket ticket, String role) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.orange);
        embedBuilder.setTitle("Тикет «" + ticket.getTypeProblem() + "-" + guild.getMemberById(ticket.getUser().getId()).getEffectiveName() + "» закрыт!");
        embedBuilder.setDescription("<t:" + ticket.getDateCreate().getTime() / 1000 + ":f> - <t:" + ticket.getDateClose().getTime() / 1000 + ":f>");
        embedBuilder.addField(ticket.getTypeProblem() + ":", "> " + ticket.getDescriptionProblem(), false);
        embedBuilder.addField("Решение:", "> " + ticket.getSolution(), false);
        embedBuilder.addField(role + ":", "> " + "<@" + ticket.getModerator().getId() + ">", false);
        return embedBuilder;
    }


    public void viewTicketForUser(Member member, TextChannel textChannel) {
        long allow = Permission.VIEW_CHANNEL.getRawValue(); //разрешение писать в тиките пользователю
        long deny = 0; // нет запрещений
        textChannel.getManager().putPermissionOverride(member, allow, deny).queue();
    }

    public void notViewTicketForRole(long role, TextChannel textChannel) {
        textChannel.getManager().clearOverridesAdded().queue();
        //textChannel.getManager().putRolePermissionOverride(role, 0, Permission.VIEW_CHANNEL.getRawValue()).queue();
    }


    @Override
    public void onGenericSelectMenuInteraction(GenericSelectMenuInteractionEvent event) {
        if (event.getComponent().getId().contains("ticket3")) {
            switch (event.getComponent().getId()) {
                //выбор типа проблемы для тикета, в соответствии с ним создаётся форма для заполнения подробностей проблемы
                case ("type-select-problem-ticket3"): {
                    String value = (String) event.getValues().get(0);
                    TextInput.Builder descriptionProblem = TextInput.create("description-problem3", "Описание проблемы", TextInputStyle.PARAGRAPH)
                            .setMinLength(5)
                            .setMaxLength(500);
                    Modal.Builder modal = Modal.create("create-ticket-modal3", "Title");
                    switch (value) {
                        case ("question"): {
                            modal.setTitle("Назрел вопросик");
                            modal.setId("create-ticket-question-modal3");
                            descriptionProblem.setPlaceholder("В чём вопросик?");
                            modal.addActionRow(descriptionProblem.build());
                            break;
                        }
                        case ("violation"): {
                            modal.setTitle("Нарушение участника сервера");
                            descriptionProblem.setPlaceholder("Здесь можешь нажаловаться на кого душе угодно ;)");
                            modal.addActionRow(descriptionProblem.build());
                            modal.setId("create-ticket-violation-modal3");
                            TextInput.Builder userProblem = TextInput.create("user-problem3", "Введи имя проблемного пользователя", TextInputStyle.SHORT)
                                    .setPlaceholder("Имя пользователя")
                                    .setMinLength(1)
                                    .setMaxLength(100);
                            modal.addActionRow(userProblem.build());
                            break;
                        }
                        case ("bag"): {
                            modal.setTitle("Баг в работе утёнка");
                            modal.setId("create-ticket-bag-modal3");
                            descriptionProblem.setPlaceholder("Где Утёнок накосячил...");
                            modal.addActionRow(descriptionProblem.build());
                            TextInput.Builder commandProblem = TextInput.create("command-problem3", "Укажи команду, с которой возникла проблема", TextInputStyle.SHORT)
                                    .setPlaceholder("Название команды")
                                    .setMinLength(1)
                                    .setMaxLength(100);
                            modal.addActionRow(commandProblem.build());
                            break;
                        }
                    }
                    event.replyModal(modal.build()).queue();
                    break;
                }
                case ("select-add-user-in-ticket3"): {
                    List values = event.getValues();
                    Member addMember = (Member) values.get(0);
                    TextChannel textChannel = event.getChannel().asTextChannel();
                    if (!textChannel.getMembers().contains(addMember)) {
                        viewTicketForUser(addMember, textChannel);
                        event.deferReply(false).setContent("<@" + addMember.getIdLong() + "> успешно добавлен к обсуждению").queue();
                    } else {
                        event.deferReply(true).setContent("Данный пользователь уже добавлен к обсуждению").queue();
                    }
                    break;
                }
                default:
                    event.deferReply(true).setContent("Работаем над этим вопросом").queue();
            }
        }
    }
}
