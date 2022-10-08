package it.hemerald.basementx.api.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RId;
import org.redisson.api.annotation.RIndex;

@REntity
@Getter
@Setter
@RequiredArgsConstructor
public class UserData {

    public UserData() {
        uuid = null;
        username = null;
    }

    @RId
    private final String uuid;

    @RIndex
    private final String username;

    private int networkLevel = 0;
    private int xp = 0;
    private int networkCoin = 0;
    private int gems = 0;
    private boolean premium = false;
    private int protocolVersion;
    private String language = "it";
    private boolean streamMode = false;

    public boolean getPremium() {
        return premium;
    }

    public boolean getStreamMode() {
        return streamMode;
    }
}
