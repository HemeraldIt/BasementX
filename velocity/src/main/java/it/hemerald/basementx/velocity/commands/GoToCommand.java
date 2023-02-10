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
public class GoToCommand implements SimpleCommand {

    private final BasementVelocity velocity;

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!(source instanceof Player)) {
            source.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Questo comando è eseguibile sono in game!").color(NamedTextColor.RED)));
            return;
        }

        if (args.length != 1) {
            source.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Uso corretto: /goto <server>").color(NamedTextColor.RED)));
            return;
        }

        Optional<RegisteredServer> optionalServer = velocity.getServer().getServer(args[0]);
        if (optionalServer.isEmpty()) {
            source.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Server non trovato").color(NamedTextColor.RED)));
            return;
        }

        RegisteredServer server = optionalServer.get();

        Player player = (Player) invocation.source();
        Optional<ServerConnection> serverConnectionOptional = player.getCurrentServer();
        if (serverConnectionOptional.isEmpty()) {
            source.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Ci dispiace non è stato possibile connetterti al server").color(NamedTextColor.RED)));
            return;
        }

        ServerConnection serverConnectionFrom = serverConnectionOptional.get();
        if (serverConnectionFrom.getServerInfo().getName().equalsIgnoreCase(server.getServerInfo().getName())) {
            source.sendMessage(Component.text()
                    .append(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .append(Component.text("Ti trovi già in quel server").color(NamedTextColor.RED)));
            return;
        }

        player.createConnectionRequest(server).fireAndForget();
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        if (invocation.arguments().length == 0) return CompletableFuture.completedFuture(ImmutableList.of());
        return CompletableFuture.supplyAsync(() -> velocity.getServer().getAllServers().parallelStream()
                .map(registeredServer -> registeredServer.getServerInfo().getName())
                .filter(name -> name.toLowerCase().startsWith(invocation.arguments()[0].toLowerCase())).toList());
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("basement.staff");
    }
}
