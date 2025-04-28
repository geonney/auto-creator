package studio.geonlee.auto_creator.generator;

import studio.geonlee.auto_creator.common.record.EntityMetadata;
import studio.geonlee.auto_creator.common.record.FieldMetadata;
import studio.geonlee.auto_creator.common.util.CaseUtils;

import java.util.List;

/**
 * @author GEON
 * @since 2025-04-28
 **/
public class QueryGenerator {

    public static String generate(String type, EntityMetadata meta) {
        return switch (type.toLowerCase()) {
            case "select" -> generateSelect(meta);
            case "insert" -> generateInsert(meta);
            case "update" -> generateUpdate(meta);
            case "delete" -> generateDelete(meta);
            default -> "// ❌ 지원하지 않는 쿼리 타입입니다.";
        };
    }

    private static String generateSelect(EntityMetadata meta) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT");

        List<FieldMetadata> fields = meta.fields();
        for (int i = 0; i < fields.size(); i++) {
            if(i == 0) {
                sb.append(" ");
            } else {
                sb.append("       ");
            }
            sb.append(fields.get(i).columnName());
            if (i < fields.size() - 1) sb.append(",");
            sb.append("\n");
        }

        sb.append("  FROM ").append(meta.schema()).append(".").append(meta.tableName()).append("\n");
        sb.append(" WHERE 1=1");
        return sb.toString();
    }

    private static String generateInsert(EntityMetadata meta) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(meta.schema()).append(".").append(meta.tableName()).append(" (\n");

        List<FieldMetadata> fields = meta.fields().stream()
                .filter(f -> !f.primaryKey()) // ✅ 보통 PK는 INSERT 안함
                .toList();

        for (int i = 0; i < fields.size(); i++) {
            sb.append("    ").append(fields.get(i).columnName());
            if (i < fields.size() - 1) sb.append(",");
            sb.append("\n");
        }

        sb.append(") VALUES (\n");

        for (int i = 0; i < fields.size(); i++) {
            sb.append("    #{").append(CaseUtils.toCamelCase(fields.get(i).columnName())).append("}");
            if (i < fields.size() - 1) sb.append(",");
            sb.append("\n");
        }

        sb.append(");\n");
        return sb.toString();
    }

    private static String generateUpdate(EntityMetadata meta) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ").append(meta.schema()).append(".").append(meta.tableName()).append("\n");
        sb.append("   SET");

        List<FieldMetadata> fields = meta.fields().stream()
                .filter(f -> !f.primaryKey()) // ✅ PK는 SET 안함
                .toList();

        for (int i = 0; i < fields.size(); i++) {
            sb.append(" ").append(fields.get(i).columnName())
                    .append(" = #{").append(CaseUtils.toCamelCase(fields.get(i).columnName())).append("}");
            if (i < fields.size() - 1) {
                sb.append(",");
                sb.append("\n      ");
            }
        }

        // ✅ WHERE 조건: PK 기준
        sb.append("\n WHERE ");
        List<FieldMetadata> pkFields = meta.fields().stream()
                .filter(FieldMetadata::primaryKey)
                .toList();

        for (int i = 0; i < pkFields.size(); i++) {
            sb.append(pkFields.get(i).columnName())
                    .append(" = #{").append(CaseUtils.toCamelCase(pkFields.get(i).columnName())).append("}\n");
            if (i < pkFields.size() - 1) sb.append("   AND ");
        }
        return sb.toString();
    }

    private static String generateDelete(EntityMetadata meta) {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ").append(meta.schema()).append(".").append(meta.tableName()).append("\n");
        sb.append(" WHERE");

        List<FieldMetadata> pkFields = meta.fields().stream()
                .filter(FieldMetadata::primaryKey)
                .toList();

        for (int i = 0; i < pkFields.size(); i++) {
            sb.append(" ").append(pkFields.get(i).columnName()).append(" = #{")
                    .append(CaseUtils.toCamelCase(pkFields.get(i).columnName())).append("}\n");
            if (i < pkFields.size() - 1) sb.append("   AND");
        }
        return sb.toString();
    }
}
