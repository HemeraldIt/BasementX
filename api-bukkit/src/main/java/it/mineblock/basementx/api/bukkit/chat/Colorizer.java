package it.mineblock.basementx.api.bukkit.chat;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Colorizer {

    private static final Pattern hexColorPattern = Pattern.compile("#([A-Fa-f0-9]{6})");
    private static boolean useAllColors;

    static {
        Matcher matcher = Pattern.compile("[0-9]\\.[0-9]+").matcher(Bukkit.getBukkitVersion());

        if (matcher.find()) {
            try {
                useAllColors = Integer.parseInt(matcher.group(0).split("\\.")[1]) >= 16;
            } catch (NumberFormatException ignored) {
                useAllColors = false;
            }
        }
    }

    private Colorizer() {
        throw new AssertionError("Nope");
    }

    public static String colorize(String string) {
        string =  ChatColor.translateAlternateColorCodes('&', string);
        string = translateHex(string);
        return string;
    }

    public static List<String> colorize(List<String> strings) {
        return strings.stream().map(Colorizer::colorize).collect(Collectors.toList());
    }

    public static List<String> colorize(String... strings) {
        return Arrays.stream(strings).map(Colorizer::colorize).collect(Collectors.toList());
    }

    public static String translateHex(String msg) {
        Preconditions.checkNotNull(msg, "Cannot translate null text");

        msg = msg.replace("&g", "#2196F3")
                .replace("&h", "#2962FF");

        if (!useAllColors) return msg;

        Matcher matcher = hexColorPattern.matcher(msg);
        while (matcher.find()) {
            String match = matcher.group(0);
            msg = msg.replace(match, net.md_5.bungee.api.ChatColor.of(match).toString());
        }

        return msg;
    }
}
