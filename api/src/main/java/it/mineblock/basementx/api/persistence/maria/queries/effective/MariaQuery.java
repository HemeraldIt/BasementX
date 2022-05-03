package it.mineblock.basementx.api.persistence.maria.queries.effective;

import it.mineblock.basementx.api.persistence.generic.connection.Connector;
import it.mineblock.basementx.api.persistence.maria.structure.AbstractMariaHolder;
import lombok.Getter;
import lombok.Setter;

/**
 * represents the form of the query ready to run
 */
@Setter
public abstract class MariaQuery {

    /**
     * main Holder, provider of the future connector.
     */
    @Getter
    protected AbstractMariaHolder holder;

    protected Connector getConnector() {
        return holder.getConnector();
    }

    /**
     * Name of the present of future database
     */
    @Getter
    protected String databaseName;

    public MariaQuery() {}

    public MariaQuery(AbstractMariaHolder holder, String databaseName) {
        this.holder = holder;
        this.databaseName = databaseName;
    }

    /**
     * The actual query
     */
    protected String sql;

}
