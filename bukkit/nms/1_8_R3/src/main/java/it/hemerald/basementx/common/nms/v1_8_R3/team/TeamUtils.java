package it.hemerald.basementx.common.nms.v1_8_R3.team;

import it.hemerald.basementx.api.bukkit.BasementBukkit;
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

    private final BasementBukkit basement;
    private final String UUID;
    private final Map<String, ScoreboardTeam> fakeTeams = new HashMap<>();

    private final Scoreboard scoreboard = new Scoreboard();
    private final CraftServer craftServer = (CraftServer) Bukkit.getServer();

    @Override
    public void setColor(Team team, ChatColor color) {

    }

    @Override
    public void updateFakeTeam(Player player) {
        if(!basement.getStreamMode().isEnabled()) return;
        BasementPlayer basementPlayer = basement.getPlayerManager().getBasementPlayer(player.getName());
        if(basementPlayer == null) return;
        boolean status = basementPlayer.isInStreamMode();

        ScoreboardTeam team = fakeTeams.get(player.getName());
        String newName = getFakeTeamName(basementPlayer, status);

        createTeam(basementPlayer, newName, status);
        if (team != null) {
            updateTeam(basementPlayer, team, status);
        }

        updateTab(player, basementPlayer, status);
    }

    private void updateTab(Player player, BasementPlayer basementPlayer, boolean status) {
        var packet = makePacket(player, basementPlayer);

        if(status) sendPacket(packet);
        else sendPacketToStreamers(packet);
    }

    private void createTeam(BasementPlayer player, String name, boolean status) {
        removePlayer(player, status);

        ScoreboardTeam team = scoreboard.getTeam(name);
        PacketPlayOutScoreboardTeam packet;

        if(team == null) {
            team = scoreboard.createTeam(name);
            team.setPrefix(ChatColor.GRAY.toString());
            team.setAllowFriendlyFire(true);
            team.getPlayerNameSet().add(player.getStreamName());
            packet = new PacketPlayOutScoreboardTeam(team, 0);
        } else {
            team.getPlayerNameSet().add(player.getStreamName());
            packet = new PacketPlayOutScoreboardTeam(team, 3);
        }

        fakeTeams.put(player.getName(), team);

        if(status) sendPacket(packet);
        else sendPacketToStreamers(packet);
    }

    public void updateTeam(BasementPlayer player, ScoreboardTeam team, boolean status) {
        team.getPlayerNameSet().add(player.getStreamName());
        var packet = new PacketPlayOutScoreboardTeam(team, 3);
        if(status) sendPacket(packet);
        else sendPacketToStreamers(packet);
    }

    public void removePlayer(BasementPlayer player, boolean status) {
        ScoreboardTeam team = fakeTeams.get(player.getName());
        if(team != null) {
            team.getPlayerNameSet().remove(player.getStreamName());
            fakeTeams.remove(player.getName());
            if(team.getPlayerNameSet().isEmpty()) {
                var packet = new PacketPlayOutScoreboardTeam(team, 1);
                if(status) sendPacket(packet);
                else sendPacketToStreamers(packet);
                scoreboard.removeTeam(team);
            } else {
                PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam(new ScoreboardTeam(scoreboard, team.getName()), 4);
                packet.g.add(player.getStreamName());
                if(status) sendPacket(packet);
                else sendPacketToStreamers(packet);
            }
        }
    }

    private String getFakeTeamName(BasementPlayer basementPlayer, boolean status) {
        return basement.getNameTagModule().resize(UUID + (char)(status ? 265 : 270) + basementPlayer.getStreamName());
    }

    private void sendPacket(Packet<?> packet) {
        Set<String> streamNames =
                basement.getPlayerManager().getStreamers().parallelStream().map(BasementPlayer::getName).collect(Collectors.toSet());
        for (CraftPlayer onlinePlayer : craftServer.getOnlinePlayers()) {
            if(streamNames.contains(onlinePlayer.getName())) continue;
            onlinePlayer.getHandle().playerConnection.sendPacket(packet);
        }
    }

    private void sendPacketToStreamers(Packet<?> packet) {
        basement.getPlayerManager().getStreamers().parallelStream()
                .map(basementPlayer -> (CraftPlayer)Bukkit.getPlayer(basementPlayer.getName()))
                .forEach(player -> player.getHandle().playerConnection.sendPacket(packet));
    }

    private PacketPlayOutPlayerInfo makePacket(Player player, BasementPlayer basementPlayer) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME);

        packet.b.add(packet.constructData(entityPlayer.getProfile(), entityPlayer.ping, entityPlayer.playerInteractManager.getGameMode(),
                CraftChatMessage.fromString(basement.getNameTagModule().getAdapter().getFakePrefix(player) + basementPlayer.getStreamName())[0]));
        return packet;
    }
}
