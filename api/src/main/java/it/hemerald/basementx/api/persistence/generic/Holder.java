package it.hemerald.basementx.api.persistence.generic;

import it.hemerald.basementx.api.persistence.generic.connection.Connector;
import lombok.Getter;
import lombok.Setter;

public abstract class Holder {
    @Getter
    @Setter
    protected Connector connector;

    public abstract void close();
}
