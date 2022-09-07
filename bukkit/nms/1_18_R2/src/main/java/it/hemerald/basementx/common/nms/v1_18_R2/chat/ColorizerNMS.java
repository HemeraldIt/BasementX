package it.hemerald.basementx.common.nms.v1_18_R2.chat;

import com.google.common.base.Preconditions;
import it.hemerald.basementx.api.bukkit.chat.Colorizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorizerNMS implements Colorizer.NMS {

    private static final Pattern hexColorPattern = Pattern.compile("#([A-Fa-f0-9]{6})");

    @Override
    public String translateHex(String msg) {
        Preconditions.checkNotNull(msg, "Cannot translate null text");

        msg = msg.replace("&g", "#2196F3")
                .replace("&h", "#2962FF");

        Matcher matcher = hexColorPattern.matcher(msg);
        while (matcher.find()) {
            String match = matcher.group(0);
            msg = msg.replace(match, net.md_5.bungee.api.ChatColor.of(match).toString());
        }

        return msg;
    }
}
