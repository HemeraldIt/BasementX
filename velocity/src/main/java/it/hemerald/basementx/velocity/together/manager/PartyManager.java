package it.hemerald.basementx.velocity.together.manager;

import com.google.common.collect.Sets;
import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.velocity.together.commands.CommandArgument;
import it.hemerald.basementx.api.party.Party;
import it.hemerald.basementx.common.party.BasementParty;
import it.hemerald.basementx.velocity.together.Together;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class PartyManager {

    @Getter private final Together together;
    private final RLocalCachedMap<String, Party> parties;
    private final Set<String> partyChats = Sets.newConcurrentHashSet();

    public PartyManager(Together together) {
        this.together = together;
        this.parties = together.getBasement().getRedisManager().getRedissonClient().getLocalCachedMap("party", LocalCachedMapOptions.defaults());
    }

    public void disable() {
        parties.clear();
        partyChats.clear();
    }

    public Optional<Party> getParty(String player) {
        return Optional.ofNullable(parties.get(player));
    }

    public Optional<Party> getParty(Player player) {
        return getParty(player.getUsername());
    }

    public void saveParty(Party party) {
        party.getMembers().forEach(member -> parties.fastPut(member, party));
    }

    public Party createParty(Player leader) {
        Party party = new BasementParty(leader.getUsername());
        parties.fastPut(leader.getUsername(), party);
        return party;
    }

    public void leave(Player player) {
        partyChats.remove(player.getUsername());

        getParty(player).ifPresent(party -> {
            if(party.getLeader().equalsIgnoreCase(player.getUsername()) || party.getMembers().size() == 1) {
                disband(party);
                return;
            }

            leave(party, player);
            broadcastMessage(party, Component.text(player.getUsername()).color(NamedTextColor.AQUA).append(Component.text(" è uscito dal party").color(NamedTextColor.GRAY)));
        });
    }

    public void leave(Party party, Player player) {
        party.getMembers().remove(player.getUsername());
        deleteParty(player.getUsername());
        saveParty(party);
    }

    public void disband(Party party) {
        broadcastMessage(party, Component.text("Il tuo party è stato disbandato").color(NamedTextColor.RED));
        partyChats.remove(party.getLeader());
        party.getMembers().forEach(partyChats::remove);
        together.getInvitationService().disband(party);
        party.getMembers().forEach(parties::fastRemove);
    }

    public void toggleChat(Player player) {
        if(partyChats.contains(player.getUsername())) {
            partyChats.remove(player.getUsername());
            player.sendMessage(CommandArgument.PREFIX.append(Component.text("Ora scriverai in chat pubblica").color(NamedTextColor.GREEN)));
            return;
        }

        partyChats.add(player.getUsername());
        player.sendMessage(CommandArgument.PREFIX.append(Component.text("Ora scriverai nella chat del party").color(NamedTextColor.AQUA)));
    }

    public boolean isChat(Player player) {
        return partyChats.contains(player.getUsername());
    }

    public void broadcastMessage(Party party, Component component) {
        party.getMembers().forEach(name -> consume(name, player -> player.sendMessage(component)));
    }

    private void consume(String name, Consumer<Player> consumer) {
        if(name != null) together.getServer().getPlayer(name).ifPresent(consumer);
    }

    public void deleteParty(String playerName) {
        parties.fastRemove(playerName);
    }
}
