package it.mineblock.basementx.velocity.together.commands;

import com.google.common.collect.ImmutableList;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import it.mineblock.basementx.velocity.together.manager.PartyManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;

@RequiredArgsConstructor
@Getter
public abstract class CommandArgument {

    public static final Component PREFIX = Component.text()
            .append(Component.text("PARTY!").color(NamedTextColor.DARK_AQUA).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
            .append(Component.text(" ").color(NamedTextColor.WHITE).append(Component.text("").decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))).build();

    protected final PartyManager partyService;
    private final String argument;
    private final int length;


    public static void sendMessage(Player player, String message) {
        player.sendMessage(PREFIX.append(Component.text(message)));
    }

    public static void sendMessage(Player player, Component message, boolean prefix) {
        player.sendMessage(PREFIX.append(message));
    }

    public static void sendMessage(Player player, Component message) {
        sendMessage(player, message, true);
    }

    public abstract void execute(Player player, String[] args);

    public List<String> suggest(CommandSource source, String[] currentArgs) {
        return ImmutableList.of();
    }
}
