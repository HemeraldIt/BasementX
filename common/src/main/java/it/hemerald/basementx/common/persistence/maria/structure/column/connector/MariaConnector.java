package it.hemerald.basementx.common.persistence.maria.structure.column.connector;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import it.hemerald.basementx.api.persistence.maria.structure.data.QueryData;
import it.hemerald.basementx.common.persistence.hikari.HikariConnector;
import it.hemerald.basementx.common.persistence.hikari.property.HikariProperty;
import it.hemerald.basementx.common.persistence.hikari.property.PropertiesProvider;
import it.hemerald.basementx.common.persistence.hikari.property.PropertyPair;
import it.hemerald.basementx.common.persistence.maria.structure.data.QueryDataImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class MariaConnector extends HikariConnector {

    public MariaConnector() {
        super("org.mariadb.jdbc.Driver");
    }

    protected HikariConfig getConfig() {
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(30);
        config.setMinimumIdle(10);
        return config;
    }

    @Override
    protected PropertiesProvider getProperties() {
        PropertiesProvider holder = new PropertiesProvider();
        holder.withProperties(
                new PropertyPair(HikariProperty.cachePrepStmts, "true"),
                new PropertyPair(HikariProperty.alwaysSendSetIsolation, "false"),
                new PropertyPair(HikariProperty.cacheServerConfiguration, "true"),
                new PropertyPair(HikariProperty.elideSetAutoCommits, "true"),
                new PropertyPair(HikariProperty.maintainTimeStats, "false"),
                new PropertyPair(HikariProperty.useLocalSessionState, "true"),
                new PropertyPair(HikariProperty.useServerPrepStmts, "true"),
                new PropertyPair(HikariProperty.prepStmtCacheSize, "500"),
                new PropertyPair(HikariProperty.rewriteBatchedStatements, "true"),
                new PropertyPair(HikariProperty.prepStmtCacheSqlLimit, "2048"),
                new PropertyPair(HikariProperty.cacheCallableStmts, "true"),
                new PropertyPair(HikariProperty.cacheResultSetMetadata, "true"),
                new PropertyPair(HikariProperty.characterEncoding, "utf8"),
                new PropertyPair(HikariProperty.useUnicode, "true"),
                new PropertyPair(HikariProperty.zeroDateTimeBehavior, "CONVERT_TO_NULL")
        );
        return holder;
    }

    @Override
    public void execute(String query) {
        try (Connection connection = source.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(query);
        } catch (SQLException throwable) {
            Logger.getGlobal().severe("Error on sql query:" + query);
            throwable.printStackTrace();
        }
    }

    @Override
    public PreparedStatement asPrepared(String query) {
        try {
            return source.getConnection().prepareStatement(query);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    @Override
    public QueryData executeReturn(String query) {
        try (Connection connection = source.getConnection(); Statement statement = connection.createStatement()) {
            return new QueryDataImpl(statement.executeQuery(query));
        } catch (SQLException throwable) {
            Logger.getGlobal().severe("Error on sql query:" + query);
            throwable.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() {
        source.close();
    }

    @Override
    public void connect(String host) {
        super.connect("jdbc:mariadb://" + host);
    }

    @Override
    public void connect(String host, String username) {
        super.connect("jdbc:mariadb://" + host, username);
    }

    @Override
    public void connect(String host, String username, String password) {
        super.connect("jdbc:mariadb://" + host, username, password);
    }

}
