package it.hemerald.basementx.common.persistence.hikari;

import it.hemerald.basementx.common.persistence.maria.structure.column.connector.MariaConnector;

import java.util.function.Supplier;

public enum TypeConnector {

    MARIADB(MariaConnector::new);

    private final Supplier<HikariConnector> connectorSupplier;


    TypeConnector(Supplier<HikariConnector> connectorSupplier) {
        this.connectorSupplier = connectorSupplier;
    }

    public HikariConnector provide() {
        return connectorSupplier.get();
    }

}
