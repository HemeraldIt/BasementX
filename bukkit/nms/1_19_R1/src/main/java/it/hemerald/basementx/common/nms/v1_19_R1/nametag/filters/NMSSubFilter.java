package it.hemerald.basementx.common.nms.v1_19_R1.nametag.filters;

import it.hemerald.basementx.api.bukkit.nametag.filters.SubFilter;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class NMSSubFilter extends SubFilter {

    public NMSSubFilter() {
        super();
    }

    @Override
    public void apply(Player player, boolean ignoreMe) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        PacketPlayOutPlayerInfo packet = makePacket(craftPlayer);
        List<String> permissions = getPermissionOfPlayer(player);

        for (String permission : permissions) {
            for (Player sub : getPlayers(permission)) {
                CraftPlayer craftSub = (CraftPlayer) sub;
                craftPlayer.getHandle().b.a(makePacket(craftSub));
                if(!ignoreMe) craftSub.getHandle().b.a(packet);
            }
        }
    }

    private PacketPlayOutPlayerInfo makePacket(CraftPlayer player) {
        EntityPlayer entityPlayer = player.getHandle();
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.d);
        ProfilePublicKey profilePublicKey = entityPlayer.fz();
        ProfilePublicKey.a data = profilePublicKey != null ? profilePublicKey.b() : null;
        packet.b().add(new PacketPlayOutPlayerInfo.PlayerInfoData(entityPlayer.fy(), entityPlayer.e, entityPlayer.d.b(),
                IChatBaseComponent.a(prefix + player.getPlayerListName()), data));
        return packet;
    }
}
