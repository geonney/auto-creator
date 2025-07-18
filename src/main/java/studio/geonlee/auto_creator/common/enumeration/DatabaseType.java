package studio.geonlee.auto_creator.common.enumeration;

import lombok.Getter;

/**
 * @author GEON
 * @since 2025-04-25
 **/
public enum DatabaseType {
    POSTGRESQL("PostgreSQL",
            5432,
            "jdbc:postgresql://%s:%d/",
            "SELECT datname FROM pg_database WHERE datistemplate = false"),
    MYSQL("MySQL",
            3306,
            "jdbc:mysql://%s:%d/",
            "SHOW DATABASES"),
    MARIADB("MariaDB",
            3306,
            "jdbc:mariadb://%s:%d/",
            "SHOW DATABASES"),
    ORACLE("Oracle",
            1521,
            "jdbc:oracle:thin:@%s:%d/",
            "SELECT username AS datname FROM all_users");

    @Getter
    private final String label;
    @Getter
    private final int defaultPort;
    private final String urlTemplate;
    @Getter
    private final String databaseListQuery;

    DatabaseType(String label, int defaultPort, String urlTemplate, String databaseListQuery) {
        this.label = label;
        this.defaultPort = defaultPort;
        this.urlTemplate = urlTemplate;
        this.databaseListQuery = databaseListQuery;
    }

    public String formatUrl(String host, int port) {
        return String.format(urlTemplate, host, port);
    }

    @Override
    public String toString() {
        return label;
    }
}
