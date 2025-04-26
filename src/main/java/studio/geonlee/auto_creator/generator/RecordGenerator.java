package studio.geonlee.auto_creator.generator;

import studio.geonlee.auto_creator.common.enumeration.DatabaseType;
import studio.geonlee.auto_creator.common.record.FieldMetadata;
import studio.geonlee.auto_creator.context.DatabaseContext;

import java.sql.Connection;
import java.util.List;

import static studio.geonlee.auto_creator.generator.EntityCodeGenerator.extractFieldMetadata;

/**
 * @author GEON
 * @since 2025-04-26
 **/
public class RecordGenerator {

    public static String generate(
            String mode, // "Create", "Update", ...
            String baseClassName,
            String tableName,
            String schema,
            DatabaseType dbType
    ) {
        try {
            Connection conn = DatabaseContext.getConnection();
            List<FieldMetadata> fields = extractFieldMetadata(conn, schema, tableName, dbType);

            String recordName = baseClassName + mode + "Record";

            List<FieldMetadata> selected = switch (mode.toLowerCase()) {
                case "create" -> fields.stream()
                        .filter(f -> !f.primaryKey())
                        .toList();
                case "update", "search" -> fields;
                case "delete" -> fields.stream()
                        .filter(FieldMetadata::primaryKey)
                        .toList();
                default -> throw new IllegalArgumentException("Unknown mode: " + mode);
            };

            StringBuilder sb = new StringBuilder();
            sb.append("package studio.geonlee.record;\n\n");
            boolean hasValidation = selected.stream()
                    .anyMatch(f -> !f.nullable() || (f.javaType().equals("String") && f.length() > 0));

            if (hasValidation) {
                sb.append("import jakarta.validation.constraints.*;\n\n");
            }

            sb.append("public record ").append(recordName).append("(\n");

            for (int i = 0; i < selected.size(); i++) {
                FieldMetadata f = selected.get(i);

                if (f.comment() != null && !f.comment().isBlank()) {
                    sb.append("    /** ").append(f.comment()).append(" */\n");
                }

                // Validation 추가
                if (!f.nullable()) {
                    sb.append("    @NotNull\n");
                }
                if (f.javaType().equals("String") && f.length() > 0) {
                    sb.append("    @Size(max = ").append(f.length()).append(")\n");
                }

                sb.append("    ").append(f.javaType()).append(" ").append(f.fieldName());
                sb.append(i < selected.size() - 1 ? ",\n\n" : "\n");
            }

            sb.append(") {}\n");
            return sb.toString();

        } catch (Exception e) {
            return "// ❌ Record 생성 실패: " + e.getMessage();
        }
    }

    // extractFieldMetadata()는 EntityGenerator와 동일한 방식
}
