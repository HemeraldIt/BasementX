package it.hemerald.basementx.api.redis.messages.implementation;

import it.hemerald.basementx.api.redis.messages.BasementMessage;
import lombok.Getter;

@Getter
public class TpToMessage extends BasementMessage {

    public static final String TOPIC = "tp-to";

    private final String player;
    private final String target;

    public TpToMessage() {
        super(TOPIC);
        this.player = null;
        this.target = null;
    }

    public TpToMessage(String player, String target) {
        super(TOPIC);
        this.player = player;
        this.target = target;
    }
}
