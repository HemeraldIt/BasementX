package it.mineblock.basementx.plugin;

import ch.jalu.configme.SettingsHolder;
import it.mineblock.basementx.api.Basement;
import it.mineblock.basementx.api.BasementProvider;
import it.mineblock.basementx.api.plugin.BasementPlugin;
import lombok.Getter;

@Getter
public abstract class AbstractBasementPlugin implements BasementPlugin {

    protected StandardBasement basement;

    public void init() {
        basement = new StandardBasement(this);
    }

    public void init(Class<? extends SettingsHolder> settingsHolder) {
        basement = new StandardBasement(this, settingsHolder);
    }

    public void enable() {
        enable(null);
    }

    public void enable(Class<? extends SettingsHolder> settingsHolder) {
        init(settingsHolder);
        basement.start();

        BasementProvider.register(basement);

        registerApiOnPlatform(basement);
        registerLocales();
        registerCommands();
        registerListeners();
    }

    public void disable() {
        BasementProvider.unregister();
        basement.stop();
    }

    protected abstract void registerApiOnPlatform(Basement basement);

    protected abstract void registerCommands();

    protected abstract void registerListeners();

    protected abstract void registerLocales();
}
