package it.hemerald.basementx.common.nms.v1_8_R3.chat;

import it.hemerald.basementx.api.bukkit.chat.Colorizer;

public class ColorizerNMS implements Colorizer.NMS {

    @Override
    public String translateHex(String msg) {
        return msg;
    }
}
