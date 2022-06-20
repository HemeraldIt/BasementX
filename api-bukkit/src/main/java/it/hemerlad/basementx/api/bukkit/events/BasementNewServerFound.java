package it.hemerlad.basementx.api.bukkit.events;

import it.hemerlad.basementx.api.server.BukkitServer;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BasementNewServerFound extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    @Getter
    private final BukkitServer server;

    public BasementNewServerFound(BukkitServer server) {
        super(true);
        this.server = server;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
