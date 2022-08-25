package it.hemerald.basementx.api.bukkit.disguise.adapter;

import it.hemerald.basementx.api.bukkit.adapter.Adapter;
import it.hemerald.basementx.api.bukkit.disguise.module.DisguiseModule;

public abstract class DisguiseAdapter extends Adapter<DisguiseModule> {

    public DisguiseAdapter(DisguiseModule module) {
        super(module);
    }
}
