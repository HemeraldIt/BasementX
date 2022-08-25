package it.hemerald.basementx.api.bukkit.adapter;

import it.hemerald.basementx.api.bukkit.module.Module;

public abstract class Adapter<T extends Module<?>> {

    protected T module;

    public Adapter(T module) {
        this.module = module;
    }
}
