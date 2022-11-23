package it.hemerald.basementx.velocity.remote;

import it.hemerald.basementx.api.player.UserData;
import it.hemerald.basementx.api.remote.UserDataService;
import it.hemerald.basementx.velocity.player.UserDataManager;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class UserDataServiceImpl implements UserDataService {

    private final UserDataManager userDataManager;

    @Override
    public int getNetworkLevel(UUID uuid) {
        Optional<UserData> optionalUserData = userDataManager.getUserData(uuid);
        if(optionalUserData.isEmpty()) return 0;
        return optionalUserData.get().getNetworkLevel();
    }

    @Override
    public int getNetworkLevel(String username) {
        return userDataManager.getUserData(username).getNetworkLevel();
    }

    @Override
    public int getXP(UUID uuid) {
        Optional<UserData> optionalUserData = userDataManager.getUserData(uuid);
        if(optionalUserData.isEmpty()) return 0;
        return optionalUserData.get().getXp();
    }

    @Override
    public int getXP(String username) {
        return userDataManager.getUserData(username).getXp();
    }

    @Override
    public int getNetworkCoin(UUID uuid) {
        Optional<UserData> optionalUserData = userDataManager.getUserData(uuid);
        if(optionalUserData.isEmpty()) return 0;
        return optionalUserData.get().getNetworkCoins();
    }

    @Override
    public int getNetworkCoin(String username) {
        return userDataManager.getUserData(username).getNetworkCoins();
    }

    @Override
    public int getGems(UUID uuid) {
        Optional<UserData> optionalUserData = userDataManager.getUserData(uuid);
        if(optionalUserData.isEmpty()) return 0;
        return optionalUserData.get().getGems();
    }

    @Override
    public int getGems(String username) {
        return userDataManager.getUserData(username).getGems();
    }

    @Override
    public boolean isPremium(UUID uuid) {
        Optional<UserData> optionalUserData = userDataManager.getUserData(uuid);
        if(optionalUserData.isEmpty()) return false;
        return optionalUserData.get().getPremium();
    }

    @Override
    public boolean isPremium(String username) {
        return userDataManager.getUserData(username).getPremium();
    }

    @Override
    public int getProtocolVersion(UUID uuid) {
        Optional<UserData> optionalUserData = userDataManager.getUserData(uuid);
        if(optionalUserData.isEmpty()) return 0;
        return optionalUserData.get().getProtocolVersion();
    }

    @Override
    public int getProtocolVersion(String username) {
        return userDataManager.getUserData(username).getProtocolVersion();
    }

    @Override
    public String getLanguage(UUID uuid) {
        Optional<UserData> optionalUserData = userDataManager.getUserData(uuid);
        if(optionalUserData.isEmpty()) return "it-it";
        return optionalUserData.get().getLanguage();
    }

    @Override
    public String getLanguage(String username) {
        return userDataManager.getUserData(username).getLanguage();
    }

    @Override
    public boolean isInStreamMode(UUID uuid) {
        Optional<UserData> optionalUserData = userDataManager.getUserData(uuid);
        if(optionalUserData.isEmpty()) return false;
        return optionalUserData.get().getStreamMode();
    }

    @Override
    public boolean isInStreamMode(String username) {
        return userDataManager.getUserData(username).getStreamMode();
    }

    @Override
    public void setNetworkLevel(UUID uuid, int level) {
        userDataManager.getUserData(uuid).ifPresent(userData -> userData.setNetworkLevel(level));
    }

    @Override
    public void setNetworkLevel(String username, int level) {
        userDataManager.getUserData(username).setNetworkLevel(level);
    }

    @Override
    public void setXP(UUID uuid, int xp) {
        userDataManager.getUserData(uuid).ifPresent(userData -> {
            userData.setXp(xp);
            userData.tryLevelUp();
        });
    }

    @Override
    public void setXP(String username, int xp) {
        UserData userData = userDataManager.getUserData(username);
        userData.setXp(xp);
        userData.tryLevelUp();
    }

    @Override
    public void setNetworkCoin(UUID uuid, int coin) {
        userDataManager.getUserData(uuid).ifPresent(userData -> userData.setNetworkCoins(coin));
    }

    @Override
    public void setNetworkCoin(String username, int coin) {
        userDataManager.getUserData(username).setNetworkCoins(coin);
    }

    @Override
    public void setGems(UUID uuid, int gems) {
        userDataManager.getUserData(uuid).ifPresent(userData -> userData.setGems(gems));
    }

    @Override
    public void setGems(String username, int gems) {
        userDataManager.getUserData(username).setGems(gems);
    }

    @Override
    public void setLanguage(UUID uuid, String lang) {
        userDataManager.getUserData(uuid).ifPresent(userData -> userData.setLanguage(lang));
    }

    @Override
    public void setLanguage(String username, String lang) {
        userDataManager.getUserData(username).setLanguage(lang);
    }

    @Override
    public void setStreamMode(UUID uuid, boolean streamMode) {
        userDataManager.getUserData(uuid).ifPresent(userData -> userData.setStreamMode(streamMode));
    }

    @Override
    public void setStreamMode(String username, boolean streamMode) {
        userDataManager.getUserData(username).setStreamMode(streamMode);
    }

    @Override
    public void addNetworkLevel(UUID uuid, int level) {
        userDataManager.getUserData(uuid).ifPresent(userData -> userData.setNetworkLevel(Math.max(0, userData.getNetworkLevel()+level)));
    }

    @Override
    public void addNetworkLevel(String username, int level) {
        UserData data = userDataManager.getUserData(username);
        data.setNetworkLevel(Math.max(0, data.getNetworkLevel()+level));
    }

    @Override
    public void addXP(UUID uuid, int xp, boolean applyBoost) {
        userDataManager.getUserData(uuid).ifPresent(userData -> {
            userData.setXp(userData.getXp()+(applyBoost && userData.hasXpBoost() ? xp * userData.getXpBoost() : xp));
            userData.tryLevelUp();
        });
    }

    @Override
    public void addXP(String username, int xp, boolean applyBoost) {
        UserData data = userDataManager.getUserData(username);
        data.setXp(data.getXp()+(applyBoost && data.hasXpBoost() ? xp * data.getXpBoost() : xp));
        data.tryLevelUp();
    }

    @Override
    public void addNetworkCoin(UUID uuid, int coin, boolean applyBoost) {
        userDataManager.getUserData(uuid).ifPresent(userData ->
                userData.setNetworkCoins(userData.getNetworkCoins()+(applyBoost && userData.hasCoinsBoost() ? coin * userData.getCoinsBoost() : coin)));
    }

    @Override
    public void addNetworkCoin(String username, int coin, boolean applyBoost) {
        UserData data = userDataManager.getUserData(username);
        data.setNetworkCoins(data.getNetworkCoins()+(applyBoost && data.hasCoinsBoost() ? coin * data.getCoinsBoost() : coin));
    }

    @Override
    public void addGems(UUID uuid, int gems) {
        userDataManager.getUserData(uuid).ifPresent(userData -> userData.setGems(Math.max(0, userData.getGems()+gems)));
    }

    @Override
    public void addGems(String username, int gems) {
        UserData data = userDataManager.getUserData(username);
        data.setGems(data.getGems()+gems);
    }

    @Override
    public void removeNetworkLevel(UUID uuid, int level) {
        userDataManager.getUserData(uuid).ifPresent(userData -> userData.setNetworkLevel(Math.max(0, userData.getNetworkLevel()-level)));
    }

    @Override
    public void removeNetworkLevel(String username, int level) {
        UserData data = userDataManager.getUserData(username);
        data.setNetworkLevel(Math.max(0, data.getNetworkLevel()-level));
    }

    @Override
    public void removeXP(UUID uuid, int xp) {
        userDataManager.getUserData(uuid).ifPresent(userData -> userData.setXp(Math.max(0, userData.getXp()-xp)));
    }

    @Override
    public void removeXP(String username, int xp) {
        UserData data = userDataManager.getUserData(username);
        data.setXp(Math.max(0, data.getXp()-xp));
    }

    @Override
    public void removeNetworkCoin(UUID uuid, int coin) {
        userDataManager.getUserData(uuid).ifPresent(userData -> userData.setNetworkCoins(Math.max(0, userData.getNetworkCoins()-coin)));
    }

    @Override
    public void removeNetworkCoin(String username, int coin) {
        UserData data = userDataManager.getUserData(username);
        data.setNetworkCoins(Math.max(0, data.getNetworkCoins()-coin));
    }

    @Override
    public void removeGems(UUID uuid, int gems) {
        userDataManager.getUserData(uuid).ifPresent(userData -> userData.setGems(Math.max(0, userData.getGems()-gems)));
    }

    @Override
    public void removeGems(String username, int gems) {
        UserData data = userDataManager.getUserData(username);
        data.setGems(Math.max(0, data.getGems()-gems));
    }

}
