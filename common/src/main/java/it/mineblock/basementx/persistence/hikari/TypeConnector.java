package it.mineblock.basementx.persistence.hikari;

import it.mineblock.basementx.persistence.maria.structure.column.connector.MariaConnector;

import java.util.function.Supplier;

public enum TypeConnector {

    MARIADB(MariaConnector::new);

    private final Supplier<HikariConnector> connectorSupplier;


    public HikariConnector provide() {
        return connectorSupplier.get();
    }

    TypeConnector(Supplier<HikariConnector> connectorSupplier) {
        this.connectorSupplier = connectorSupplier;
    }

}
