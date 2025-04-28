package studio.geonlee.auto_creator.common.record;

import studio.geonlee.auto_creator.common.enumeration.DatabaseType;
import studio.geonlee.auto_creator.common.util.CaseUtils;
import studio.geonlee.auto_creator.common.util.EntityTypeMapper;

/**
 * @author GEON
 * @since 2025-04-25
 * column 단위 metadata
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
                CaseUtils.toCamelCase(columnName),
                EntityTypeMapper.map(dbType, dbTypeName),
                isPrimaryKey,
                nullable,
                length,
                columnDefault,
                comment
        );
    }
}
