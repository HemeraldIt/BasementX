package it.hemerlad.basementx.api.redis.messages.implementation;

import it.hemerlad.basementx.api.redis.messages.BasementMessage;

public class VelocityNotifyMessage extends BasementMessage {

    public static final String TOPIC = "velocity-notify";

    public VelocityNotifyMessage() {
        super(TOPIC);
    }
}
