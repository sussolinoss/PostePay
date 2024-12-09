package dev.sussolino.postepay.utils.color;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {

    public static String color(String message) {
        message = message.replace('&', '§');

        Matcher matcher = Pattern.compile("&#([A-Fa-f\\d]{6})").matcher(message);

        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            String group = matcher.group(1);

            String replacement = "§x§" + group.charAt(0) + "§" + group.charAt(1) + "§" +
                    group.charAt(2) + "§" + group.charAt(3) + "§" +
                    group.charAt(4) + "§" + group.charAt(5);
            matcher.appendReplacement(buffer, replacement);
        }

        matcher.appendTail(buffer);

        return buffer.toString();
    }

    public static List<String> colorList(List<String> list) {
        List<String> colorList = new ArrayList<>();
        list.forEach(string -> colorList.add(color(string)));
        return colorList;
    }
}

