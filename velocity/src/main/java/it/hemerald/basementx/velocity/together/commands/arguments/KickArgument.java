package it.hemerald.basementx.velocity.together.commands.arguments;

import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.api.party.Party;
import it.hemerald.basementx.velocity.together.commands.CommandArgument;
import it.hemerald.basementx.velocity.together.manager.PartyManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;

public class KickArgument extends CommandArgument {

    public KickArgument(PartyManager partyService) {
        super(partyService, "kick", 2);
    }

    @Override
    public void execute(Player player, String[] args) {
        Optional<Party> optional = partyService.getParty(player);
        if (optional.isEmpty()) {
            partyService.sendMessage(player, "Devi essere in un party per eseguire questo comando!");
            return;
        }

        if (!optional.get().getLeader().equals(player.getUsername())) {
            partyService.sendMessage(player, "Non sei il leader del party.");
            return;
        }

        Optional<Player> kickedPlayer = partyService.getTogether().getServer().getPlayer(args[1]);
        if (kickedPlayer.isEmpty()) {
            partyService.sendMessage(player, "Giocatore non trovato.");
            return;
        }

        if (kickedPlayer.get() == player) {
            partyService.sendMessage(player, "Non puoi auto kickarti.");
            return;
        }

        Optional<Party> targetParty = partyService.getParty(kickedPlayer.get());
        if (targetParty.isEmpty()) {
            partyService.sendMessage(player, kickedPlayer.get().getUsername() + " non è in un party.");
            return;
        }

        if (targetParty.get() != optional.get()) {
            partyService.sendMessage(player, kickedPlayer.get().getUsername() + " non è nel tuo party.");
            return;
        }

        partyService.leave(optional.get(), kickedPlayer.get());

        partyService.broadcastMessage(targetParty.get(), Component.text(player.getUsername()).color(NamedTextColor.AQUA)
                .append(Component.text(" ha espulso ").color(NamedTextColor.GRAY)
                        .append(Component.text(kickedPlayer.get().getUsername()).color(NamedTextColor.AQUA))));
        partyService.sendMessage(kickedPlayer.get(), Component.text("Sei stato espulso dal party.").color(NamedTextColor.GRAY));

    }
}
