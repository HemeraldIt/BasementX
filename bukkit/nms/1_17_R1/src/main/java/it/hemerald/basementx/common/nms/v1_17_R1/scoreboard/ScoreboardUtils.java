package it.hemerald.basementx.common.nms.v1_17_R1.scoreboard;

import net.minecraft.EnumChatFormat;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardObjective;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam;
import net.minecraft.server.ScoreboardServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.scores.criteria.IScoreboardCriteria;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class ScoreboardUtils implements it.hemerlad.basementx.api.bukkit.scoreboard.ScoreboardUtils {

    @Override
    public final int getCharactersLimits() {
        return 128;
    }

    @Override
    public PacketPlayOutScoreboardObjective createObjectivePacket(int mode, String name, String displayName) {
        try {
            PacketPlayOutScoreboardObjective packet = PacketPlayOutScoreboardObjective.class.newInstance();
            setFieldValue(packet, "d", name);
            setFieldValue(packet, "g", mode);

            if (mode == 0 || mode == 2) {
                setFieldValue(packet, "e", new ChatComponentText(displayName));
                setFieldValue(packet, "f", IScoreboardCriteria.EnumScoreboardHealthDisplay.a);
            }

            return packet;
        }catch (Exception ignored){
            return null;
        }
    }

    public PacketPlayOutScoreboardDisplayObjective createDisplayObjectivePacket(String name) {
        PacketPlayOutScoreboardDisplayObjective packet = new PacketPlayOutScoreboardDisplayObjective(1, null);
        setFieldValue(packet, "b", name);
        return packet;
    }

    @Override
    public PacketPlayOutScoreboardScore createScorePacket(String name, String line, int score) {
        return new PacketPlayOutScoreboardScore(ScoreboardServer.Action.a, name, line, score);
    }

    @Override
    public PacketPlayOutScoreboardTeam createTeamPacket(int mode, String name, @Nullable String prefix, @Nullable String suffix) {
        PacketPlayOutScoreboardTeam packet = null;
        try {
            packet = PacketPlayOutScoreboardTeam.class.newInstance();
            setFieldValue(packet, "h", mode);
            setFieldValue(packet, "i", name);
            PacketPlayOutScoreboardTeam.b options = PacketPlayOutScoreboardTeam.b.class.newInstance();
            setFieldValue(options, "a", new ChatComponentText(""));
            setFieldValue(options, "b", new ChatComponentText(prefix != null ? prefix : ""));
            setFieldValue(options, "c", new ChatComponentText(suffix != null ? suffix : ""));
            setFieldValue(options, "d", "always");
            setFieldValue(options, "e", "never");
            setFieldValue(options, "f", EnumChatFormat.v);
            setFieldValue(options, "g", 0);
            setFieldValue(packet, "k", options);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return packet;
    }

    @Override
    public PacketPlayOutScoreboardTeam createUpdateUserPacket(int mode, String name, String user) {
        PacketPlayOutScoreboardTeam packet = createTeamPacket(mode, name, null, null);
        setFieldValue(packet, "j", Collections.singletonList(user));
        return packet;
    }

    @Override
    public void sendPackets(Player player, Object... packets) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
        for (Object packet : packets)
            connection.sendPacket((Packet<?>) packet);
    }
}
