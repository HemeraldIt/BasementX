package it.mineblock.basementx.api.persistence.generic.connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * It represents the abstraction of the object
 * that contains the real connections and configurations
 * to maria database.
 */
public interface Connector {

    /**
     * try to connect to maria
     * database via a direct url
     *
     * @param host connection url
     */
    void connect(String host);

    /**
     * @param host     connection url
     * @param username username used for the authentication
     *                 TYPE LOGIN : USING PASSWORD NO
     */
    void connect(String host, String username);

    /**
     * @param host     connection url
     * @param username username used for the authentication
     * @param password password used for the authentication
     * TYPE LOGIN : USING PASSWORD YES
     */
    void connect(String host, String username, String password);

    /**
     * Executes a query
     * @param query the query
     */
    void execute(String query);

    /**
     * Get prepared statement for batching operations
     * @param query the query statement batched
     */
    PreparedStatement asPrepared(String query);

    /**
     * executes a query
     * expecting a return value
     * @param query the query
     * @return the return set of the query
     */
    ResultSet executeReturn(String query);

    void close();

}
