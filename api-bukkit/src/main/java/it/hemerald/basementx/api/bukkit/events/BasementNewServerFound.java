package it.hemerald.basementx.api.bukkit.events;

import it.hemerald.basementx.api.server.BukkitServer;
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

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
