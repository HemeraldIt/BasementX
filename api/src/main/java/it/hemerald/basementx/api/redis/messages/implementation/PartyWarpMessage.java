package it.hemerald.basementx.api.redis.messages.implementation;

import it.hemerald.basementx.api.redis.messages.BasementMessage;
import lombok.Getter;

@Getter
public class PartyWarpMessage extends BasementMessage {

    public static final String TOPIC = "party-warp";

    private final String player;
    private final String server;

    public PartyWarpMessage() {
        super(TOPIC);
        this.player = null;
        server = null;
    }

    public PartyWarpMessage(String player, String server) {
        super(TOPIC);
        this.player = player;
        this.server = server;
    }
}
