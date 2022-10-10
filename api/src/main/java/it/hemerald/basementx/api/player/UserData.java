package it.hemerald.basementx.api.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RId;
import org.redisson.api.annotation.RIndex;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

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

    @RIndex
    private int tableIndex = -1;

    private int networkLevel = 0;
    private int xp = 0;
    private int networkCoins = 0;
    private int gems = 0;
    private boolean premium = false;
    private int protocolVersion;
    private String language = "it";
    private boolean streamMode = false;
    private int xpBoost = 1;
    private long xpBoostTime = 0; // Instant.plus(now, duration) then specify the end time of boost
    private int coinsBoost = 1;
    private long coinsBoostTime = 0; // Instant.plus(now, duration) then specify the end time of boost

    public boolean getPremium() {
        return premium;
    }

    public boolean getStreamMode() {
        return streamMode;
    }

    public void xpBoostTime(int duration) {
        setXpBoostTime(Instant.now().plus(duration, ChronoUnit.SECONDS).toEpochMilli());
    }

    public void xpCoinsTime(int duration) {
        setCoinsBoostTime(Instant.now().plus(duration, ChronoUnit.SECONDS).toEpochMilli());
    }

    public boolean hasXpBoost() {
        if (System.currentTimeMillis() < getXpBoostTime())
            return true;
        if (getXpBoost() != 1)
            setXpBoost(1);
        return false;
    }

    public boolean hasCoinsBoost() {
        if (System.currentTimeMillis() < getCoinsBoostTime())
            return true;
        if (getCoinsBoost() != 1)
            setCoinsBoost(1);
        return false;
    }
}
