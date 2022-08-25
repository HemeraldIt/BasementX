package it.hemerald.basementx.common.redis;

import ch.jalu.configme.SettingsManager;
import it.hemerald.basementx.common.config.BasementConfig;
import it.hemerald.basementx.api.redis.RedisCredentials;
import it.hemerald.basementx.api.redis.RedisManager;
import it.hemerald.basementx.api.redis.messages.BasementMessage;
import it.hemerald.basementx.api.redis.messages.handler.BasementMessageHandler;
import org.redisson.Redisson;
import org.redisson.api.RReliableTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

import java.util.HashMap;
import java.util.Map;

public class DefaultRedisManager implements RedisManager {

    private final RedissonClient redissonClient;
    private final Map<String, RReliableTopic> reliableTopicMap;

    public DefaultRedisManager(SettingsManager settingsManager) {
        RedisCredentials credentials = new RedisCredentials(
                settingsManager.getProperty(BasementConfig.REDIS_HOST),
                settingsManager.getProperty(BasementConfig.REDIS_PORT),
                settingsManager.getProperty(BasementConfig.REDIS_USERNAME),
                settingsManager.getProperty(BasementConfig.REDIS_PASSWORD));
        Config config = new Config();
        config.setUseThreadClassLoader(false);
        config.setCodec(TypedJsonJacksonCodec.INSTANCE);
        config.setNettyThreads(64);
        config.setThreads(24);
        SingleServerConfig singleConfig = config.useSingleServer().setAddress("redis://" + credentials.getHost() + ":" + credentials.getPort());
        if (!credentials.getUsername().isEmpty())
            singleConfig.setUsername(credentials.getUsername());
        if (!credentials.getPassword().isEmpty())
            singleConfig.setPassword(credentials.getPassword());
        redissonClient = Redisson.create(config);
        reliableTopicMap = new HashMap<>();
    }

    @Override
    public RedissonClient getRedissonClient() {
        return redissonClient;
    }

    @Override
    public <T extends BasementMessage> String registerTopicListener(String name, BasementMessageHandler<T> basementMessageHandler) {
        RReliableTopic topic = reliableTopicMap.get(name);
        if(topic == null) {
            topic = redissonClient.getReliableTopic(name);
            reliableTopicMap.put(name, topic);
        }
        return topic.addListener(basementMessageHandler.getCommandClass(), basementMessageHandler);
    }

    public void unregisterTopicListener(String name, String... listenerId) {
        RReliableTopic topic = reliableTopicMap.get(name);
        if(topic == null) {
            topic = redissonClient.getReliableTopic(name);
            reliableTopicMap.put(name, topic);
        }
        topic.removeListener(listenerId);
    }

    public void clearTopicListeners(String name) {
        RReliableTopic topic = reliableTopicMap.get(name);
        if(topic == null) {
            topic = redissonClient.getReliableTopic(name);
            reliableTopicMap.put(name, topic);
        }
        topic.removeAllListeners();
    }

    public <T extends BasementMessage> long publishMessage(T message) {
        RReliableTopic topic = reliableTopicMap.get(message.getTopic());
        if(topic == null) {
            topic = redissonClient.getReliableTopic(message.getTopic());
            reliableTopicMap.put(message.getTopic(), topic);
        }
        return topic.publish(message);
    }
}
