package com.example.bot.listeners;

import com.example.bot.entity.*;
import com.example.bot.entity.Event;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.internal.interactions.component.StringSelectMenuImpl;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.bot.BotApplication.*;
import static com.example.bot.SystemMessage.EMOJI_PREV;
import static com.example.bot.listeners.BotCommands.userRepository;

public class ShopListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getButton().getId().contains("shop7")) {
            if (event.getButton().getId().contains("buy-button-shop7-")) {
                User user = userRepository.getUserById(event.getMember().getIdLong());
                Roles role = rolesRepository.getRolesById(Long.parseLong(event.getButton().getId().split("-")[3]));
                if (shopRepository.getByBuyerAndRole(user, role) == null) {
                    if (user.getCoins() >= role.getCoins()) {
                        user.updateCoins(role.getCoins() * -1);
                        userRepository.save(user);
                        Shop shop = new Shop(user, role);
                        shopRepository.save(shop);

                        Button backButton = Button.secondary("back-button-shop7", "Назад").withEmoji(guild.getEmojiById(EMOJI_PREV));
                        Button wearButton = Button.primary("wear-button-shop7-" + role.getId(), "Надеть");
                        Button stockButton = Button.primary("stock-button-shop7", "Инвентарь");

                        event.deferEdit().setEmbeds(getAcceptShopMsg(event.getMember(), role).build()).setActionRow(backButton, wearButton, stockButton).queue();
                    } else
                        event.deferReply(true).setContent("Не хватает монеток :с\nМонетки заработать очень легко! Достаточно просто быть чуть активнее на сервере.\nПиши больше сообщений, общайся в голосовых каналах или поучавствуй в ивенте!").queue();
                } else event.deferReply(true).setContent("Данная роль уже есть в твоём инвентаре!").queue();
            } else if (event.getButton().getId().contains("wear-button-shop7-")) {
                Roles role = rolesRepository.getRolesById(Long.parseLong(event.getButton().getId().split("-")[3]));
                wearRole(event.getMember(), role);
                event.deferReply(true).setContent("Роль успешно установлена в твой профиль!").queue();
            } else {
                switch (event.getButton().getId()) {
                    case ("open-shop7"): {
                        event.deferReply(true).setEmbeds(getShopMsg(event.getMember()).build()).addActionRow(getSelectShop()).queue();
                        break;
                    }
                    case ("back-button-shop7"): {
                        event.deferEdit().setEmbeds(getShopMsg(event.getMember()).build()).setActionRow(getSelectShop()).queue();
                        break;
                    }
                    case ("back-button-stock-shop7"): {
                        event.deferEdit().setEmbeds(getStockOfUser(event.getMember()).build()).setActionRow(getSelectStock(userRepository.getUserById(event.getMember().getIdLong()))).queue();
                        break;
                    }
                    case ("stock-button-shop7"): {
                        event.deferReply(true).setEmbeds(getStockOfUser(event.getMember()).build())
                                .addActionRow(getSelectStock(userRepository.getUserById(event.getMember().getIdLong())))
                                .queue();
                        break;
                    }
                    case ("unwear-no-button-shop7"): {
                        unwearRole(event.getMember());
                        event.deferReply(true).setContent("Роль успешно удалена из твоего профиля! Она остаётся в инвентаре, можешь надеть её в любое время.").queue();
                        break;
                    }
                }
            }
        }
    }

    public StringSelectMenuImpl getSelectStock(User user) {
        List<SelectOption> selectOptions = new ArrayList<>();
        int i = 1;
        for (Shop shop : shopRepository.getShopsByBuyer(user)) {
            selectOptions.add(SelectOption.of(i + ". " + guild.getRoleById(shop.getRole().getId()).getName(), String.valueOf(shop.getRole().getId())));
            i++;
        }
        if (selectOptions.size() == 0) return null;
        StringSelectMenuImpl selectMenu = new StringSelectMenuImpl("role-select-stock-shop7", "Выбери роль", 1, 1, false, selectOptions);
        return selectMenu;
    }

    public EmbedBuilder getStockOfUser(Member member) {
        List<Shop> shops = shopRepository.findAll();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.orange);
        int i = 1;
        String description = "## <a:6a4723057fe5aea0e8df745469c23cbd:1238969983338418197>  Инвентарь \n" +
                "Здесь хранятся все твои приобретённые вещички!\n";
        Roles roleOfUser = getRoleOnUser(member);
        if (shops.size() != 0) {
            for (Shop shop : shops) {
                description += "### ";
                if (roleOfUser != null)
                    if (Objects.equals(shop.getRole().getId(), getRoleOnUser(member).getId()))
                        description += "<:check_mark:1241420207512354886> ";
                description += i + ".  <@&" + shop.getRole().getId() + ">\n Дата приобретения: <t:" + shop.getDateTime().getTime() / 1000 + ":f> \n";
                i++;
            }
        } else description += "### Нет купленных предметов.";
        embedBuilder.setDescription(description);
        return embedBuilder;
    }

    public Roles getRoleOnUser(Member member) {
        for (Role roleServer : member.getRoles()) {
            Roles role = rolesRepository.getRolesById(Long.parseLong(roleServer.getId()));
            if (role != null) {
                System.out.println(role);
                return role;
            }
        }
        return null;
    }

    public void unwearRole(Member member) {
        //снимаем любую покупную другую роль
        for (Role roleServer : member.getRoles()) {
            if (rolesRepository.getRolesById(Long.parseLong(roleServer.getId())) != null) {
                guild.removeRoleFromMember(member, roleServer).queue();
            }
        }
    }

    public void wearRole(Member member, Roles role) {
        unwearRole(member);
        guild.addRoleToMember(member, guild.getRoleById(role.getId())).queue();
    }

    @Override
    public void onGenericSelectMenuInteraction(GenericSelectMenuInteractionEvent event) {
        if (event.getComponent().getId().contains("shop7")) {
            switch (event.getComponent().getId()) {
                case ("role-select-shop7"): {
                    Roles role = rolesRepository.getRolesById(Long.valueOf((String) event.getValues().get(0)));
                    Button backButton = Button.secondary("back-button-shop7", "Назад").withEmoji(guild.getEmojiById(EMOJI_PREV));
                    Button buyButton = Button.primary("buy-button-shop7-" + role.getId(), "Купить!").withDisabled(shopRepository.getByBuyerAndRole(userRepository.getUserById(event.getMember().getIdLong()), role) != null);
                    event.deferEdit().setEmbeds(getShopMsgAbout(event.getMember(), role, true).build())
                            .setActionRow(backButton, buyButton)
                            .queue();
                    break;
                }
                case ("role-select-stock-shop7"): {
                    Roles role = rolesRepository.getRolesById(Long.valueOf((String) event.getValues().get(0)));
                    Button backButton = Button.secondary("back-button-stock-shop7", "Назад").withEmoji(guild.getEmojiById(EMOJI_PREV));
                    Button wearButton = Button.primary("wear-button-shop7-" + role.getId(), "Надеть");
                    Button unwearButton = Button.primary("unwear-no-button-shop7", "Снять");
                    event.deferEdit().setEmbeds(getShopMsgAbout(event.getMember(), role, false).build())
                            .setActionRow(backButton, wearButton, unwearButton)
                            .queue();
                    break;
                }
            }
        }
    }

    public StringSelectMenuImpl getSelectShop() {
        List<SelectOption> selectOptions = new ArrayList<>();
        int i = 1;
        for (Roles role : rolesRepository.findAll()) {
            selectOptions.add(SelectOption.of(i + ". " + guild.getRoleById(role.getId()).getName(), String.valueOf(role.getId())));
            i++;
        }
        if (selectOptions.size() == 0) return null;
        StringSelectMenuImpl selectMenu = new StringSelectMenuImpl("role-select-shop7", "Что приглянулось?", 1, 1, false, selectOptions);
        return selectMenu;
    }

    public EmbedBuilder getAcceptShopMsg(Member member, Roles role) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode(role.getColor()));
        String description = "# Покупка успешно выполнена\n" +
                "### <@&" + role.getId() + ">  Уже в твоём инвентаре!\n";
        description += "Баланс: ~~" +
                (userRepository.getUserById(member.getIdLong()).getCoins() + role.getCoins()) + "~~ <:prize:1241420233407729846>  ->  " +
                userRepository.getUserById(member.getIdLong()).getCoins() + " <:prize:1241420233407729846>";
        embedBuilder.setDescription(description);
        embedBuilder.setThumbnail(role.getAnimationIcon());
        return embedBuilder;
    }

    public EmbedBuilder getShopMsgAbout(Member member, Roles role, Boolean isShop) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode(role.getColor()));
        String title = "Роль";
        if (isShop) title = "Покупка роли";
        String description = "# " + title + "\n" +
                "### <@&" + role.getId() + ">\n" +
                "### " + role.getDescription() + "\n";
        Shop shop = shopRepository.getByBuyerAndRole(userRepository.getUserById(member.getIdLong()), role);
        if (shop != null) {
            description += "Куплена <t:" + shop.getDateTime().getTime() / 1000 + ":f>";
        } else {
            if (isShop) {
                description += "Стоимость: " + role.getCoins() + "<:prize:1241420233407729846>\n" +
                        "Текущий баланс: " + userRepository.getUserById(member.getIdLong()).getCoins() + "<:prize:1241420233407729846>";
            }
        }
        embedBuilder.setDescription(description);
        embedBuilder.setThumbnail(role.getAnimationIcon());
        return embedBuilder;
    }

    public EmbedBuilder getShopMsg(Member member) {
        List<Roles> roles = rolesRepository.findAll();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.orange);
        //embedBuilder.setThumbnail("https://sun9-74.userapi.com/c909628/u572432973/docs/d6/c696e1119fe6/assort.gif?extra=dV-qEbppQ4qTEOiZMvgPjJ9TIJ3-z5NS4gNMd7NdG88CUjbh-4WuxXt4Z6FZ4Aje2qKlaY5qjxdeVluQ2pNO988X0EXIqc381vJ1eO6_yheBQdZIDoedz5RTpVsrxkejVs4Au_f8tGMgDm0KYxWXtpqd");
        int i = 1;
        String description = "## <a:1592ducklaugh:1238511202385002527> Ассортимент магазина <a:1592ducklaugh:1238511202385002527>\n" +
                "Покупка роли позволит тебе изменить цвет отображения \nтвоего имени на сервере, а также изменит цвет твоего профиля.\nДругими словами, сделает тебя только ярче и красивее!\n";
        for (Roles role : roles) {
            description += "### ";
            if (shopRepository.getByBuyerAndRole(userRepository.getUserById(member.getIdLong()), role) != null)
                description += "<:check_mark:1241420207512354886> ";
            description += i + ".  <@&" + role.getId() + ">   -   " + role.getCoins() + "<:prize:1241420233407729846> \n" + role.getDescription() + "\n";
            i++;
        }
        embedBuilder.setDescription(description);
        return embedBuilder;
    }

}
