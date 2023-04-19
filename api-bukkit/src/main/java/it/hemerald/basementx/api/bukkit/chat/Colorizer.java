package it.hemerald.basementx.api.bukkit.chat;

import lombok.Setter;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class Colorizer {

    @Setter
    private static NMS nms;

    private Colorizer() {
        throw new AssertionError("Nope");
    }

    public static String colorize(String string) {
        string = ChatColor.translateAlternateColorCodes('&', string);
        return nms.translateHex(string);
    }

    public static List<String> colorize(List<String> strings) {
        return strings.stream().map(Colorizer::colorize).collect(Collectors.toList());
    }

    public static List<String> colorize(String... strings) {
        return Arrays.stream(strings).map(Colorizer::colorize).collect(Collectors.toList());
    }

    public static String translateHex(String msg) {
        return nms.translateHex(msg);
    }

    public static class NMS {

        public String translateHex(String msg) {
            return msg;
        }

    }
}