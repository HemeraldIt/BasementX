package it.hemerald.basementx.common.persistence.hikari;

import it.hemerald.basementx.api.persistence.maria.structure.AbstractMariaHolder;
import it.hemerald.basementx.common.persistence.maria.structure.MariaHolder;

import java.util.HashMap;
import java.util.Map;

public class TypeHolder {

    public static Map<Class<?>, Class<?>> TYPES = new HashMap<>();

    static {
        TYPES.put(AbstractMariaHolder.class, MariaHolder.class);
    }

}
