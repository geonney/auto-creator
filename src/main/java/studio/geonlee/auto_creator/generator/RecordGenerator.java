package studio.geonlee.auto_creator.generator;

import studio.geonlee.auto_creator.common.enumeration.DatabaseType;
import studio.geonlee.auto_creator.common.record.FieldMetadata;
import studio.geonlee.auto_creator.common.util.CaseUtils;
import studio.geonlee.auto_creator.common.util.DatabaseMetaReader;
import studio.geonlee.auto_creator.config.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;
import studio.geonlee.auto_creator.ui.frame.MainFrame;

import java.util.List;


/**
 * @author GEON
 * @since 2025-04-26
 **/
public class RecordGenerator {

    public static String generate(
            String mode, // ex: "create-request", "create-response", ...
            String baseClassName,
            String tableName,
            String schema,
            DatabaseType databaseType
    ) {
        try {
            List<FieldMetadata> fields = DatabaseMetaReader.readTableFields(schema, tableName, databaseType);
            System.out.println(fields);

            // ✅ mode 분리
            boolean isRequest = mode.endsWith("-request");
            String action = mode.split("-")[0]; // "create", "update", "delete", "search"

            String recordName = baseClassName + CaseUtils.toPascalCase(action) + (isRequest ? "RequestRecord" : "ResponseRecord");

            // ✅ 필드 필터링
            List<FieldMetadata> selectedFields = switch (action.toLowerCase()) {
                case "create" -> fields.stream()
                        .filter(f -> !isRequest || !f.primaryKey()) // Create-Request 는 PK 제외
                        .toList();
                case "update", "search" -> fields;
                case "delete" -> fields.stream()
                        .filter(FieldMetadata::primaryKey)
                        .toList();
                default -> throw new IllegalArgumentException("Unknown mode: " + mode);
            };

            DefaultConfig config = DefaultConfigFileHandler.load();
            boolean useSwagger = config.isUseSwagger();
            String basePackage = config.getDomainBasePackage();
            String recordPackage = basePackage + "." + tableName.toLowerCase() + ".record";

            StringBuilder sb = new StringBuilder();
            sb.append("package ").append(recordPackage).append(";").append("\n\n");

            boolean needsValidation = isRequest && selectedFields.stream()
                    .anyMatch(f -> !f.nullable() || ("String".equals(f.javaType()) && f.length() > 0));

            if (needsValidation) {
                sb.append("import jakarta.validation.constraints.*;\n\n");
            }
            if (useSwagger) {
                sb.append("import io.swagger.v3.oas.annotations.media.Schema;\n\n");
            }

            if (useSwagger) {
                sb.append("@Schema(description = \"")
                        .append(tableName).append(" ").append(CaseUtils.toPascalCase(action))
                        .append(isRequest ? " Request" : " Response")
                        .append(" Record\")\n");
            }

            sb.append("public record ").append(recordName).append("(\n");
            for (int i = 0; i < selectedFields.size(); i++) {
                FieldMetadata field = selectedFields.get(i);
                String fieldType = convertFieldType(field.javaType(), isRequest);

                // ✅ 주석 (swagger 사용하지 않을 경우 주석으로 필드 comment 추가)
                if (!useSwagger && field.comment() != null) {
                    sb.append("    /** ").append(field.comment()).append(" */\n");
                }

                // ✅ Validation
                if (isRequest) {
                    if (!field.nullable()) {
                        sb.append("    @NotNull\n");
                    }
                    if ("String".equals(fieldType) && field.length() > 0) {
                        sb.append("    @Size(max = ").append(field.length()).append(")\n");
                    }
                }

                // ✅ Swagger
                if (useSwagger) {
                    sb.append("    @Schema(description = \"")
                            .append(field.comment() != null ? field.comment() : field.columnName())
                            .append("\", example = \"")
                            .append(generateExample(fieldType))
                            .append("\")\n");
                }

                sb.append("    ").append(fieldType).append(" ").append(field.fieldName());
                sb.append(i < selectedFields.size() - 1 ? ",\n\n" : "\n");
            }

            sb.append(") {}\n");

            return sb.toString();

        } catch (Exception e) {
            MainFrame.log("❌ Record 생성 실패: " + e.getMessage());
            return "// ❌ Record 생성 실패: " + e.getMessage();
        }
    }

    /**
     * Request 일 때는 LocalDate/LocalDateTime을 String으로 변환
     */
    private static String convertFieldType(String javaType, boolean isRequest) {
        if (isRequest) {
            return switch (javaType) {
                case "LocalDate", "LocalDateTime", "LocalTime" -> "String";
                default -> javaType;
            };
        } else {
            return javaType;
        }
    }

    /**
     * Swagger Example 값 생성
     */
    private static String generateExample(String fieldType) {
        String type = fieldType.toLowerCase();
        if (type.contains("string")) return "sample text";
        if (type.contains("int") || type.contains("long")) return "1";
        if (type.contains("boolean")) return "true";
        if (type.contains("localdate")) return "2025-04-29";
        if (type.contains("localdatetime") || type.contains("time")) return "2025-04-29 00:00:00";
        if (type.contains("bigdecimal") || type.contains("double") || type.contains("float")) return "12345.67";
        return "sample";
    }
}