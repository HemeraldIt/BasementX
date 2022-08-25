package it.hemerald.basementx.api.persistence.maria.queries.builders.database;

import it.hemerald.basementx.api.persistence.generic.queries.effective.ReturningQuery;
import it.hemerald.basementx.api.persistence.maria.structure.AbstractMariaDatabase;

/**
 * Builder representing a 'CREATE DATABASE' Query
 */
public interface QueryBuilderCreateDatabase extends ReturningQuery<QueryBuilderCreateDatabase, AbstractMariaDatabase> {

    /**
     * if true it includes
     * OR REPLACE
     * block in the final query
     * @param add if the block must be present
     * @return self Query Builder
     */
    QueryBuilderCreateDatabase orReplace(boolean add);

    /**
     * if true it includes
     * IF NOT EXISTS
     * block in the final query
     * @param add if the block must be present
     * @return selfQuery Builder
     */
    QueryBuilderCreateDatabase ifNotExists(boolean add);
    
}
