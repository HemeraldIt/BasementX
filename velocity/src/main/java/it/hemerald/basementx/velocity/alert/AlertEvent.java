package it.hemerald.basementx.velocity.alert;

import com.velocitypowered.api.event.ResultedEvent;
import lombok.Getter;

@Getter
public class AlertEvent implements ResultedEvent<ResultedEvent.GenericResult> {

    private final String server;
    private final String playerName;
    private final String category;
    private final String type;
    private final String desc;
    private final int level;
    private final int maxLevel;
    private final long cps;
    private final long ping;

    private GenericResult result = GenericResult.allowed();

    public AlertEvent(String server, String playerName, String category, String type, String desc, int level, int maxLevel, long cps, long ping) {
        this.server = server;
        this.playerName = playerName;
        this.category = category;
        this.type = type;
        this.desc = desc;
        this.level = level;
        this.maxLevel = maxLevel;
        this.cps = cps;
        this.ping = ping;
    }

    @Override
    public GenericResult getResult() {
        return result;
    }

    @Override
    public void setResult(GenericResult result) {
        this.result = result;
    }

    public String toString() {
        return "AlertEvent{server=" + this.server + ", playerName=" + this.playerName + ", category=" + this.category + ", type=" + this.type + ", desc=" + this.desc +
                ", level=" + this.level + ", maxLevel=" + this.maxLevel + ", cps=" + this.cps + ", ping=" + this.ping + "}";
    }
}
