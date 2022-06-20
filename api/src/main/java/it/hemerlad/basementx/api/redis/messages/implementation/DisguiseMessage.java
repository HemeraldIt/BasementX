package it.hemerlad.basementx.api.redis.messages.implementation;

import it.hemerlad.basementx.api.player.disguise.DisguiseAction;
import it.hemerlad.basementx.api.redis.messages.BasementMessage;
import lombok.Getter;

@Getter
public class DisguiseMessage extends BasementMessage {

    public static final String TOPIC = "disguise";

    private final String player;
    private final DisguiseAction action;

    public DisguiseMessage() {
        super(TOPIC);
        player = null;
        action = null;
    }

    public DisguiseMessage(String player, DisguiseAction action) {
        super(TOPIC);
        this.player = player;
        this.action = action;
    }
}
