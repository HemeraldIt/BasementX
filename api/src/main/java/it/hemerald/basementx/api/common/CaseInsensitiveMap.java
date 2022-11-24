package it.hemerald.basementx.api.common;

import java.util.HashMap;

public class CaseInsensitiveMap<V> extends HashMap<String, V> {

    @Override
    public V put(String key, V value) {
        return super.put(key.toLowerCase(), value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return super.remove(key.toString().toLowerCase(), value);
    }

    @Override
    public V remove(Object key) {
        return super.remove(key.toString().toLowerCase());
    }

    @Override
    public V get(Object key) {
        return super.get(key.toString().toLowerCase());
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return super.getOrDefault(key.toString().toLowerCase(), defaultValue);
    }
}
