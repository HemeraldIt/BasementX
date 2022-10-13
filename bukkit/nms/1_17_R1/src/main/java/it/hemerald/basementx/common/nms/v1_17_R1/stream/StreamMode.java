package it.hemerald.basementx.common.nms.v1_17_R1.stream;

import com.mojang.authlib.GameProfile;
import it.hemerald.basementx.api.bukkit.BasementBukkit;
import it.hemerald.basementx.api.player.BasementPlayer;
import it.hemerald.basementx.api.player.PlayerManager;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.network.protocol.game.PacketPlayOutRespawn;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class StreamMode extends it.hemerald.basementx.api.bukkit.player.stream.StreamMode {

    @Override
    public void sendPackets(JavaPlugin plugin, Player who, String streamName, Player... players) {
        // Get the entity player from the base player
        EntityPlayer oldEntity = ((CraftPlayer) who).getHandle();
        EntityPlayer entityPlayer = new EntityPlayer(oldEntity.c, oldEntity.getWorld().getWorld().getHandle(),
                new GameProfile(who.getUniqueId(), streamName));

        // Create the PacketPlayOutRespawn packet
        PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(
                entityPlayer.getWorld().getDimensionManager(),
                entityPlayer.getWorld().getDimensionKey(),
                entityPlayer.getWorld().getWorldData().c(),
                entityPlayer.d.getGameMode(),
                entityPlayer.d.c(),
                false,
                entityPlayer.getWorldServer().isFlatWorld(),
                true);

        PacketPlayOutEntityDestroy entitiesPacket = new PacketPlayOutEntityDestroy(entityPlayer.getId());
        PacketPlayOutPlayerInfo removePlayerPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, entityPlayer);
        PacketPlayOutPlayerInfo addPlayerPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, entityPlayer);

        for (Player player : players) {
            NetworkManager connection = ((CraftPlayer)player).getHandle().b.a;
            connection.sendPacket(entitiesPacket);
            connection.sendPacket(removePlayerPacket);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player player : players) {
                NetworkManager connection = ((CraftPlayer)player).getHandle().b.a;
                connection.sendPacket(respawn);
                connection.sendPacket(addPlayerPacket);
            }
        }, 1L);
    }

    @Override
    public void sendPackets(BasementBukkit basement, List<Player> players, Player streamer) {
        PlayerManager<BasementPlayer> playerManager = basement.getPlayerManager();
        for (Player who : players) {
            EntityPlayer oldEntity = ((CraftPlayer) who).getHandle();
            EntityPlayer entityPlayer = new EntityPlayer(oldEntity.c, oldEntity.getWorld().getWorld().getHandle(),
                    new GameProfile(who.getUniqueId(), playerManager.getBasementPlayer(who.getName()).getStreamName()));

            // Create the PacketPlayOutRespawn packet
            PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(
                    entityPlayer.getWorld().getDimensionManager(),
                    entityPlayer.getWorld().getDimensionKey(),
                    entityPlayer.getWorld().getWorldData().c(),
                    entityPlayer.d.getGameMode(),
                    entityPlayer.d.c(),
                    false,
                    entityPlayer.getWorldServer().isFlatWorld(),
                    true);

            PacketPlayOutEntityDestroy entitiesPacket = new PacketPlayOutEntityDestroy(entityPlayer.getId());
            PacketPlayOutPlayerInfo removePlayerPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, entityPlayer);
            PacketPlayOutPlayerInfo addPlayerPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, entityPlayer);

            NetworkManager connection = ((CraftPlayer) streamer).getHandle().b.a;
            connection.sendPacket(entitiesPacket);
            connection.sendPacket(removePlayerPacket);

            Bukkit.getScheduler().runTaskLaterAsynchronously(basement.getPlugin(), () -> {
                connection.sendPacket(respawn);
                connection.sendPacket(addPlayerPacket);
            }, 1L);
        }
    }
}
