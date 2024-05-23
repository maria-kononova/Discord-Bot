package com.example.bot;

import java.util.Arrays;

import static com.example.bot.BotApplication.guild;
import static com.example.bot.listeners.ControlListener.MODERATOR_ROLE;
import static com.example.bot.listeners.ControlListener.SUPPORT_ROLE;

public class enums {
    public enum commands{
        TEXT_COMMAND (new String[]{"кря", "некря", "Утёнок", "глупый", "вопрос", "буллить", "буллит", "плачу", "ем", "пойду есть"}),
        TEXT_MESSAGE (new String[]{"Не крякай тут!", "Правильно, не крякай!", "Всегда тут!", "<@&"+MODERATOR_ROLE+ "> тут id обзывается!",
        "По всем вопросам всегда можно обратиться к <@&"+SUPPORT_ROLE+">", "Никакого буль-буллинга", "Никакого буль-буллинга", "Не плакай," +
                " когда-нибудь всё обязательно будет хорошо!"});

        private final String[] titles;

        commands(String[] titles) {
            this.titles = titles;
        }

        public String[] getTitles() {
            return titles;
        }

        @Override
        public String toString() {
            return "commands{" +
                    "title='" + Arrays.toString(titles) + '\'' +
                    '}';
        }
    }
    public enum messages {


    }
}
