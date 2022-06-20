package it.hemerald.basementx.common.party;

import it.hemerlad.basementx.api.party.Party;
import it.hemerlad.basementx.api.party.PartyManager;
import it.hemerlad.basementx.api.redis.RedisManager;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;

import java.util.Optional;

public class DefaultPartyManager implements PartyManager {

    private final RLocalCachedMap<String, Party> partyMap;

    public DefaultPartyManager(RedisManager redisManager) {
        this.partyMap = redisManager.getRedissonClient().getLocalCachedMap("party", LocalCachedMapOptions.defaults());
    }

    @Override
    public Optional<Party> getParty(String player) {
        return Optional.ofNullable(partyMap.get(player));
    }
}
