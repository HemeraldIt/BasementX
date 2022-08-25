package it.hemerald.basementx.common.cooldown;

import it.hemerald.basementx.api.cooldown.Cooldown;
import it.hemerald.basementx.api.cooldown.CooldownFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j(topic = "basement")
@Getter
public class DefaultCooldownFactory implements CooldownFactory {

    private final TimerTask timerTask;
    private final Map<Object, Cooldown> cooldowns = new HashMap<>();

    public DefaultCooldownFactory() {
        Timer timer = new Timer();
        timer.schedule(timerTask = new TimerTask() {
            @Override
            public void run() {
                cooldowns.values().removeIf(cooldown -> {
                    if (cooldown.isExpired()) {
                        if(!cooldown.isAsync()) {
                            log.warn("Cooldown expired, but is not permitted sync execution in this environment");
                        }
                        cooldown.getRunnable().run();
                        return true;
                    }
                    return false;
                });
            }
        }, 1, 1);
    }

    @Override
    public <K> Cooldown create(K key, long millis, boolean async, Runnable runnable) {
        Cooldown cooldown = new Cooldown(millis, async, runnable);
        cooldowns.put(key, cooldown);
        return cooldown;
    }

    @Override
    public <K> void start(K key, Cooldown cooldown) {
        cooldowns.put(key, cooldown);
    }

    @Override
    public <K> boolean isInCooldown(K key) {
        return cooldowns.containsKey(key);
    }

    @Override
    protected void finalize() throws Throwable {
        timerTask.cancel();
        super.finalize();
    }
}
