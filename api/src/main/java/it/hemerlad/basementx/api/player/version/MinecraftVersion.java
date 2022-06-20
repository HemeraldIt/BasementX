package it.hemerlad.basementx.api.player.version;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MinecraftVersion {

    UNKNOWN(-1),
    v1_8_X(47),
    v1_9(107),
    v1_9_1(108),
    v1_9_2(109),
    v1_9_4(110),
    v1_10_X(210),
    v1_11(315),
    v1_11_2(316),
    v1_12(335),
    v1_12_1(338),
    v1_12_2(340),
    v1_13(393),
    v1_13_1(401),
    v1_13_2(404),
    v1_14(477),
    v1_14_1(480),
    v1_14_2(485),
    v1_14_3(490),
    v1_14_4(498),
    v1_15(573),
    v1_15_1(575),
    v1_15_2(578),
    v1_16(735),
    v1_16_1(736),
    v1_16_2(751),
    v1_16_3(753),
    v1_16_5(754),
    v1_17(755),
    v1_17_1(756),
    v1_18_1(757),
    v1_18_2(758);

    private static final Int2ObjectMap<MinecraftVersion> mapping = Int2ObjectMaps.emptyMap();

    static {
        for (MinecraftVersion minecraftVersion : values()) {
            mapping.put(minecraftVersion.getProtocolVersion(), minecraftVersion);
        }
    }

    private final int protocolVersion;

    public static MinecraftVersion byProtocolVersion(int protocolVersion) {
        return mapping.getOrDefault(protocolVersion, UNKNOWN);
    }

    public static boolean isAfter1_13(MinecraftVersion minecraftVersion) {
        return minecraftVersion.getProtocolVersion() > v1_13.getProtocolVersion();
    }

    public static boolean isAfter1_16(MinecraftVersion minecraftVersion) {
        return minecraftVersion.getProtocolVersion() > v1_16.getProtocolVersion();
    }
}
