package it.hemerald.basementx.api.server;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class BukkitServer {

    private final String name;
    private int online;
    private int max;
    private boolean whitelist;
    private boolean hostable;
    private boolean hosted;
    private String hoster;
    private ServerStatus status;

    private BukkitServer() {
        this.name = null;
    }

    public BukkitServer(String name, int online, int max, boolean whitelist, boolean hostable, boolean hosted, String hoster, ServerStatus status) {
        this.name = name;
        this.online = online;
        this.max = max;
        this.whitelist = whitelist;
        this.hostable = hostable;
        this.hosted = hosted;
        this.hoster = hoster;
        this.status = status;
    }

    public boolean isServerOnline() {
        return status == ServerStatus.OPEN;
    }
}
