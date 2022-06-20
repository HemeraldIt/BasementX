package it.hemerald.basementx.common.persistence.maria.queries.data;

import it.hemerlad.basementx.api.persistence.maria.queries.builders.data.QueryBuilderSelect;
import it.hemerlad.basementx.api.persistence.maria.queries.effective.MariaQuery;
import it.hemerlad.basementx.api.persistence.maria.structure.AbstractMariaHolder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class QuerySelect extends MariaQuery implements QueryBuilderSelect {

    public QuerySelect() {}

    public QuerySelect(AbstractMariaHolder holder, String database) {
        super(holder, database);
    }

    /*
        Building
     */

    private String columns;

    @Override
    public QueryBuilderSelect columns(String... columns) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String column : columns) {
            if (first) {
                first = false;
                builder.append(column);
                continue;
            }
            builder.append(", ").append(column);
        }
        this.columns = builder.toString();
        return this;
    }

    @Override
    public QueryBuilderSelect columns(String statement) {
        columns = statement;
        return this;
    }

    private String from;
    @Override
    public QueryBuilderSelect from(String... tables) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String table : tables) {
            if (first) {
                first = false;
                builder.append(databaseName).append(".").append(table);
                continue;
            }
            builder.append(", ").append(databaseName).append(".").append(table);
        }
        this.from = builder.toString();
        return this;
    }

    private String fromExternal;
    @Override
    public QueryBuilderSelect fromExternal(String... fullyTableName) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String table : fullyTableName) {
            if (first) {
                first = false;
                builder.append(table);
                continue;
            }
            builder.append(", ").append(table);
        }
        this.fromExternal = builder.toString();
        return this;
    }

    private String where;
    @Override
    public QueryBuilderSelect where(String conditions) {
        where = conditions;
        return this;
    }

    private String groupBy;
    @Override
    public QueryBuilderSelect groupBy(String statement) {
        groupBy = statement;
        return this;
    }

    private String having;
    @Override
    public QueryBuilderSelect having(String conditions) {
        having = conditions;
        return this;
    }

    private String orderBy;
    @Override
    public QueryBuilderSelect orderBy(String statement) {
        this.orderBy = statement;
        return this;
    }

    private String limit;
    @Override
    public QueryBuilderSelect limit(int limit, int offset) {
        this.limit = offset + ", " + limit;
        return this;
    }

    /*
        Execution
     */

    @Override
    public QueryBuilderSelect build() {
        StringBuilder builder = new StringBuilder("SELECT ");
        builder.append(columns).append(" FROM ").append(from);

        if (fromExternal != null)
            builder.append(", ").append(fromExternal);

        if (where != null)
            builder.append(" WHERE ").append(where);

        if (groupBy != null) {
            builder.append(" GROUP BY ").append(groupBy);
            if (having != null)
                builder.append(" HAVING ").append(having);
        }
        if (orderBy != null)
            builder.append(" ORDER BY ").append(orderBy);
        if (limit != null)
            builder.append(" LIMIT ").append(limit);
        setSql(builder.append(";").toString());
        return this;
    }

    @Override
    public QueryBuilderSelect exec() {
        getConnector().execute(getSql());
        return this;
    }

    @Override
    public QueryBuilderSelect patternClone() {
        QuerySelect copy = new QuerySelect(holder, databaseName);
        copy.columns = columns;
        copy.from = from;
        copy.fromExternal = fromExternal;
        copy.where = where;
        copy.groupBy = groupBy;
        copy.having = having;
        copy.orderBy = orderBy;
        copy.limit = limit;
        return copy;
    }

    @Override
    public PreparedStatement asPrepared() {
        return getConnector().asPrepared(getSql());
    }

    @Override
    public CompletableFuture<QueryBuilderSelect> execAsync() {
        return CompletableFuture.supplyAsync(this::exec);
    }

    @Override
    public ResultSet execReturn() {
        return getConnector().executeReturn(getSql());
    }

    @Override
    public CompletableFuture<ResultSet> execReturnAsync() {
        return CompletableFuture.supplyAsync(this::execReturn);
    }

    @Override
    public QueryBuilderSelect execConsume(Consumer<ResultSet> digest) {
        digest.accept(execReturn());
        return this;
    }

    @Override
    public CompletableFuture<QueryBuilderSelect> execConsumeAsync(Consumer<ResultSet> digest) {
        return CompletableFuture.supplyAsync(() -> execConsume(digest));
    }

    @Override
    public ResultSet execReturnAfter(UnaryOperator<ResultSet> action) {
        return action.apply(execReturn());
    }

    @Override
    public CompletableFuture<ResultSet> execReturnAfterAsync(UnaryOperator<ResultSet> action) {
        return CompletableFuture.supplyAsync(() -> execReturnAfter(action));
    }

    @Override
    public String getSql() {
        return super.sql;
    }

}
