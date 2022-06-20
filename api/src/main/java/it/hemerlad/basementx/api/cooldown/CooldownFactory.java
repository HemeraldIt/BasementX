package it.hemerlad.basementx.api.cooldown;

public interface CooldownFactory {

    <K> Cooldown create(K key, long millis, boolean async, Runnable runnable);

    default <K> Cooldown createSync(K key, long millis, Runnable runnable) {
        return create(key, millis, false, runnable);
    }

    default <K> Cooldown createAsync(K key, long millis, Runnable runnable) {
        return create(key, millis, true, runnable);
    }

    <K> void start(K key, Cooldown cooldown);

    <K> boolean isInCooldown(K key);
}
