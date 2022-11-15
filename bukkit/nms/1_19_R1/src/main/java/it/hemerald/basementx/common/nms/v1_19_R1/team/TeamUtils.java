package it.hemerald.basementx.common.nms.v1_19_R1.team;

import it.hemerald.basementx.api.bukkit.BasementBukkit;
import it.hemerald.basementx.api.bukkit.nametag.module.NameTagModule;
import it.hemerald.basementx.api.player.BasementPlayer;
import lombok.RequiredArgsConstructor;
import net.minecraft.EnumChatFormat;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
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
        team.setColor(color);
    }

    @Override
    public void updateFakeTeam(Player player) {
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

        ScoreboardTeam team = scoreboard.f(name);
        PacketPlayOutScoreboardTeam packet;

        if(team == null) {
            team = scoreboard.g(name);
            team.b(IChatBaseComponent.a(ChatColor.GRAY.toString()));
            team.a(EnumChatFormat.h);
            team.a(true);
            team.g().add(player.getStreamName());
            packet = PacketPlayOutScoreboardTeam.a(team, true);
        } else {
            packet = PacketPlayOutScoreboardTeam.a(team, player.getStreamName(), PacketPlayOutScoreboardTeam.a.a);
        }

        fakeTeams.put(player.getName(), team);

        if(status) sendPacket(packet);
        else sendPacketToStreamers(packet);
    }

    public void updateTeam(BasementPlayer player, ScoreboardTeam team, boolean status) {
        var packet = PacketPlayOutScoreboardTeam.a(team, player.getStreamName(), PacketPlayOutScoreboardTeam.a.a);

        if(status) sendPacket(packet);
        else sendPacketToStreamers(packet);
    }

    public void removePlayer(BasementPlayer player, boolean status) {
        ScoreboardTeam team = fakeTeams.get(player.getName());
        if(team != null) {
            team.g().remove(player.getStreamName());
            fakeTeams.remove(player.getName());
            if(team.g().isEmpty()) {
                var packet = PacketPlayOutScoreboardTeam.a(team);
                if(status) sendPacket(packet);
                else sendPacketToStreamers(packet);
                scoreboard.d(team);
            } else {
                var packet = PacketPlayOutScoreboardTeam.a(team, player.getStreamName(), PacketPlayOutScoreboardTeam.a.b);
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
            onlinePlayer.getHandle().b.a(packet);
        }
    }

    private void sendPacketToStreamers(Packet<?> packet) {
        basement.getPlayerManager().getStreamers().parallelStream()
                .map(basementPlayer -> (CraftPlayer)Bukkit.getPlayer(basementPlayer.getName()))
                .forEach(player -> player.getHandle().b.a(packet));
    }

    private PacketPlayOutPlayerInfo makePacket(Player player, BasementPlayer basementPlayer) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.d);
        ProfilePublicKey profilePublicKey = entityPlayer.fz();
        ProfilePublicKey.a data = profilePublicKey != null ? profilePublicKey.b() : null;
        packet.b().add(new PacketPlayOutPlayerInfo.PlayerInfoData(entityPlayer.fy(), entityPlayer.e, entityPlayer.d.b(),
                IChatBaseComponent.a(basement.getNameTagModule().getAdapter().getFakePrefix(player) + basementPlayer.getStreamName()), data));
        return packet;
    }
}
