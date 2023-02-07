package it.hemerald.basementx.velocity.together.commands.arguments;

import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.api.party.Party;
import it.hemerald.basementx.velocity.together.commands.CommandArgument;
import it.hemerald.basementx.velocity.together.manager.PartyManager;
import net.kyori.adventure.text.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ListArgument extends CommandArgument {

    public ListArgument(PartyManager partyService) {
        super(partyService, "list", 1);
    }

    @Override
    public void execute(Player player, String[] args) {
        Optional<Party> targetParty = partyService.getParty(player);

        if (targetParty.isEmpty()) {
            partyService.sendMessage(player, "Devi essere in un party per eseguire questo comando!");
            return;
        }

        partyService.sendMessage(player, Component.text("§7Leader: §a" + targetParty.get().getLeader() + " §7(§e" + (targetParty.get().getFriends().size()) + "§7)"));

        if (targetParty.get().getFriends().size() > 1) {
            Set<String> members = new HashSet<>(targetParty.get().getFriends());
            members.remove(targetParty.get().getLeader());
            partyService.sendMessage(player, Component.text("§7Membri: §b" + String.join("§7, §b", members)));
        }
    }
}
