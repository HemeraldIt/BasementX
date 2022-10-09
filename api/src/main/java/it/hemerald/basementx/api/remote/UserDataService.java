package it.hemerald.basementx.api.remote;

import java.util.UUID;

public interface UserDataService {

    int getNetworkLevel(UUID uuid);
    int getNetworkLevel(String username);

    int getXP(UUID uuid);
    int getXP(String username);

    int getNetworkCoin(UUID uuid);
    int getNetworkCoin(String username);

    int getGems(UUID uuid);
    int getGems(String username);

    boolean isPremium(UUID uuid);
    boolean isPremium(String username);

    int getProtocolVersion(UUID uuid);
    int getProtocolVersion(String username);

    String getLanguage(UUID uuid);
    String getLanguage(String username);

    boolean isInStreamMode(UUID uuid);
    boolean isInStreamMode(String username);


    void setNetworkLevel(UUID uuid, int level);
    void setNetworkLevel(String username, int level);

    void setXP(UUID uuid, int xp);
    void setXP(String username, int xp);

    void setNetworkCoin(UUID uuid, int coin);
    void setNetworkCoin(String username, int coin);

    void setGems(UUID uuid, int gems);
    void setGems(String username, int gems);

    void setLanguage(UUID uuid, String lang);
    void setLanguage(String username, String lang);

    void setStreamMode(UUID uuid, boolean streamMode);
    void setStreamMode(String username, boolean streamMode);


    void addNetworkLevel(UUID uuid, int level);
    void addNetworkLevel(String username, int level);

    void addXP(UUID uuid, int xp, boolean applyBoost);
    void addXP(String username, int xp, boolean applyBoost);

    default void addXP(UUID uuid, int xp) {
        addXP(uuid, xp, true);
    }
    default void addXP(String username, int xp) {
        addXP(username, xp, true);
    }

    void addNetworkCoin(UUID uuid, int coin, boolean applyBoost);
    void addNetworkCoin(String username, int coin, boolean applyBoost);

    default void addNetworkCoin(UUID uuid, int xp) {
        addNetworkCoin(uuid, xp, true);
    }
    default void addNetworkCoin(String username, int xp) {
        addNetworkCoin(username, xp, true);
    }

    void addGems(UUID uuid, int gems);
    void addGems(String username, int gems);


    void removeNetworkLevel(UUID uuid, int level);
    void removeNetworkLevel(String username, int level);

    void removeXP(UUID uuid, int xp);
    void removeXP(String username, int xp);

    void removeNetworkCoin(UUID uuid, int coin);
    void removeNetworkCoin(String username, int coin);

    void removeGems(UUID uuid, int gems);
    void removeGems(String username, int gems);

}
