package studio.geonlee.auto_creator.common.record;

import studio.geonlee.auto_creator.common.enumeration.DatabaseType;
import studio.geonlee.auto_creator.common.util.CaseUtils;

import java.util.List;

/**
 * @author GEON
 * @since 2025-04-28
 * table 단위 metadata
 **/
public record EntityMetadata(
        String tableName,             // 테이블명 (ex: member)
        String baseClassName,          // 기본 클래스명 (ex: Member)
        String schema,                 // 스키마명 (ex: public)
        DatabaseType databaseType,     // Database 종류 (ex: PostgreSQL, MySQL)
        List<FieldMetadata> fields     // 컬럼 정보 리스트
) {
    // factory method
    public static EntityMetadata of(
            String schema,
            String tableName,
            DatabaseType dbType,
            List<FieldMetadata> fields
    ) {
        return new EntityMetadata(
                tableName,
                CaseUtils.toPascalCase(tableName),
                schema,
                dbType,
                fields
        );
    }
}
