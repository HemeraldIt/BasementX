package it.mineblock.basementx.persistence.maria.structure;

import it.mineblock.basementx.api.persistence.maria.queries.builders.data.QueryBuilderDelete;
import it.mineblock.basementx.api.persistence.maria.queries.builders.data.QueryBuilderInsert;
import it.mineblock.basementx.api.persistence.maria.queries.builders.data.QueryBuilderSelect;
import it.mineblock.basementx.api.persistence.maria.queries.builders.data.QueryBuilderUpdate;
import it.mineblock.basementx.api.persistence.maria.queries.builders.database.QueryBuilderCreateDatabase;
import it.mineblock.basementx.api.persistence.maria.queries.builders.database.QueryBuilderDropDatabase;
import it.mineblock.basementx.api.persistence.maria.structure.AbstractMariaDatabase;
import it.mineblock.basementx.api.persistence.maria.structure.AbstractMariaHolder;
import it.mineblock.basementx.persistence.maria.queries.data.QueryDelete;
import it.mineblock.basementx.persistence.maria.queries.data.QueryInsert;
import it.mineblock.basementx.persistence.maria.queries.data.QuerySelect;
import it.mineblock.basementx.persistence.maria.queries.data.QueryUpdate;
import it.mineblock.basementx.persistence.maria.queries.database.QueryCreateDatabase;
import it.mineblock.basementx.persistence.maria.queries.database.QueryDropDatabase;

import java.util.HashMap;
import java.util.Map;

public class MariaHolder extends AbstractMariaHolder {

    /*
        State
     */

    private final Map<String, AbstractMariaDatabase> databases = new HashMap<>();

    /*
        Internal Use
     */

    public void loadDatabase(AbstractMariaDatabase database) {
        databases.put(database.getName(), database);
    }

    public void unloadDatabase(AbstractMariaDatabase database) {
        unloadDatabase(database.getName());
    }

    public void unloadDatabase(String databaseName) {
        databases.remove(databaseName);
    }

    /*
        Super use
     */

    @Override
    public AbstractMariaDatabase useDatabase(String databaseName) {
        return databases.get(databaseName);
    }

    @Override
    public QueryBuilderCreateDatabase createDatabase(String databaseName) {
        return new QueryCreateDatabase(this, databaseName);
    }

    @Override
    public QueryBuilderDropDatabase dropDatabase(String databaseName) {
        return new QueryDropDatabase(this, databaseName);
    }

    /*
        Query Executor
     */

    @Override
    public QueryBuilderSelect select(String database) {
        return new QuerySelect(this, database);
    }

    @Override
    public QueryBuilderInsert insert(String database) {
        return new QueryInsert(this, database);
    }

    @Override
    public QueryBuilderDelete delete(String database) {
        return new QueryDelete(this, database);
    }

    @Override
    public QueryBuilderUpdate update(String database) {
        return new QueryUpdate(this, database);
    }

    @Override
    public void close() {
        connector.close();
    }

}
