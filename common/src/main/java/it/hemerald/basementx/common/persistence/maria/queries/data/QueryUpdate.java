package it.hemerald.basementx.common.persistence.maria.queries.data;

import it.hemerlad.basementx.api.persistence.generic.queries.effective.ExecutiveQuery;
import it.hemerlad.basementx.api.persistence.generic.queries.effective.ReturningQuery;
import it.hemerlad.basementx.api.persistence.maria.queries.builders.data.QueryBuilderUpdate;
import it.hemerlad.basementx.api.persistence.maria.queries.effective.MariaQuery;
import it.hemerlad.basementx.api.persistence.maria.structure.AbstractMariaHolder;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class QueryUpdate extends MariaQuery implements QueryBuilderUpdate {

    public QueryUpdate() {}

    public QueryUpdate(AbstractMariaHolder holder, String database) {
        super(holder, database);
    }

    /*
        Building
     */

    private String tables;

    @Override
    public QueryBuilderUpdate table(String... tables) {
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
        this.tables = builder.toString();
        return this;
    }

    private final List<String> set = new ArrayList<>();
    @Override
    public QueryBuilderUpdate set(String str, Object value) {
        set.add(str + "='" + value + "'");
        return this;
    }

    @Override
    public QueryBuilderUpdate setNQ(String str, Object value) {
        set.add(str + "=" + value);
        return this;
    }

    @Override
    public QueryBuilderUpdate set(String str, ReturningQuery<? extends ExecutiveQuery<?>, ?> value) {
        set.add(str + "=(" + value.build().getSql().replace(";", "") + ")");
        return this;
    }

    @Override
    public QueryBuilderUpdate add(String str, Number value) {
        set.add(str + "=" + str + "+" + value);
        return this;
    }

    @Override
    public QueryBuilderUpdate subtract(String str, Number value) {
        set.add(str + "=" + str + "-" + value);
        return this;
    }

    private String where;
    @Override
    public QueryBuilderUpdate where(String conditions) {
        where = conditions;
        return this;
    }

    private String orderBy;
    @Override
    public QueryBuilderUpdate orderBy(String statement) {
        orderBy = statement;
        return this;
    }

    private int limit;
    @Override
    public QueryBuilderUpdate limit(int n) {
        this.limit = n;
        return this;
    }

    /*
        Execution
     */

    @Override
    public QueryBuilderUpdate build() {
        StringBuilder builder = new StringBuilder("UPDATE ").append(tables);
        builder.append(" SET ");
        boolean first = true;
        for (String s : set) {
            if (first) {
                first = false;
                builder.append(s);
                continue;
            }
            builder.append(", ").append(s);
        }
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
    public QueryBuilderUpdate exec() {
        getConnector().execute(getSql());
        return this;
    }

    @Override
    public QueryBuilderUpdate patternClone() {
        QueryUpdate copy = new QueryUpdate(holder, databaseName);
        copy.tables = tables;
        copy.set.addAll(set);
        copy.where = where;
        copy.orderBy = orderBy;
        copy.limit = limit;
        return copy;
    }

    @Override
    public CompletableFuture<QueryBuilderUpdate> execAsync() {
        return CompletableFuture.supplyAsync(this::exec);
    }

    @Override
    public String getSql() {
        return super.sql;
    }

}
