package it.hemerald.basementx.common.nms.v1_8_R3.team;

import it.hemerald.basementx.api.bukkit.nametag.module.NameTagModule;
import it.hemerald.basementx.api.player.BasementPlayer;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TeamUtils implements NameTagModule.TeamUtils {

    private final NameTagModule nameTagModule;
    private final String UUID;
    private final Map<String, ScoreboardTeam> fakeTeams = new HashMap<>();

    private final Scoreboard scoreboard = new Scoreboard();
    private final CraftServer craftServer = (CraftServer) Bukkit.getServer();

    @Override
    public void setColor(Team team, ChatColor color) {

    }

    @Override
    public void updateFakeTeam(Player player) {
        BasementPlayer basementPlayer = nameTagModule.getBasement().getPlayerManager().getBasementPlayer(player.getName());
        if(!basementPlayer.isInStreamMode()) {
            updateTab(player, basementPlayer, false);
            return;
        }
        ScoreboardTeam team = fakeTeams.get(player.getName());
        String newName = getFakeTeamName(basementPlayer);

        if(team == null) {
            createTeam(basementPlayer, newName);
        } else {
            if (team.getName().equals(newName)) {
                updateTeam(basementPlayer, newName);
            } else {
                createTeam(basementPlayer, newName);
            }
        }

        updateTab(player, basementPlayer, true);
    }

    private void updateTab(Player player, BasementPlayer basementPlayer, boolean streamer) {
        var packet = makePacket(player, basementPlayer);

        if(streamer) sendPacket(packet);
        else sendPacketToStreamers(packet);
    }

    private void createTeam(BasementPlayer player, String name) {
        removePlayer(player);

        ScoreboardTeam team = scoreboard.getTeam(name);
        PacketPlayOutScoreboardTeam packet;

        if(team == null) {
            team = scoreboard.createTeam(name);
            team.setAllowFriendlyFire(true);
            packet = new PacketPlayOutScoreboardTeam(team, 0);
        } else {
            packet = new PacketPlayOutScoreboardTeam(team, 3);
        }

        team.getPlayerNameSet().add(player.getStreamName());
        fakeTeams.put(player.getName(), team);

        sendPacket(packet);
    }

    public void updateTeam(BasementPlayer player, String name) {
        ScoreboardTeam team = fakeTeams.get(name);
        team.getPlayerNameSet().add(player.getStreamName());
        sendPacket(new PacketPlayOutScoreboardTeam(team, 3));
    }

    public void removePlayer(BasementPlayer player) {
        ScoreboardTeam team = fakeTeams.get(player.getName());
        if(team != null) {
            team.getPlayerNameSet().remove(player.getStreamName());
            fakeTeams.remove(player.getName());
            if(team.getPlayerNameSet().isEmpty()) {
                sendPacket(new PacketPlayOutScoreboardTeam(team, 1));
                scoreboard.removeTeam(team);
            } else {
                PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam(new ScoreboardTeam(scoreboard, team.getName()), 4);
                packet.g.add(player.getStreamName());
                sendPacket(packet);
            }
        }
    }

    private String getFakeTeamName(BasementPlayer basementPlayer) {
        return nameTagModule.resize(UUID + (char) (265) + basementPlayer.getStreamName());
    }

    private void sendPacket(Packet<?> packet) {
        Set<String> streamNames =
                nameTagModule.getBasement().getPlayerManager().getStreamers().parallelStream().map(BasementPlayer::getName).collect(Collectors.toSet());
        for (CraftPlayer onlinePlayer : craftServer.getOnlinePlayers()) {
            if(streamNames.contains(onlinePlayer.getName())) continue;
            onlinePlayer.getHandle().playerConnection.sendPacket(packet);
        }
    }

    private void sendPacketToStreamers(Packet<?> packet) {
        nameTagModule.getBasement().getPlayerManager().getStreamers().parallelStream()
                .map(basementPlayer -> (CraftPlayer)Bukkit.getPlayer(basementPlayer.getName()))
                .forEach(player -> player.getHandle().playerConnection.sendPacket(packet));
    }

    private PacketPlayOutPlayerInfo makePacket(Player player, BasementPlayer basementPlayer) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME);

        packet.b.add(packet.constructData(entityPlayer.getProfile(), entityPlayer.ping, entityPlayer.playerInteractManager.getGameMode(),
                CraftChatMessage.fromString(basementPlayer.getStreamName())[0]));
        return packet;
    }
}
