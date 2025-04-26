package studio.geonlee.auto_creator.generator;

import studio.geonlee.auto_creator.common.enumeration.DatabaseType;
import studio.geonlee.auto_creator.common.record.FieldMetadata;
import studio.geonlee.auto_creator.config.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;
import studio.geonlee.auto_creator.context.DatabaseContext;
import studio.geonlee.auto_creator.frame.MainFrame;

import java.sql.Connection;
import java.util.List;

import static studio.geonlee.auto_creator.generator.EntityCodeGenerator.extractFieldMetadata;

/**
 * @author GEON
 * @since 2025-04-26
 **/
public class RecordGenerator {

    public static String generate(
            String mode, // "Create", "Update", "Delete", "Search"
            String baseClassName,
            String tableName,
            String schema,
            DatabaseType dbType
    ) {
        try {
            Connection conn = DatabaseContext.getConnection();
            List<FieldMetadata> fields = extractFieldMetadata(conn, schema, tableName, dbType);

            String recordName = baseClassName + mode + "Record";

            List<FieldMetadata> selectedFields = switch (mode.toLowerCase()) {
                case "create" -> fields.stream()
                        .filter(f -> !f.primaryKey())
                        .toList();
                case "update", "search" -> fields;
                case "delete" -> fields.stream()
                        .filter(FieldMetadata::primaryKey)
                        .toList();
                default -> throw new IllegalArgumentException("Unknown mode: " + mode);
            };

            DefaultConfig config = DefaultConfigFileHandler.load();
            boolean useSwagger = config.isUseSwagger();

            String basePackage = config.getRecordBasePackage();
            String recordPackage = basePackage + "." + tableName.toLowerCase() + ".record";

            StringBuilder sb = new StringBuilder();
            sb.append("package ").append(recordPackage).append(";").append("\n\n");

            boolean needsValidation = selectedFields.stream()
                    .anyMatch(f -> !f.nullable() || ("String".equals(f.javaType()) && f.length() > 0));

            if (needsValidation) {
                sb.append("import jakarta.validation.constraints.*;\n\n");
            }

            if (useSwagger) {
                sb.append("import io.swagger.v3.oas.annotations.media.Schema;\n\n");
            }

            if (useSwagger) {
                sb.append("@Schema(description = \"")
                        .append(tableName).append(" ").append(mode).append(" Record\")\n");
            }

            sb.append("public record ").append(recordName).append("(\n");

            for (int i = 0; i < selectedFields.size(); i++) {
                FieldMetadata field = selectedFields.get(i);

                if (!useSwagger && field.comment() != null) {
                    sb.append("    /** ").append(field.comment()).append(" */\n");
                }
                if (!field.nullable()) {
                    sb.append("    @NotNull\n");
                }
                if ("String".equals(field.javaType()) && field.length() > 0) {
                    sb.append("    @Size(max = ").append(field.length()).append(")\n");
                }
                if (useSwagger) {
                    sb.append("    @Schema(description = \"")
                            .append(field.comment() != null ? field.comment() : field.columnName())
                            .append("\", example = \"")
                            .append(generateExample(field.javaType()))
                            .append("\")\n");
                }

                sb.append("    ").append(field.javaType()).append(" ").append(field.fieldName());
                sb.append(i < selectedFields.size() - 1 ? ",\n\n" : "\n");
            }

            sb.append(") {}\n");
            return sb.toString();

        } catch (Exception e) {
            MainFrame.log("❌ Record 생성 실패");
            throw new RuntimeException("❌ Record 생성 실패", e);
        }
    }

    private static String generateExample(String fieldType) {
        fieldType = fieldType.toLowerCase();
        if (fieldType.contains("string")) {
            return "sample text";
        } else if (fieldType.contains("int") || fieldType.contains("long")) {
            return "1";
        } else if (fieldType.contains("boolean")) {
            return "true";
        } else if (fieldType.contains("date") && !fieldType.contains("time")) {
            return "2024-01-01";
        } else if (fieldType.contains("time")) {
            return "2024-01-01 12:00:00";
        } else if (fieldType.contains("bigdecimal") || fieldType.contains("double") || fieldType.contains("float")) {
            return "12345.67";
        }
        return "sample";
    }
}