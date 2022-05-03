package it.mineblock.basementx.api.bukkit.disguise.adapter;

import it.mineblock.basementx.api.bukkit.adapter.Adapter;
import it.mineblock.basementx.api.bukkit.disguise.module.DisguiseModule;

public abstract class DisguiseAdapter extends Adapter<DisguiseModule> {

    public DisguiseAdapter(DisguiseModule module) {
        super(module);
    }
}
