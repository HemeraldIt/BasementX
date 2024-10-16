package it.hemerald.basementx.common.redis;

import ch.jalu.configme.SettingsManager;
import it.hemerald.basementx.api.redis.RedisCredentials;
import it.hemerald.basementx.api.redis.RedisManager;
import it.hemerald.basementx.api.redis.messages.BasementMessage;
import it.hemerald.basementx.api.redis.messages.handler.BasementMessageHandler;
import it.hemerald.basementx.common.config.BasementConfig;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

import java.util.HashMap;
import java.util.Map;

public class DefaultRedisManager implements RedisManager {

    private final RedissonClient redissonClient;
    private final Map<String, RTopic> topicMap;

    public DefaultRedisManager(SettingsManager settingsManager) {
        RedisCredentials credentials = new RedisCredentials(settingsManager.getProperty(BasementConfig.REDIS_HOST));

        Config config = new Config();
        config.setUseThreadClassLoader(false);
        config.setCodec(TypedJsonJacksonCodec.INSTANCE);

        SingleServerConfig singleServerConfig = config.useSingleServer().setAddress("redis://" + credentials.getHost() + ":6379");
        singleServerConfig.setClientName("BasementX");

        redissonClient = Redisson.create(config);
        topicMap = new HashMap<>();
    }

    @Override
    public RedissonClient getRedissonClient() {
        return redissonClient;
    }

    @Override
    public <T extends BasementMessage> int registerTopicListener(String name, BasementMessageHandler<T> basementMessageHandler) {
        RTopic topic = topicMap.get(name);
        if (topic == null) {
            topic = redissonClient.getTopic(name);
            topicMap.put(name, topic);
        }
        return topic.addListener(basementMessageHandler.getCommandClass(), basementMessageHandler);
    }

    public void unregisterTopicListener(String name, Integer... listenerId) {
        RTopic topic = topicMap.get(name);
        if (topic == null) {
            topic = redissonClient.getTopic(name);
            topicMap.put(name, topic);
        }
        topic.removeListener(listenerId);
    }

    public void clearTopicListeners(String name) {
        RTopic topic = topicMap.get(name);
        if (topic == null) {
            topic = redissonClient.getTopic(name);
            topicMap.put(name, topic);
        }
        topic.removeAllListeners();
    }

    public <T extends BasementMessage> long publishMessage(T message) {
        RTopic topic = topicMap.get(message.getTopic());
        if (topic == null) {
            topic = redissonClient.getTopic(message.getTopic());
            topicMap.put(message.getTopic(), topic);
        }
        return topic.publish(message);
    }
}
