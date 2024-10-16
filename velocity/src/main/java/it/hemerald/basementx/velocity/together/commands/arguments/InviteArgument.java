package it.hemerald.basementx.velocity.together.commands.arguments;

import com.google.common.collect.ImmutableList;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.api.party.Party;
import it.hemerald.basementx.velocity.together.commands.CommandArgument;
import it.hemerald.basementx.velocity.together.invitation.Invitation;
import it.hemerald.basementx.velocity.together.manager.PartyManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InviteArgument extends CommandArgument {

    public InviteArgument(PartyManager partyService) {
        super(partyService, "invite", 2);
    }

    @Override
    public void execute(Player player, String[] args) {

        Optional<Player> invitedPlayer = partyService.getTogether().getServer().getPlayer(args[1]);
        if (invitedPlayer.isEmpty()) {
            partyService.sendMessage(player, "Giocatore non trovato.");
            return;
        }

        if (invitedPlayer.get() == player) {
            partyService.sendMessage(player, "Non puoi auto invitarti.");
            return;
        }

        if (partyService.getParty(invitedPlayer.get()).isPresent() || (invitedPlayer.get().hasPermission("basement.party.cannotinvite") && !player.hasPermission("basement.party.cannotinvite"))) {
            partyService.sendMessage(player, "Questo giocatore è già in un party.");
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

        String invitedName = invitedPlayer.get().getUsername();
        Optional<Invitation> optionalInvitation = partyService.getTogether().getInvitationService().getByInvited(invitedName, party);
        if (optionalInvitation.isPresent()) {
            partyService.getTogether().getInvitationService().endInvitation(optionalInvitation.get());
            partyService.broadcastMessage(party,
                    Component.text(player.getUsername()).color(NamedTextColor.AQUA)
                            .append(Component.text(" ha revocato l'invito di ").color(NamedTextColor.GRAY))
                            .append(Component.text(invitedPlayer.get().getUsername()).color(NamedTextColor.AQUA)));
            return;
        }

        partyService.getTogether().getInvitationService().createInvitation(party, invitedName);
        partyService.sendMessage(invitedPlayer.get(), Component.text("§b" + player.getUsername() + " §7ti ha invitato nel party §a(Clicca Qui)")
                .clickEvent(ClickEvent.runCommand("/party join " + player.getUsername())));
        partyService.broadcastMessage(party, Component.text(player.getUsername()).color(NamedTextColor.AQUA)
                .append(Component.text(" ha invitato ").color(NamedTextColor.GRAY))
                .append(Component.text(invitedPlayer.get().getUsername()).color(NamedTextColor.GREEN))
                .append(Component.text(" nel party.").color(NamedTextColor.GRAY)));
    }

    @Override
    public List<String> suggest(CommandSource source, String[] currentArgs) {

        if (currentArgs.length == 1) {

            return getPartyService().getTogether().getServer()
                    .getAllPlayers()
                    .stream()
                    .map(Player::getUsername)
                    .filter(name -> name.regionMatches(true, 0, currentArgs[0], 0, currentArgs[0].length()))
                    .collect(Collectors.toList());

        }
        return ImmutableList.of();

    }
}
