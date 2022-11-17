package it.hemerald.basementx.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.velocity.BasementVelocity;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

@RequiredArgsConstructor
public class GlobalChatCommand implements SimpleCommand {

    private final BasementVelocity velocity;

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        String username = "CONSOLE";

        if (source instanceof Player player) {
            username = player.getUsername();
        }

        if (args.length > 0) {
            sendBroadcast(username, String.join(" ", args));
            return;
        }

        source.sendMessage(Component.text("Nessun messaggio inserito!").color(NamedTextColor.RED));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("basement.globalchat");
    }

    private void sendBroadcast(String username, String message) {
        Component globalChat = Component.text()
                .append(Component.text("GC! ").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                .append(Component.text(username).color(NamedTextColor.DARK_RED))
                .append(Component.text(": ").color(NamedTextColor.GRAY))
                .append(LegacyComponentSerializer.legacyAmpersand().deserialize("&7" + message)).build();
        for (Player player : velocity.getServer().getAllPlayers()) {
            player.sendMessage(globalChat);
        }
    }
}
