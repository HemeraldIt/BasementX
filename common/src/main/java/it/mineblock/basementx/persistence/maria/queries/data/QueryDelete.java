package it.mineblock.basementx.persistence.maria.queries.data;

import it.mineblock.basementx.api.persistence.maria.queries.builders.data.QueryBuilderDelete;
import it.mineblock.basementx.api.persistence.maria.queries.effective.MariaQuery;
import it.mineblock.basementx.api.persistence.maria.structure.AbstractMariaHolder;

import java.sql.PreparedStatement;
import java.util.concurrent.CompletableFuture;

public class QueryDelete extends MariaQuery implements QueryBuilderDelete {

    private String tableName;
    private String where;
    private String orderBy;
    private int limit;

    public QueryDelete() {}

    public QueryDelete(AbstractMariaHolder holder, String database) {
        super(holder, database);
    }

    @Override
    public QueryBuilderDelete from(String from) {
        tableName = from;
        return this;
    }

    @Override
    public QueryBuilderDelete where(String conditions) {
        where = conditions;
        return this;
    }

    @Override
    public QueryBuilderDelete orderBy(String statement) {
        orderBy = statement;
        return this;
    }

    @Override
    public QueryBuilderDelete limit(int limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public QueryBuilderDelete build() {
        StringBuilder builder = new StringBuilder("DELETE FROM")
                .append(" ").append(databaseName).append(".").append(tableName);
        if (where != null)
            builder.append(" WHERE ").append(where);
        if (orderBy != null)
            builder.append(" ORDER BY ").append(orderBy);
        if (limit != 0)
            builder.append(" LIMIT ").append(limit);
        setSql(builder.append(";").toString());
        return this;
    }

    @Override
    public PreparedStatement asPrepared() {
        return getConnector().asPrepared(getSql());
    }

    @Override
    public QueryBuilderDelete exec() {
        getConnector().execute(getSql());
        return this;
    }

    @Override
    public QueryBuilderDelete patternClone() {
        QueryDelete copy = new QueryDelete(holder, databaseName);
        copy.limit = limit;
        copy.orderBy = orderBy;
        copy.tableName = tableName;
        copy.where = where;
        return copy;
    }

    @Override
    public CompletableFuture<QueryBuilderDelete> execAsync() {
        return CompletableFuture.supplyAsync(this::exec);
    }

    @Override
    public String getSql() {
        return super.sql;
    }

}
