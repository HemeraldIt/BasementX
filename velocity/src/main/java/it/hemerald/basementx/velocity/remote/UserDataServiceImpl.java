package it.hemerald.basementx.velocity.remote;

import it.hemerald.basementx.api.player.UserData;
import it.hemerald.basementx.api.remote.UserDataService;
import it.hemerald.basementx.velocity.player.UserDataManager;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class UserDataServiceImpl implements UserDataService {

    private final UserDataManager userDataManager;

    @Override
    public UserData getUserData(UUID uuid) {
        return userDataManager.getUserData(uuid);
    }

    @Override
    public int getNetworkLevel(UUID uuid) {
        return userDataManager.getUserData(uuid).getNetworkLevel();
    }

    @Override
    public int getNetworkLevel(String username) {
        return userDataManager.getUserData(username).getNetworkLevel();
    }

    @Override
    public int getXP(UUID uuid) {
        return userDataManager.getUserData(uuid).getXp();
    }

    @Override
    public int getXP(String username) {
        return userDataManager.getUserData(username).getXp();
    }

    @Override
    public int getNetworkCoin(UUID uuid) {
        return userDataManager.getUserData(uuid).getNetworkCoin();
    }

    @Override
    public int getNetworkCoin(String username) {
        return userDataManager.getUserData(username).getNetworkCoin();
    }

    @Override
    public int getGems(UUID uuid) {
        return userDataManager.getUserData(uuid).getGems();
    }

    @Override
    public int getGems(String username) {
        return userDataManager.getUserData(username).getGems();
    }

    @Override
    public boolean isPremium(UUID uuid) {
        return userDataManager.getUserData(uuid).getPremium();
    }

    @Override
    public boolean isPremium(String username) {
        return userDataManager.getUserData(username).getPremium();
    }

    @Override
    public int getProtocolVersion(UUID uuid) {
        return userDataManager.getUserData(uuid).getProtocolVersion();
    }

    @Override
    public int getProtocolVersion(String username) {
        return userDataManager.getUserData(username).getProtocolVersion();
    }

    @Override
    public String getLanguage(UUID uuid) {
        return userDataManager.getUserData(uuid).getLanguage();
    }

    @Override
    public String getLanguage(String username) {
        return userDataManager.getUserData(username).getLanguage();
    }

    @Override
    public boolean isInStreamMode(UUID uuid) {
        return userDataManager.getUserData(uuid).getStreamMode();
    }

    @Override
    public boolean isInStreamMode(String username) {
        return userDataManager.getUserData(username).getStreamMode();
    }

    @Override
    public void setNetworkLevel(UUID uuid, int level) {
        userDataManager.getUserData(uuid).setNetworkLevel(level);
    }

    @Override
    public void setNetworkLevel(String username, int level) {
        userDataManager.getUserData(username).setNetworkLevel(level);
    }

    @Override
    public void setXP(UUID uuid, int xp) {
        userDataManager.getUserData(uuid).setXp(xp);
    }

    @Override
    public void setXP(String username, int xp) {
        userDataManager.getUserData(username).setXp(xp);
    }

    @Override
    public void setNetworkCoin(UUID uuid, int coin) {
        userDataManager.getUserData(uuid).setNetworkCoin(coin);
    }

    @Override
    public void setNetworkCoin(String username, int coin) {
        userDataManager.getUserData(username).setNetworkCoin(coin);
    }

    @Override
    public void setGems(UUID uuid, int gems) {
        userDataManager.getUserData(uuid).setGems(gems);
    }

    @Override
    public void setGems(String username, int gems) {
        userDataManager.getUserData(username).setGems(gems);
    }

    @Override
    public void setLanguage(UUID uuid, String lang) {
        userDataManager.getUserData(uuid).setLanguage(lang);
    }

    @Override
    public void setLanguage(String username, String lang) {
        userDataManager.getUserData(username).setLanguage(lang);
    }

    @Override
    public void setStreamMode(UUID uuid, boolean streamMode) {
        userDataManager.getUserData(uuid).setStreamMode(streamMode);
    }

    @Override
    public void setStreamMode(String username, boolean streamMode) {
        userDataManager.getUserData(username).setStreamMode(streamMode);
    }

    @Override
    public void addNetworkLevel(UUID uuid, int level) {
        UserData data = userDataManager.getUserData(uuid);
        data.setNetworkLevel(data.getNetworkLevel()+level);
    }

    @Override
    public void addNetworkLevel(String username, int level) {
        UserData data = userDataManager.getUserData(username);
        data.setNetworkLevel(data.getNetworkLevel()+level);
    }

    @Override
    public void addXP(UUID uuid, int xp) {
        UserData data = userDataManager.getUserData(uuid);
        data.setXp(data.getXp()+xp);
    }

    @Override
    public void addXP(String username, int xp) {
        UserData data = userDataManager.getUserData(username);
        data.setXp(data.getXp()+xp);
    }

    @Override
    public void addNetworkCoin(UUID uuid, int coin) {
        UserData data = userDataManager.getUserData(uuid);
        data.setNetworkCoin(data.getNetworkCoin()+coin);
    }

    @Override
    public void addNetworkCoin(String username, int coin) {
        UserData data = userDataManager.getUserData(username);
        data.setNetworkCoin(data.getNetworkCoin()+coin);
    }

    @Override
    public void addGems(UUID uuid, int gems) {
        UserData data = userDataManager.getUserData(uuid);
        data.setGems(data.getGems()+gems);
    }

    @Override
    public void addGems(String username, int gems) {
        UserData data = userDataManager.getUserData(username);
        data.setGems(data.getGems()+gems);
    }

    @Override
    public void removeNetworkLevel(UUID uuid, int level) {
        UserData data = userDataManager.getUserData(uuid);
        data.setNetworkLevel(Math.max(0, data.getNetworkLevel()-level));
    }

    @Override
    public void removeNetworkLevel(String username, int level) {
        UserData data = userDataManager.getUserData(username);
        data.setNetworkLevel(Math.max(0, data.getNetworkLevel()-level));
    }

    @Override
    public void removeXP(UUID uuid, int xp) {
        UserData data = userDataManager.getUserData(uuid);
        data.setXp(Math.max(0, data.getXp()-xp));
    }

    @Override
    public void removeXP(String username, int xp) {
        UserData data = userDataManager.getUserData(username);
        data.setXp(Math.max(0, data.getXp()-xp));
    }

    @Override
    public void removeNetworkCoin(UUID uuid, int coin) {
        UserData data = userDataManager.getUserData(uuid);
        data.setNetworkCoin(Math.max(0, data.getNetworkCoin()-coin));
    }

    @Override
    public void removeNetworkCoin(String username, int coin) {
        UserData data = userDataManager.getUserData(username);
        data.setNetworkCoin(Math.max(0, data.getNetworkCoin()-coin));
    }

    @Override
    public void removeGems(UUID uuid, int gems) {
        UserData data = userDataManager.getUserData(uuid);
        data.setGems(Math.max(0, data.getGems()-gems));
    }

    @Override
    public void removeGems(String username, int gems) {
        UserData data = userDataManager.getUserData(username);
        data.setGems(Math.max(0, data.getGems()-gems));
    }

}
