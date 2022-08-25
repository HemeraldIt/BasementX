package it.hemerald.basementx.api.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class RedisCredentials {

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public RedisCredentials(String host, String password) {
        this(host, 6379, "default", password);
    }
}
