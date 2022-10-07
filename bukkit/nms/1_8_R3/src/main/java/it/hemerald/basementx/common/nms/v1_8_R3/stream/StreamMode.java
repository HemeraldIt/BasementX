package it.hemerald.basementx.common.nms.v1_8_R3.stream;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class StreamMode implements it.hemerald.basementx.api.bukkit.player.stream.StreamMode {

    @Override
    public void sendPackets(JavaPlugin plugin, Player who, String streamName, Player... players) {
        // Get the entity player from the base player
        EntityPlayer oldEntity = ((CraftPlayer) who).getHandle();
        EntityPlayer entityPlayer = new EntityPlayer(oldEntity.server, oldEntity.getWorld().getWorld().getHandle(),
                new GameProfile(who.getUniqueId(), streamName), new PlayerInteractManager(oldEntity.world));

        // Create the PacketPlayOutRespawn packet
        PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(
                entityPlayer.dimension,
                entityPlayer.getWorld().getDifficulty(),
                entityPlayer.getWorld().getWorldData().getType(),
                entityPlayer.playerInteractManager.getGameMode());

        PacketPlayOutEntityDestroy entitiesPacket = new PacketPlayOutEntityDestroy(entityPlayer.getId());
        PacketPlayOutPlayerInfo removePlayerPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);
        PacketPlayOutPlayerInfo addPlayerPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer);

        for (Player player : players) {
            PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
            connection.sendPacket(entitiesPacket);
            connection.sendPacket(removePlayerPacket);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player player : players) {
                PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
                connection.sendPacket(respawn);
                connection.sendPacket(addPlayerPacket);
            }
        }, 1L);
    }
}
