package it.hemerald.basementx.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.api.player.UserData;
import it.hemerald.basementx.velocity.BasementVelocity;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class ToggleDisguiseCommand implements SimpleCommand {

    private final BasementVelocity velocity;
    private final List<String> cooldown = new ArrayList<>();

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player player)) return;

        if(cooldown.contains(player.getUsername())) {
            player.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Sei in cooldown!").color(NamedTextColor.RED)));
            return;
        }

        Optional<UserData> optionalUserData = velocity.getUserDataManager().getUserData(player.getUniqueId());
        if(optionalUserData.isPresent() && optionalUserData.get().getStreamMode()) {
            player.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Non puoi effettuare il comando mentre sei in StreamMode!").color(NamedTextColor.RED)));
            return;
        }

        cooldown.add(player.getUsername());
        velocity.getServer().getScheduler().buildTask(velocity, () -> cooldown.remove(player.getUsername())).delay(4, TimeUnit.SECONDS).schedule();

        if (velocity.getBasement().getPlayerManager().isDisguised(player.getUsername())) {
            velocity.getBasement().getPlayerManager().undisguise(player.getUsername());
            player.sendMessage(Component.text()
                    .append(Component.text("DISGUISE! ").color(NamedTextColor.DARK_AQUA).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Sei uscito dalla modalità disguise!").color(NamedTextColor.WHITE)));
        } else {
            velocity.getBasement().getPlayerManager().disguise(player.getUsername());
            player.sendMessage(Component.text()
                    .append(Component.text("DISGUISE! ").color(NamedTextColor.DARK_AQUA).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Sei entrato nella modalità disguise!").color(NamedTextColor.WHITE)));
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("basement.disguise");
    }
}
