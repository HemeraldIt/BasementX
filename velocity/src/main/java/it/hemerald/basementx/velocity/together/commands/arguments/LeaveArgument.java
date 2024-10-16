package it.hemerald.basementx.velocity.together.commands.arguments;

import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.api.party.Party;
import it.hemerald.basementx.velocity.together.commands.CommandArgument;
import it.hemerald.basementx.velocity.together.manager.PartyManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;

public class LeaveArgument extends CommandArgument {

    public LeaveArgument(PartyManager partyService) {
        super(partyService, "leave", 1);
    }

    @Override
    public void execute(Player player, String[] args) {
        Optional<Party> targetParty = partyService.getParty(player);

        if (targetParty.isEmpty()) {
            partyService.sendMessage(player, "Devi essere in un party per eseguire questo comando!");
            return;
        }

        partyService.sendMessage(player, Component.text("Sei uscito dal party.").color(NamedTextColor.GRAY));
        partyService.leave(player);
    }
}
