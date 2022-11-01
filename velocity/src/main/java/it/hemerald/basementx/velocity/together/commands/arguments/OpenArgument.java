package it.hemerald.basementx.velocity.together.commands.arguments;

import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.api.party.Party;
import it.hemerald.basementx.velocity.together.commands.CommandArgument;
import it.hemerald.basementx.velocity.together.manager.PartyManager;

import java.util.Optional;

public class OpenArgument extends CommandArgument {

    public OpenArgument(PartyManager partyService) {
        super(partyService, "open", 1);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("party.open")) {
            partyService.sendMessage(player, "Non hai il permesso di eseguire questa azione!");
            return;
        }

        Optional<Party> optional = partyService.getParty(player);
        Party party;
        if (optional.isEmpty()) {
            party = partyService.createParty(player);
        } else {
            party = optional.get();
            if (!party.getLeader().equals(player.getUsername())) {
                partyService.sendMessage(player, "Non sei il leader.");
                return;
            }

            if (party.isFull()) {
                partyService.sendMessage(player, "Il party è pieno!");
                return;
            }
        }

        if (party.isOpen()) {
            partyService.sendMessage(player, "Party chiuso!");
        } else {
            partyService.sendMessage(player, "Party aperto!");
            /*player.getCurrentServer().ifPresent(serverConnection -> serverConnection.getServer().partyService.sendMessage(
                    Component.text(player.getUsername()).color(NamedTextColor.AQUA)
                            .append(Component.text(" sta hostando un party pubblico, clicca per entrare!").color(NamedTextColor.GRAY))
                            .clickEvent(ClickEvent.runCommand("/party join " + player.getUsername()))));*/
        }

        party.setOpen(!party.isOpen());
    }
}
