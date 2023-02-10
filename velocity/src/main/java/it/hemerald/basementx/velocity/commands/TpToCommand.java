package it.hemerald.basementx.velocity.commands;

import com.google.common.collect.ImmutableList;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import it.hemerald.basementx.api.redis.messages.implementation.TpToMessage;
import it.hemerald.basementx.velocity.BasementVelocity;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class TpToCommand implements SimpleCommand {

    private final BasementVelocity velocity;

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!(source instanceof Player from)) {
            source.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Questo comando è eseguibile sono in game!").color(NamedTextColor.RED)));
            return;
        }

        if (args.length != 1) {
            source.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Uso corretto: /tpto <player>").color(NamedTextColor.RED)));
            return;
        }

        Optional<Player> optionalPlayer = velocity.getServer().getPlayer(args[0]);
        if (optionalPlayer.isEmpty()) {
            source.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Giocatore non trovato!").color(NamedTextColor.RED)));
            return;
        }

        Player to = optionalPlayer.get();
        if (to.getUsername().equalsIgnoreCase(from.getUsername())) {
            source.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Non puoi teletrasportarti da te stesso!").color(NamedTextColor.RED)));
            return;
        }

        Optional<ServerConnection> optionalServerTo = to.getCurrentServer();
        if (optionalServerTo.isEmpty()) {
            source.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Non è stato possibile connettersi al server di questo giocatore!").color(NamedTextColor.RED)));
            return;
        }

        Optional<ServerConnection> optionalServerFrom = from.getCurrentServer();
        if (optionalServerFrom.isEmpty()) {
            source.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Non è stato possibile connettersi al server di questo giocatore!").color(NamedTextColor.RED)));
            return;
        }

        ServerConnection serverConnectionTo = optionalServerTo.get();
        ServerConnection serverConnectionFrom = optionalServerFrom.get();
        if (serverConnectionFrom.getServerInfo().getName().equalsIgnoreCase(serverConnectionTo.getServerInfo().getName())) {
            velocity.getBasement().getRedisManager().publishMessage(new TpToMessage(from.getUsername(), to.getUsername()));
            return;
        }

        from.createConnectionRequest(serverConnectionTo.getServer()).fireAndForget();
        velocity.getBasement().getRedisManager().publishMessage(new TpToMessage(from.getUsername(), to.getUsername()));
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        if (invocation.arguments().length == 0) return CompletableFuture.completedFuture(ImmutableList.of());
        return CompletableFuture.supplyAsync(() -> velocity.getServer().getAllPlayers().parallelStream().map(Player::getUsername)
                .filter(username -> username.toLowerCase().startsWith(invocation.arguments()[0].toLowerCase())).toList());
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("basement.staff");
    }
}
