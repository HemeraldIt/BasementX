package it.mineblock.basementx.velocity.together.commands.arguments;

import com.velocitypowered.api.proxy.Player;
import it.mineblock.basementx.api.party.Party;
import it.mineblock.basementx.velocity.together.commands.CommandArgument;
import it.mineblock.basementx.velocity.together.invitation.Invitation;
import it.mineblock.basementx.velocity.together.manager.PartyManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;

public class JoinArgument extends CommandArgument {

    public JoinArgument(PartyManager partyService) {
        super(partyService, "join", 2);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (partyService.getParty(player).isPresent()) {
            sendMessage(player, "Sei già in un party.");
            return;
        }

        Optional<Player> toJoinPlayer = partyService.getTogether().getServer().getPlayer(args[1]);

        if (toJoinPlayer.isEmpty()) {
            sendMessage(player, "Giocatore non trovato.");
            return;
        }

        if (toJoinPlayer.get() == player) {
            sendMessage(player, "Non puoi entrare nel tuo stesso party.");
            return;
        }

        Optional<Party> party = partyService.getParty(toJoinPlayer.get());
        if (party.isEmpty()) {
            sendMessage(player, "Questo giocatore è già in un party.");
            return;
        }
        Party toJoin = party.get();
        Optional<Invitation> optionalInvitation = partyService.getTogether().getInvitationService().getByInvited(player.getUsername(), toJoin);

        if (!toJoin.isOpen() && optionalInvitation.isEmpty()) {
            sendMessage(player, "Non sei stato invitato in quel party.");
            return;
        }

        if (toJoin.isFull()) {
            sendMessage(player, "Il party è pieno.");
            return;
        }


        if (optionalInvitation.isPresent() && !partyService.getTogether().getInvitationService().acceptInvitation(optionalInvitation.get())) {
            sendMessage(player, "Si è verificato un errore nell'accesso al party, prova a farti reinvitare!");
            return;
        }

        String name = player.getUsername();
        if (name == null) return;

        partyService.broadcastMessage(toJoin, Component.text(player.getUsername()).color(NamedTextColor.AQUA)
                .append(Component.text(" è entrato a far parte del party.").color(NamedTextColor.GRAY)));
        toJoin.getMembers().add(name);
        partyService.saveParty(toJoin);
        sendMessage(player, Component.text("Sei entrato nel party di ").append(Component.text(toJoin.getLeader()).color(NamedTextColor.AQUA)));
    }
}
