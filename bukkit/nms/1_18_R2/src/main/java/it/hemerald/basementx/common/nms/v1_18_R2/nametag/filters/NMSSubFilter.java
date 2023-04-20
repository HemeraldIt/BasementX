package it.hemerald.basementx.common.nms.v1_18_R2.nametag.filters;

import it.hemerald.basementx.api.bukkit.BasementBukkit;
import it.hemerald.basementx.api.bukkit.nametag.filters.SubFilter;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class NMSSubFilter extends SubFilter {

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
                if (!basement.getDisguiseModule().isDisguised(sub) && !ignoreMe(sub))
                    craftPlayer.getHandle().b.a(makePacket(craftSub));
                if (!ignoreMe) craftSub.getHandle().b.a(packet);
            }
        }
    }

    @Override
    public void clear(Player player, String permission) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        PacketPlayOutPlayerInfo packet = makePacketClear(craftPlayer);

        for (Player sub : getPlayers(permission)) {
            craftPlayer.getHandle().b.a(makePacketClear((CraftPlayer) sub));
            ((CraftPlayer) sub).getHandle().b.a(packet);
        }
    }

    private PacketPlayOutPlayerInfo makePacket(CraftPlayer player) {
        EntityPlayer entityPlayer = player.getHandle();
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.d);
        packet.b().add(new PacketPlayOutPlayerInfo.PlayerInfoData(entityPlayer.fq(), entityPlayer.e, entityPlayer.d.b(),
                IChatBaseComponent.a(prefix + player.getPlayerListName())));
        return packet;
    }

    private PacketPlayOutPlayerInfo makePacketClear(CraftPlayer player) {
        EntityPlayer entityPlayer = player.getHandle();
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.d);
        packet.b().add(new PacketPlayOutPlayerInfo.PlayerInfoData(entityPlayer.fq(), entityPlayer.e, entityPlayer.d.b(),
                IChatBaseComponent.a(player.getPlayerListName())));
        return packet;
    }
}
