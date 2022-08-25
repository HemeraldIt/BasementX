package it.hemerald.basementx.api.persistence.maria.structure;

import it.hemerald.basementx.api.persistence.maria.queries.builders.data.QueryBuilderDelete;
import it.hemerald.basementx.api.persistence.maria.queries.builders.data.QueryBuilderInsert;
import it.hemerald.basementx.api.persistence.maria.queries.builders.data.QueryBuilderSelect;
import it.hemerald.basementx.api.persistence.maria.queries.builders.data.QueryBuilderUpdate;

/**
 * represents the interface containing the
 * main operations addressed to the tables,
 * in this case without specifying
 * the database name to which the query refers
 */
public interface AbstractQueryHolderExecutor {
    /**
     * initializes a select query from the table
     * @param database referent database for the query
     * @return query to build
     */
    QueryBuilderSelect select(String database);

    /**
     * initializes an insert query in the table
     * @param database referent database for the query
     * @return query to build
     */
    QueryBuilderInsert insert(String database);

    /**
     * initializes a remove query from the table
     * @param database referent database for the query
     * @return query to build
     */
    QueryBuilderDelete delete(String database);

    /**
     * initializes an update query on a table
     * @param database referent database for the query
     * @return query to build
     */
    QueryBuilderUpdate update(String database);
}
