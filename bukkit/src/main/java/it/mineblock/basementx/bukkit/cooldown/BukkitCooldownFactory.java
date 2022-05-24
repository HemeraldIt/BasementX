package it.mineblock.basementx.bukkit.cooldown;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import it.mineblock.basementx.api.cooldown.Cooldown;
import it.mineblock.basementx.api.cooldown.CooldownFactory;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitCooldownFactory implements CooldownFactory {

    private final Table<Object, Boolean, Cooldown> cooldownTable = HashBasedTable.create();

    public BukkitCooldownFactory(JavaPlugin plugin) {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            cooldownTable.column(true).values().removeIf(cooldown -> {
                if (cooldown.isExpired()) {
                    cooldown.getRunnable().run();
                    return true;
                }
                return false;
            });
        }, 1, 1);

        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            cooldownTable.column(false).values().removeIf(cooldown -> {
                if (cooldown.isExpired()) {
                    cooldown.getRunnable().run();
                    return true;
                }
                return false;
            });
        }, 1, 1);
    }

    @Override
    public <K> Cooldown create(K key, long millis, boolean async, Runnable runnable) {
        Cooldown cooldown = new Cooldown(millis, async, runnable);
        cooldownTable.put(key, async, cooldown);
        return cooldown;
    }

    @Override
    public <K> void start(K key, Cooldown cooldown) {
        cooldownTable.put(key, cooldown.isAsync(), cooldown);
    }

    @Override
    public <K> boolean isInCooldown(K key) {
        return cooldownTable.containsRow(key);
    }
}
