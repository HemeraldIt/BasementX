package it.hemerald.basementx.velocity.together.commands.arguments;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import it.hemerald.basementx.api.party.Party;
import it.hemerald.basementx.api.redis.messages.implementation.PartyWarpMessage;
import it.hemerald.basementx.velocity.together.commands.CommandArgument;
import it.hemerald.basementx.velocity.together.manager.PartyManager;

import java.util.Optional;

public class WarpArgument extends CommandArgument {

    public WarpArgument(PartyManager partyService) {
        super(partyService, "warp", 1);
    }

    @Override
    public void execute(Player player, String[] args) {
        Optional<Party> optional = partyService.getParty(player);

        if (optional.isEmpty()) {
            partyService.sendMessage(player, "Devi essere in un party per eseguire questo comando!");
            return;
        }

        Party party = optional.get();
        if (!party.getLeader().equals(player.getUsername())) {
            partyService.sendMessage(player, "Non hai i permessi sufficienti per nominare un nuovo leader.");
            return;
        }

        Optional<ServerConnection> serverConnectionOptional = player.getCurrentServer();

        if (serverConnectionOptional.isEmpty()) {
            partyService.sendMessage(player, "Non sei connesso a nessuno server!");
            return;
        }

        String serverName = serverConnectionOptional.get().getServer().getServerInfo().getName();
        if (serverName.contains("_instance_") || serverName.contains("_server_")) {
            partyService.sendMessage(player, "Non puoi effettuare il party warp in questo server!");
            return;
        }

        for (String memberName : party.getMembers()) {
            partyService.getTogether().getBasement().getRedisManager().publishMessage(new PartyWarpMessage(memberName, serverName));
        }
    }
}
