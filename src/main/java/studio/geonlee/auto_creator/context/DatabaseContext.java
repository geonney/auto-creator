package studio.geonlee.auto_creator.context;

import studio.geonlee.auto_creator.common.enumeration.DatabaseType;

import java.sql.Connection;

/**
 * @author GEON
 * @since 2025-04-25
 **/
public class DatabaseContext {
    private static Connection connection;
    private static String databaseName;
    private static DatabaseType databaseType;

    public static DatabaseType getDatabaseType() { return databaseType; }
    public static void setDatabaseType(DatabaseType type) { databaseType = type; }

    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection(Connection conn) {
        connection = conn;
    }

    public static String getDatabaseName() {
        return databaseName;
    }

    public static void setDatabaseName(String name) {
        databaseName = name;
    }
}
