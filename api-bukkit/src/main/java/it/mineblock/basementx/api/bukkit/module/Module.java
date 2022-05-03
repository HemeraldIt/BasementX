package it.mineblock.basementx.api.bukkit.module;

import ch.jalu.configme.properties.Property;
import it.mineblock.basementx.api.bukkit.BasementBukkit;
import it.mineblock.basementx.api.bukkit.adapter.Adapter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class Module<T extends Adapter<?>> {

    protected final BasementBukkit basement;
    @Getter(AccessLevel.PROTECTED) private final Property<Boolean> property;
    protected boolean enabled;
    protected T adapter = getDefaultAdapter();

    protected abstract T getDefaultAdapter();

    public void onStart() {}

    public void onStop() {}

    public final void enable() {
        if(!enabled && basement.getSettingsManager().getProperty(property)) {
            enabled = true;

            onStart();
        }
    }

    public final void disable() {
        if(enabled) {
            enabled = false;

            onStop();
        }
    }

    public void registerAdapter(T adapter) {
        this.adapter = adapter;
    }
}
