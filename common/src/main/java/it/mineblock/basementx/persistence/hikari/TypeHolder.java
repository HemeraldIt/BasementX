package it.mineblock.basementx.persistence.hikari;

import it.mineblock.basementx.api.persistence.maria.structure.AbstractMariaHolder;
import it.mineblock.basementx.persistence.maria.structure.MariaHolder;

import java.util.HashMap;
import java.util.Map;

public class TypeHolder {

    public static Map<Class<?>, Class<?>> TYPES = new HashMap<>();

    static {
        TYPES.put(AbstractMariaHolder.class, MariaHolder.class);
    }

}
