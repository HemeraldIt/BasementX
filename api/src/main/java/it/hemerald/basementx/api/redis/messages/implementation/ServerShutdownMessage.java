package it.hemerald.basementx.api.redis.messages.implementation;

import it.hemerald.basementx.api.redis.messages.BasementMessage;
import lombok.Getter;

@Getter
public class ServerShutdownMessage extends BasementMessage {

    public static final String TOPIC = "server-shutdown";

    private final String sender;
    private final String receiver;

    public ServerShutdownMessage() {
        super(TOPIC);
        this.sender = null;
        this.receiver = null;
    }

    public ServerShutdownMessage(String sender, String receiver) {
        super(TOPIC);
        this.sender = sender;
        this.receiver = receiver;
    }
}
