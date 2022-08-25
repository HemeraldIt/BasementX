package it.hemerald.basementx.api.cooldown;

import lombok.Data;

@Data
public class Cooldown {

    private final long millis;
    private final long end;
    private final boolean async;
    private final Runnable runnable;

    public Cooldown(long millis, boolean async, Runnable runnable) {
        this.millis = millis;
        this.end = System.currentTimeMillis() + millis;
        this.async = async;
        this.runnable = runnable;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > end;
    }
}
