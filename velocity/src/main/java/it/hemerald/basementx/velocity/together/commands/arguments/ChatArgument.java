package it.hemerald.basementx.velocity.together.commands.arguments;

import com.velocitypowered.api.proxy.Player;
import it.hemerlad.basementx.api.party.Party;
import it.hemerald.basementx.velocity.together.commands.CommandArgument;
import it.hemerald.basementx.velocity.together.manager.PartyManager;

import java.util.Optional;

public class ChatArgument extends CommandArgument {

    public ChatArgument(PartyManager partyService) {
        super(partyService, "chat", 1);
    }

    @Override
    public void execute(Player player, String[] args) {
        Optional<Party> targetParty = partyService.getParty(player);

        if (targetParty.isEmpty()) {
            sendMessage(player, "Non sei in nessun party.");
            return;
        }

        partyService.toggleChat(player);
    }
}
