package it.mineblock.basementx.persistence.hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import it.mineblock.basementx.api.persistence.generic.connection.Connector;
import it.mineblock.basementx.persistence.hikari.property.PropertiesProvider;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class HikariConnector implements Connector {

    protected HikariDataSource source;
    protected final HikariConfig config;

    public HikariConnector(String driver) {
        PropertiesProvider provider = getProperties();
        if (provider == null) {
            provider = new PropertiesProvider();
        }

        this.config = getConfig();
        config.setDriverClassName(driver);
        config.setDataSourceProperties(provider.getBuild());
    }

    public void connect(String host) {
        config.setJdbcUrl(host);
        source = new HikariDataSource(config);
        establish();
    }

    public void connect(String host, String username) {
        config.setJdbcUrl(host);
        config.setUsername(username);
        source = new HikariDataSource(config);
        establish();
    }

    public void connect(String host, String username, String password) {
        config.setJdbcUrl(host);
        config.setUsername(username);
        config.setPassword(password);
        source = new HikariDataSource(config);
        establish();
    }

    private void establish() {
        try (Connection connection = source.getConnection()) {
            System.out.println("Connection Established... " + connection);
        } catch (SQLException throwable) {
            System.out.println("Unable to Establish Connection...");
            throwable.printStackTrace();
        }
    }

    protected abstract HikariConfig getConfig();
    protected abstract PropertiesProvider getProperties();

}
