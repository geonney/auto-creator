package studio.geonlee.auto_creator.common.enumeration;

/**
 * @author GEON
 * @since 2025-04-25
 **/
public enum DatabaseType {
    POSTGRESQL("PostgreSQL", 5432, "jdbc:postgresql://%s:%d/"),
    MYSQL("MySQL", 3306, "jdbc:mysql://%s:%d/"),
    MARIADB("MariaDB", 3306, "jdbc:mariadb://%s:%d/"),
    ORACLE("Oracle", 1521, "jdbc:oracle:thin:@%s:%d/");

    private final String label;
    private final int defaultPort;
    private final String urlTemplate;

    DatabaseType(String label, int defaultPort, String urlTemplate) {
        this.label = label;
        this.defaultPort = defaultPort;
        this.urlTemplate = urlTemplate;
    }

    public String getLabel() {
        return label;
    }

    public int getDefaultPort() {
        return defaultPort;
    }

    public String formatUrl(String host, int port) {
        return String.format(urlTemplate, host, port);
    }

    @Override
    public String toString() {
        return label;
    }
}
