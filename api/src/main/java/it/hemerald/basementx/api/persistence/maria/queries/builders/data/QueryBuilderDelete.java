package it.hemerald.basementx.api.persistence.maria.queries.builders.data;

import it.hemerald.basementx.api.persistence.generic.queries.effective.ReturningQuery;
import it.hemerald.basementx.api.persistence.maria.structure.data.QueryData;

/**
 * Builder representing a 'DELETE' Query
 */
public interface QueryBuilderDelete extends ReturningQuery<QueryBuilderDelete, QueryData> {

    /**
     * Specifies the table used in the query
     * @param tableName table's name
     * @return self Query Builder
     */
    QueryBuilderDelete from(String... tableName);

    /**
     * Specifies the full condition
     * applied to the query
     * @param conditions full condition
     * @return self Query Builder
     */
    QueryBuilderDelete where(String conditions);

    /**
     * Specifies the full ORDER BY
     * block statement
     * @param statement group by expression
     * @return self Query Builder
     */
    QueryBuilderDelete orderBy(String statement);

    /**
     * Specifies the LIMIT block
     * applied to the query
     * @param limit limit
     * @return self Query Builder
     */
    QueryBuilderDelete limit(int limit);

    QueryBuilderDelete returning(String expression);

    QueryBuilderDelete multi(String... selector);

}
