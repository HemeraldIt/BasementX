package it.hemerald.basementx.api.persistence.maria.queries.builders.data;

import it.hemerald.basementx.api.persistence.generic.queries.effective.ExecutiveQuery;

public interface QueryBuilderReplace extends ExecutiveQuery<QueryBuilderReplace> {

    QueryBuilderReplace into(String tableName);

    QueryBuilderReplace columnSchema(String... columns);

    QueryBuilderReplace values(Object... values);

    QueryBuilderReplace valuesNQ(Object... values);

    QueryBuilderReplace clearValues();

}
