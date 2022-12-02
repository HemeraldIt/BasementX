package it.hemerald.basementx.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.api.persistence.maria.queries.builders.WhereBuilder;
import it.hemerald.basementx.api.persistence.maria.queries.builders.data.QueryBuilderDelete;
import it.hemerald.basementx.api.persistence.maria.queries.builders.data.QueryBuilderInsert;
import it.hemerald.basementx.api.persistence.maria.queries.builders.data.QueryBuilderSelect;
import it.hemerald.basementx.api.persistence.maria.structure.data.QueryData;
import it.hemerald.basementx.velocity.BasementVelocity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class StaffNoteCommand implements SimpleCommand {

    private final Component help = Component.text("/staffnotes add <player> <nota>").color(NamedTextColor.DARK_AQUA)
            .append(Component.newline())
            .append(Component.text("/staffnotes remove <player> <id nota>").color(NamedTextColor.DARK_AQUA))
            .append(Component.newline())
            .append(Component.text("/staffnotes list <player>"));

    private final BasementVelocity velocity;

    private final QueryBuilderSelect playerId;
    private final QueryBuilderSelect playerData;
    private final QueryBuilderSelect noteId;

    private final QueryBuilderInsert queryInsertNotes;
    private final QueryBuilderSelect queryGetNotes;
    private final QueryBuilderDelete queryRemoveNotes;

    public StaffNoteCommand(BasementVelocity velocity) {
        this.velocity = velocity;

        playerId = velocity.getDatabase().select().columns("id").from("players");
        playerData = velocity.getDatabase().select().columns("uuid", "username").from("players");
        noteId = velocity.getDatabase().select().columns("COUNT(note_id)+1").from("staff_notes as sn");

        queryInsertNotes = velocity.getDatabase().insert().ignore(true).into("staff_notes").columnSchema("staff_id", "player_id", "note_id", "note");
        queryGetNotes = velocity.getDatabase().select().columns("note_id", "note", "players.username")
                .from("staff_notes", "players", "players" + " as ps");
        queryRemoveNotes = velocity.getDatabase().delete().from("staff_notes");
    }

    @Override
    public void execute(Invocation invocation) {
        if(!(invocation.source() instanceof Player player)) return;
        String[] args = invocation.arguments();

        if(args.length < 2) {
            player.sendMessage(help);
            return;
        }
        String argument = args[0].toLowerCase();
        if(!validArg(argument)) {
            player.sendMessage(help);
            return;
        }
        String playerUUID;
        String playerName;

        Optional<Player> targetOptional = velocity.getServer().getPlayer(args[1]);
        if(targetOptional.isEmpty()) {
            QueryData queryData = this.playerData.patternClone()
                    .where(WhereBuilder.builder().equals("username", args[1]).close()).build().execReturn();
            if(queryData.first()) {
                playerUUID = queryData.getString(1);
                playerName = queryData.getString(2);
            } else {
                player.sendMessage(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)
                        .append(Component.text("Il giocatore non si è mai connesso!")
                                .color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)));
                return;
            }
        } else {
            Player target = targetOptional.get();
            playerUUID = target.getUniqueId().toString();
            playerName = target.getUsername();
        }


        switch (args[0].toLowerCase()) {
            case "add" -> {
                if(args.length < 3) {
                    player.sendMessage(help);
                    return;
                }

                queryInsertNotes.patternClone()
                        .values(playerId(player.getUniqueId().toString()), playerId(playerUUID),
                                noteId.patternClone().where(WhereBuilder.builder().equals("sn.player_id", playerId(playerUUID)).close()),
                                builder(args, 2)).build().execAsync();

                player.sendMessage(Component.text("SUCCESSO! ").color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)
                        .append(Component.text("Nota inserita al giocatore " + playerName)
                                .color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)));
            }
            case "remove" -> {
                if(args.length < 3) {
                    player.sendMessage(help);
                    return;
                }

                int id;
                try {
                    id = Integer.parseInt(args[2]);
                } catch (NumberFormatException ignored) {
                    player.sendMessage(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)
                            .append(Component.text("Inserisci un numero")
                                    .color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)));
                    return;
                }

                WhereBuilder whereBuilder = WhereBuilder.builder().equals("player_id", playerId(playerUUID)).and().equals("note_id", id);
                QueryBuilderDelete query = queryRemoveNotes.patternClone();
                if(player.hasPermission("basement.staff.admin")) {
                    query.where(whereBuilder.close());
                } else {
                    query.where(whereBuilder.and().equals("staff_id", playerId(player.getUniqueId().toString())).close());
                }
                query.build().execAsync();
                player.sendMessage(Component.text("SUCCESSO! ").color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)
                        .append(Component.text("Nota rimossa al giocatore " + playerName)
                                .color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)));
            }
            case "list" -> queryGetNotes.patternClone()
                    .where(WhereBuilder.builder().equalsNQ("staff_id", "players.id")
                            .and().equalsNQ("player_id", "ps.id")
                            .and().equals("ps.uuid", playerUUID).close())
                    .build().execReturnAsync().thenAccept(queryData -> {
                        if(!queryData.isBeforeFirst()) {
                            player.sendMessage(Component.text("ERRORE! ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)
                                    .append(Component.text("Il giocatore non ha nessuna nota")
                                            .color(NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)));
                            return;
                        }
                        Component message = Component.text("Lista delle note di " + playerName + ":").color(NamedTextColor.DARK_AQUA);
                        while (queryData.next()) {
                            message = message.append(Component.newline())
                                    .append(Component.text(queryData.getInt(1) + ". ").color(NamedTextColor.YELLOW))
                                    .append(Component.text(queryData.getString(2)).color(NamedTextColor.AQUA))
                                    .append(Component.text(" (by " + queryData.getString(3) + ")").color(NamedTextColor.DARK_AQUA));
                        }
                        player.sendMessage(message);
                    });
            default -> player.sendMessage(help);
        }
    }

    private boolean validArg(String argument) {
        return argument.equals("add") || argument.equals("remove") || argument.equals("list");
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("basement.staff");
    }

    private String builder(String[] args, int start) {
        StringBuilder builder = new StringBuilder(args[start]);
        for(int i = start+1; i < args.length; i++) builder.append(" ").append(args[i]);
        return builder.toString();
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return CompletableFuture.supplyAsync(() -> {
            String[] args = invocation.arguments();
            if(args.length < 2) {
                return List.of("add", "remove", "list");
            } else if(args.length == 2) {
                return velocity.getServer().getAllPlayers().parallelStream().map(Player::getUsername)
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase())).toList();
            }
            return List.of();
        });
    }

    private QueryBuilderSelect playerId(String uuid) {
        return playerId.patternClone().where(WhereBuilder.builder().equals("uuid", uuid).close());
    }
}
