package studio.geonlee.auto_creator.common.record;

import studio.geonlee.auto_creator.common.enumeration.DatabaseType;
import studio.geonlee.auto_creator.common.util.EntityTypeMapper;

/**
 * @author GEON
 * @since 2025-04-25
 **/
public record FieldMetadata(
        String columnName,
        String fieldName,
        String javaType,
        boolean primaryKey,
        boolean nullable,
        int length,
        String columnDefault,
        String comment
) {
    public static FieldMetadata of(
            DatabaseType dbType,
            String columnName,
            String dbTypeName,
            boolean isPrimaryKey,
            boolean nullable,
            int length,
            String columnDefault,
            String comment
    ) {
        return new FieldMetadata(
                columnName,
                toCamelCase(columnName),
                EntityTypeMapper.map(dbType, dbTypeName),
                isPrimaryKey,
                nullable,
                length,
                columnDefault,
                comment
        );
    }

    private static String toCamelCase(String name) {
        if (name == null || name.isEmpty()) return name;
        String[] parts = name.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder(parts[0]);

        for (int i = 1; i < parts.length; i++) {
            sb.append(Character.toUpperCase(parts[i].charAt(0)));
            sb.append(parts[i].substring(1));
        }

        return sb.toString();
    }
}
