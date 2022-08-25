package it.hemerald.basementx.api.redis.messages.implementation;

import it.hemerald.basementx.api.redis.messages.BasementMessage;
import lombok.Getter;

@Getter
public class SpectateMessage extends BasementMessage {

    public static final String TOPIC = "spectate";

    private final String player;
    private final String target;
    private final String fromServer;
    private final String targetServer;

    public SpectateMessage() {
        super(TOPIC);
        this.player = null;
        this.target = null;
        this.fromServer = null;
        this.targetServer = null;
    }

    public SpectateMessage(String player, String target, String fromServer, String targetServer) {
        super(TOPIC);
        this.player = player;
        this.target = target;
        this.fromServer = fromServer;
        this.targetServer = targetServer;
    }
}
