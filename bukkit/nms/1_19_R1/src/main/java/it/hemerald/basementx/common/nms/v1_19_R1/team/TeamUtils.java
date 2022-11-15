package it.hemerald.basementx.common.nms.v1_19_R1.team;

import it.hemerald.basementx.api.bukkit.nametag.module.NameTagModule;
import it.hemerald.basementx.api.player.BasementPlayer;
import lombok.RequiredArgsConstructor;
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

    private final NameTagModule nameTagModule;
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
            if (team.b().equals(newName)) {
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

        ScoreboardTeam team = scoreboard.f(name);
        PacketPlayOutScoreboardTeam packet;

        if(team == null) {
            team = scoreboard.g(name);
            team.a(true);
            team.g().add(player.getStreamName());
            packet = PacketPlayOutScoreboardTeam.a(team, true);
        } else {
            packet = PacketPlayOutScoreboardTeam.a(team, player.getStreamName(), PacketPlayOutScoreboardTeam.a.a);
        }

        fakeTeams.put(player.getName(), team);

        sendPacket(packet);
    }

    public void updateTeam(BasementPlayer player, String name) {
        ScoreboardTeam team = fakeTeams.get(name);
        sendPacket(PacketPlayOutScoreboardTeam.a(team, player.getStreamName(), PacketPlayOutScoreboardTeam.a.a));
    }

    public void removePlayer(BasementPlayer player) {
        ScoreboardTeam team = fakeTeams.get(player.getName());
        if(team != null) {
            team.g().remove(player.getStreamName());
            fakeTeams.remove(player.getName());
            if(team.g().isEmpty()) {
                sendPacket(PacketPlayOutScoreboardTeam.a(team));
                scoreboard.d(team);
            } else {
                sendPacket(PacketPlayOutScoreboardTeam.a(team, player.getStreamName(), PacketPlayOutScoreboardTeam.a.b));
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
            onlinePlayer.getHandle().b.a(packet);
        }
    }

    private void sendPacketToStreamers(Packet<?> packet) {
        nameTagModule.getBasement().getPlayerManager().getStreamers().parallelStream()
                .map(basementPlayer -> (CraftPlayer)Bukkit.getPlayer(basementPlayer.getName()))
                .forEach(player -> player.getHandle().b.a(packet));
    }

    private PacketPlayOutPlayerInfo makePacket(Player player, BasementPlayer basementPlayer) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.d);
        ProfilePublicKey profilePublicKey = entityPlayer.fz();
        ProfilePublicKey.a data = profilePublicKey != null ? profilePublicKey.b() : null;
        packet.b().add(new PacketPlayOutPlayerInfo.PlayerInfoData(entityPlayer.fy(), entityPlayer.e, entityPlayer.d.b(),
                IChatBaseComponent.a(basementPlayer.getStreamName()), data));
        return packet;
    }
}
