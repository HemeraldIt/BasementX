package it.hemerlad.basementx.api.persistence.maria.queries.builders.database;

import it.hemerlad.basementx.api.persistence.generic.queries.effective.ExecutiveQuery;

/**
 * Builder representing a 'DROP DATABASE' Query
 */
public interface QueryBuilderDropDatabase extends ExecutiveQuery<QueryBuilderDropDatabase> {

    /**
     * if true it includes
     * IF EXISTS
     * block in the final query
     * @param add if the block must be present
     * @return self Query Builder
     */
    QueryBuilderDropDatabase ifExists(boolean add);

}
