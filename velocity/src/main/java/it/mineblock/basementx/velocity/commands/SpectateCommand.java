package it.mineblock.basementx.velocity.commands;

import com.google.common.collect.ImmutableList;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import it.mineblock.basementx.api.redis.messages.implementation.SpectateMessage;
import it.mineblock.basementx.velocity.BasementVelocity;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class SpectateCommand implements SimpleCommand {

    private final BasementVelocity velocity;

    @Override
    public void execute(Invocation invocation) {
        if(!(invocation.source() instanceof Player player)) return;
        if(invocation.arguments().length < 1) {
            player.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Uso corretto: /spectate <player>!").color(NamedTextColor.RED)));
            return;
        }
        Optional<Player> targetOptional = velocity.getServer().getPlayer(invocation.arguments()[0]);
        if(targetOptional.isEmpty()) {
            player.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Giocatore non trovato!").color(NamedTextColor.RED)));
            return;
        }
        Player target = targetOptional.get();
        velocity.getBasement().getRedisManager().publishMessage(new SpectateMessage(player.getUsername(), target.getUsername(),
                player.getCurrentServer().get().getServerInfo().getName(), target.getCurrentServer().get().getServerInfo().getName()));
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        if(invocation.arguments().length == 0) return CompletableFuture.completedFuture(ImmutableList.of());
        return CompletableFuture.supplyAsync(() -> velocity.getServer().getAllPlayers().parallelStream().map(Player::getUsername)
                .filter(username -> username.toLowerCase().startsWith(invocation.arguments()[0].toLowerCase())).toList());
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("basement.spectate");
    }
}
