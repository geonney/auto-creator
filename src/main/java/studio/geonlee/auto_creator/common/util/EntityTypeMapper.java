package studio.geonlee.auto_creator.common.util;

import studio.geonlee.auto_creator.common.enumeration.DatabaseType;

import java.util.Map;

/**
 * @author GEON
 * @since 2025-04-25
 **/
public class EntityTypeMapper {
    private static final Map<String, String> postgresMap = Map.ofEntries(
            Map.entry("varchar", "String"),
            Map.entry("int4", "Integer"),
            Map.entry("int8", "Long"),
            Map.entry("text", "String"),
            Map.entry("bool", "Boolean"),
            Map.entry("timestamp", "LocalDateTime"),
            Map.entry("date", "LocalDate")
    );

    private static final Map<String, String> mysqlMap = Map.ofEntries(
            Map.entry("varchar", "String"),
            Map.entry("bigint", "Long"),
            Map.entry("int", "Integer"),
            Map.entry("tinyint", "Boolean"),
            Map.entry("datetime", "LocalDateTime"),
            Map.entry("date", "LocalDate")
    );

    private static final Map<String, String> oracleMap = Map.ofEntries(
            Map.entry("VARCHAR2", "String"),
            Map.entry("NUMBER", "BigDecimal"),
            Map.entry("DATE", "LocalDate"),
            Map.entry("TIMESTAMP", "LocalDateTime")
    );

    private static final Map<DatabaseType, Map<String, String>> mappingByDb = Map.of(
            DatabaseType.POSTGRESQL, postgresMap,
            DatabaseType.MYSQL, mysqlMap,
            DatabaseType.MARIADB, mysqlMap,
            DatabaseType.ORACLE, oracleMap
    );

    public static String map(DatabaseType dbType, String dbColumnType) {
        if (dbType == null || dbColumnType == null) return "String";
        Map<String, String> typeMap = mappingByDb.getOrDefault(dbType, postgresMap);
        return typeMap.getOrDefault(dbColumnType.toLowerCase(), "String");
    }
}
