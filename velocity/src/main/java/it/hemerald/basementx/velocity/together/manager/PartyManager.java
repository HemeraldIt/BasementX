package it.hemerald.basementx.velocity.together.manager;

import com.google.common.collect.Sets;
import com.velocitypowered.api.proxy.Player;
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

    public static final Component PREFIX = Component.text("§d§lParty §8§l› ");

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
        party.getFriends().forEach(member -> parties.fastPut(member, party));
    }

    public Party createParty(Player leader) {
        Party party = new BasementParty(leader.getUsername());
        parties.fastPut(leader.getUsername(), party);
        return party;
    }

    public void leave(Player player) {
        partyChats.remove(player.getUsername());

        getParty(player).ifPresent(party -> {
            if(party.getLeader().equalsIgnoreCase(player.getUsername()) || party.getFriends().size() == 1) {
                disband(party);
                return;
            }

            leave(party, player);
            broadcastMessage(party, Component.text(player.getUsername()).color(NamedTextColor.AQUA).append(Component.text(" è uscito dal party").color(NamedTextColor.GRAY)));
        });
    }

    public void leave(Party party, Player player) {
        party.getFriends().remove(player.getUsername());
        deleteParty(player.getUsername());
        saveParty(party);
    }

    public void disband(Party party) {
        broadcastMessage(party, Component.text("Il tuo party è stato sciolto").color(NamedTextColor.RED));
        partyChats.remove(party.getLeader());
        party.getFriends().forEach(partyChats::remove);
        together.getInvitationService().disband(party);
        party.getFriends().forEach(parties::fastRemove);
    }

    public void toggleChat(Player player) {
        if(partyChats.contains(player.getUsername())) {
            partyChats.remove(player.getUsername());
            sendMessage(player, Component.text("Ora scriverai in chat pubblica").color(NamedTextColor.GREEN));
            return;
        }

        partyChats.add(player.getUsername());
        sendMessage(player, Component.text("Ora scriverai nella chat del party").color(NamedTextColor.AQUA));
    }

    public boolean isChat(Player player) {
        return partyChats.contains(player.getUsername());
    }

    public void sendMessage(Player player, String component) {
        player.sendMessage(PREFIX.append(Component.text("§7" + component)));
    }

    public void sendMessage(Player player, Component component) {
        player.sendMessage(PREFIX.append(component));
    }

    public void broadcastMessage(Party party, Component component) {
        party.getFriends().forEach(name -> consume(name, player -> sendMessage(player, component)));
    }

    private void consume(String name, Consumer<Player> consumer) {
        if(name != null) together.getServer().getPlayer(name).ifPresent(consumer);
    }

    public void deleteParty(String playerName) {
        parties.fastRemove(playerName);
    }
}
