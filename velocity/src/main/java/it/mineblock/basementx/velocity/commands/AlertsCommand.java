package it.mineblock.basementx.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import it.mineblock.basementx.velocity.BasementVelocity;
import it.mineblock.basementx.velocity.alert.AlertType;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Map;

@RequiredArgsConstructor
public class AlertsCommand implements SimpleCommand {

    private final BasementVelocity velocity;

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player)) return;

        Player player = (Player) invocation.source();

        if (!player.hasPermission("mineblock.alerts")) {
            player.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Non hai il permesso per eseguire quest'azione!").color(NamedTextColor.RED)));
            return;
        }

        Map<String, AlertType> toggled = velocity.getToggled();

        if (!toggled.containsKey(player.getUsername())) toggled.put(player.getUsername(), AlertType.GLOBAL);

        AlertType newAlert = toggled.get(player.getUsername()).next();
        toggled.put(player.getUsername(), newAlert);
        player.sendMessage(newAlert.getComponent());
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("mineblock.alerts");
    }
}
