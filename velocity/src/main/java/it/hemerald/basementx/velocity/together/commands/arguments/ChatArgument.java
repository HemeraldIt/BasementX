package it.hemerald.basementx.velocity.together.commands.arguments;

import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.api.party.Party;
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
            partyService.sendMessage(player, "Devi essere in un party per eseguire questo comando!");
            return;
        }

        partyService.toggleChat(player);
    }
}
