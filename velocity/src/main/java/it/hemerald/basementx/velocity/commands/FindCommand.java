package it.hemerald.basementx.velocity.commands;

import com.google.common.collect.ImmutableList;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import it.hemerald.basementx.velocity.BasementVelocity;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class FindCommand implements SimpleCommand {

    private final BasementVelocity velocity;

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args  = invocation.arguments();
        if(args.length != 1) {
            source.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Uso corretto: /find <player>").color(NamedTextColor.RED)));
            return;
        }

        Optional<Player> optionalPlayer = velocity.getServer().getPlayer(args[0]);
        if(optionalPlayer.isEmpty()) {
            source.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Giocatore non trovato").color(NamedTextColor.RED)));
            return;
        }

        Player player = optionalPlayer.get();

        Optional<ServerConnection> optionalServer = player.getCurrentServer();
        if(optionalServer.isEmpty()) {
            source.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Server non trovato").color(NamedTextColor.RED)));
            return;
        }

        source.sendMessage(Component.text("Il giocatore " + player.getUsername() + " si trova nel server " + optionalServer.get().getServerInfo().getName())
                .color(NamedTextColor.DARK_AQUA).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE));
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        if(invocation.arguments().length == 0) return CompletableFuture.completedFuture(ImmutableList.of());
        return CompletableFuture.supplyAsync(() -> velocity.getServer().getAllPlayers().parallelStream().map(Player::getUsername)
                .filter(username -> username.toLowerCase().startsWith(invocation.arguments()[0].toLowerCase())).toList());
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("basement.staff");
    }
}
