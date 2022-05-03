package it.mineblock.basementx.api.redis.messages.implementation;

import it.mineblock.basementx.api.redis.messages.BasementMessage;

public class VelocityNotifyMessage extends BasementMessage {

    public static final String TOPIC = "velocity-notify";

    public VelocityNotifyMessage() {
        super(TOPIC);
    }
}
