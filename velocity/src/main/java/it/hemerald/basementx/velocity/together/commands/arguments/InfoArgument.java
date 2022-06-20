package it.hemerald.basementx.velocity.together.commands.arguments;

import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.velocity.together.commands.CommandArgument;
import it.hemerald.basementx.velocity.together.manager.PartyManager;
import it.hemerlad.basementx.api.party.Party;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;

public class InfoArgument extends CommandArgument {

    public InfoArgument(PartyManager partyService) {
        super(partyService, "info", 1);
    }

    @Override
    public void execute(Player player, String[] args) {
        Optional<Party> targetParty = partyService.getParty(player);

        if (targetParty.isEmpty()) {
            sendMessage(player, "Non sei in nessun party.");
            return;
        }

        sendMessage(player, Component.text("Party di ").color(NamedTextColor.AQUA)
                .append(Component.text(targetParty.get().getLeader()).color(NamedTextColor.GRAY))
                .append(Component.text(" (" + (targetParty.get().getMembers().size()) + ")").color(NamedTextColor.GRAY)), false);
        if (!targetParty.get().getMembers().isEmpty()) {
            sendMessage(player, Component.text("Membri: ").color(NamedTextColor.AQUA).append(Component.text(String.join(", ", targetParty.get().getMembers())).color(NamedTextColor.GRAY)));
        }
    }
}
