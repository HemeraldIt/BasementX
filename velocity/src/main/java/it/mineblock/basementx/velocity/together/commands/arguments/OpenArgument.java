package it.mineblock.basementx.velocity.together.commands.arguments;

import com.velocitypowered.api.proxy.Player;
import it.mineblock.basementx.api.party.Party;
import it.mineblock.basementx.velocity.together.commands.CommandArgument;
import it.mineblock.basementx.velocity.together.manager.PartyManager;

import java.util.Optional;

public class OpenArgument extends CommandArgument {

    public OpenArgument(PartyManager partyService) {
        super(partyService, "open", 1);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("party.open")) {
            sendMessage(player, "Nessun permesso!");
            return;
        }


        Optional<Party> optional = partyService.getParty(player);
        Party party;
        if (optional.isEmpty()) {
            party = partyService.createParty(player);
        } else {
            party = optional.get();
            if (!party.getLeader().equals(player.getUsername())) {
                sendMessage(player, "Non sei il leader.");
                return;
            }

            if (party.isFull()) {
                sendMessage(player, "Il party è pieno!");
                return;
            }
        }

        if (party.isOpen()) {
            sendMessage(player, "Party chiuso!");
        } else {
            sendMessage(player, "Party aperto!");
            /*player.getCurrentServer().ifPresent(serverConnection -> serverConnection.getServer().sendMessage(
                    Component.text(player.getUsername()).color(NamedTextColor.AQUA)
                            .append(Component.text(" sta hostando un party pubblico, clicca per entrare!").color(NamedTextColor.GRAY))
                            .clickEvent(ClickEvent.runCommand("/party join " + player.getUsername()))));*/
        }

        party.setOpen(!party.isOpen());
    }
}
