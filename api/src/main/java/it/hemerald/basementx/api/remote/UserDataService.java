package it.hemerald.basementx.api.remote;

import it.hemerald.basementx.api.player.UserData;

import java.util.UUID;

public interface UserDataService {

    UserData getUserData(UUID uuid);

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


    void addNetworkLevel(UUID uuid, int level);
    void addNetworkLevel(String username, int level);

    void addXP(UUID uuid, int xp);
    void addXP(String username, int xp);

    void addNetworkCoin(UUID uuid, int coin);
    void addNetworkCoin(String username, int coin);

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
