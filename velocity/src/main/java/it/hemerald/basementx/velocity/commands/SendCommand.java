package it.hemerald.basementx.velocity.commands;

import com.google.common.collect.ImmutableList;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import it.hemerald.basementx.velocity.BasementVelocity;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class SendCommand implements SimpleCommand {

    private final BasementVelocity velocity;

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args  = invocation.arguments();

        if(args.length < 2) {
            source.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Uso corretto: /send <player> <server>").color(NamedTextColor.RED)));
            return;
        }

        Optional<Player> optionalPlayer = velocity.getServer().getPlayer(args[0]);
        if(optionalPlayer.isEmpty()) {
            if (args[0].equalsIgnoreCase("current")) {
                if (source instanceof Player sender) {
                    sender.getCurrentServer().ifPresent(server -> {

                        Optional<RegisteredServer> optionalServerTo = velocity.getServer().getServer(args[1]);
                        if(optionalServerTo.isEmpty()) {
                            source.sendMessage(Component.text()
                                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                                    .append(Component.text("Non è stato possibile mandare nessun giocatore in questo server!").color(NamedTextColor.RED)));
                            return;
                        }

                        RegisteredServer registeredServer = optionalServerTo.get();
                        if(server.getServerInfo().getName().equalsIgnoreCase(registeredServer.getServerInfo().getName())) {
                            source.sendMessage(Component.text()
                                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                                    .append(Component.text("I giocatori si trovano già in quel nel server!").color(NamedTextColor.RED)));
                            return;
                        }

                        server.getServer().getPlayersConnected().forEach(player -> player.createConnectionRequest(registeredServer).fireAndForget());
                    });
                } else {
                    source.sendMessage(Component.text()
                            .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                            .append(Component.text("Non puoi usare questo comando da console!").color(NamedTextColor.RED)));
                }
                return;
            }

            source.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Giocatore non trovato!").color(NamedTextColor.RED)));
            return;
        }

        Player player = optionalPlayer.get();

        Optional<RegisteredServer> optionalServerTo = velocity.getServer().getServer(args[1]);
        Optional<ServerConnection> optionalServerConnection = player.getCurrentServer();
        if(optionalServerTo.isEmpty() || optionalServerConnection.isEmpty()) {
            source.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Non è stato possibile mandare il giocatore in questo server!").color(NamedTextColor.RED)));
            return;
        }

        RegisteredServer registeredServer = optionalServerTo.get();
        ServerConnection serverConnectionFrom = optionalServerConnection.get();
        if(serverConnectionFrom.getServerInfo().getName().equalsIgnoreCase(registeredServer.getServerInfo().getName())) {
            source.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Il giocatore si trova già in quel nel server!").color(NamedTextColor.RED)));
            return;
        }

        player.createConnectionRequest(registeredServer).fireAndForget();
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return CompletableFuture.supplyAsync(() -> {
            if(invocation.arguments().length == 0) return ImmutableList.of();
            if(invocation.arguments().length == 1) return velocity.getServer().getAllPlayers().parallelStream().map(Player::getUsername)
                    .filter(username -> username.toLowerCase().startsWith(invocation.arguments()[0].toLowerCase())).toList();
            return velocity.getServer().getAllServers().parallelStream()
                    .map(registeredServer -> registeredServer.getServerInfo().getName())
                    .filter(name -> name.toLowerCase().startsWith(invocation.arguments()[1].toLowerCase())).toList();
        });
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("basement.send");
    }
}
