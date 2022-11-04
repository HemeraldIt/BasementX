package it.hemerald.basementx.common.nms.v1_8_R3.nametag.filters;

import it.hemerald.basementx.api.bukkit.BasementBukkit;
import it.hemerald.basementx.api.bukkit.nametag.filters.SubFilter;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.util.List;

public class NMSSubFilter extends SubFilter {

    public NMSSubFilter(BasementBukkit basement) {
        super(basement);
    }

    @Override
    public void apply(Player player, boolean ignoreMe) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        PacketPlayOutPlayerInfo packet = makePacket(craftPlayer);
        List<String> permissions = getPermissionOfPlayer(player);

        for (String permission : permissions) {
            for (Player sub : getPlayers(permission)) {
                CraftPlayer craftSub = (CraftPlayer) sub;
                if(!basement.getDisguiseModule().isDisguised(sub))
                    craftPlayer.getHandle().playerConnection.sendPacket(makePacket(craftSub));
                if(!ignoreMe) craftSub.getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    private PacketPlayOutPlayerInfo makePacket(CraftPlayer player) {
        EntityPlayer entityPlayer = player.getHandle();
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME);

        packet.b.add(packet.constructData(entityPlayer.getProfile(), entityPlayer.ping, entityPlayer.playerInteractManager.getGameMode(),
                CraftChatMessage.fromString(prefix + player.getPlayerListName())[0]));
        return packet;
    }
}
