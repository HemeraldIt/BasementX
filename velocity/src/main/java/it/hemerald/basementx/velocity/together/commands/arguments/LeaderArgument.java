package it.hemerald.basementx.velocity.together.commands.arguments;

import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.api.party.Party;
import it.hemerald.basementx.velocity.together.commands.CommandArgument;
import it.hemerald.basementx.velocity.together.manager.PartyManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;

public class LeaderArgument extends CommandArgument {

    public LeaderArgument(PartyManager partyService) {
        super(partyService, "leader", 2);
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

        Optional<Player> newLeader = partyService.getTogether().getServer().getPlayer(args[1]);
        if (newLeader.isEmpty()) {
            partyService.sendMessage(player, "Giocatore non trovato.");
            return;
        }

        Optional<Party> targetParty = partyService.getParty(newLeader.get());
        if (targetParty.isEmpty()) {
            partyService.sendMessage(player, "Quel giocatore non è in un party");
            return;
        }

        if (party != targetParty.get()) {
            partyService.sendMessage(player, "Quel giocatore non fa parte del tuo stesso party.");
            return;
        }

        if (newLeader.get() == player) {
            partyService.sendMessage(player, "Sei già il leader del player.");
            return;
        }

        String name = player.getUsername();
        if (name == null) return;

        partyService.deleteParty(name);
        party.setLeader(newLeader.get().getUsername());

        getPartyService().saveParty(party);

        partyService.broadcastMessage(party,
                Component.text("§b" + player.getUsername())
                        .append(Component.text(" ha nominato ")).color(NamedTextColor.GRAY)
                        .append(Component.text(newLeader.get().getUsername()).color(NamedTextColor.GREEN))
                        .append(Component.text(" leader.")).color(NamedTextColor.GRAY));
    }
}
