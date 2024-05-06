package com.example.bot;

import java.util.Arrays;

public class enums {
    public enum commands{
        TEXT_COMMAND (new String[]{"кря", "некря"}),
        TEXT_MESSAGE (new String[]{"не крякай тут!", "правильно, не крякай!"});

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
