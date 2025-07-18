package studio.geonlee.auto_creator.generator;

import studio.geonlee.auto_creator.common.enumeration.DatabaseType;
import studio.geonlee.auto_creator.common.enumeration.LogType;
import studio.geonlee.auto_creator.common.record.FieldMetadata;
import studio.geonlee.auto_creator.common.util.CaseUtils;
import studio.geonlee.auto_creator.common.util.DatabaseMetaReader;
import studio.geonlee.auto_creator.common.util.NamingUtils;
import studio.geonlee.auto_creator.config.setting.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;
import studio.geonlee.auto_creator.config.setting.GlobalConfig;
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

            // ✅ mode 분리
            boolean isRequest = mode.endsWith("-request");
            String action = mode.split("-")[0]; // "create", "update", "delete", "search"
            String domain = CaseUtils.extractDomain(tableName);
            String pascalDomain = CaseUtils.toUppercaseFirstLetter(NamingUtils.convertFullNaming(domain));
            String recordName = pascalDomain + CaseUtils.toPascalCase(action) +
                    (isRequest ? "RequestRecord" : "ResponseRecord");

            // ✅ 필드 필터링
            List<FieldMetadata> selectedFields = switch (action.toLowerCase()) {
                case "create" -> fields.stream()
                        .filter(f -> !isRequest || !f.primaryKey()) // Create-Request 는 PK 제외
                        .toList();
                case "modify", "search" -> fields;
                case "delete" -> fields.stream()
                        .filter(FieldMetadata::primaryKey)
                        .toList();
                default -> throw new IllegalArgumentException("Unknown mode: " + mode);
            };

            DefaultConfig config = GlobalConfig.defaultConfig;
            boolean useSwagger = config.isUseSwagger();
            String basePackage = config.getDomainBasePackage();
            String recordPackage = basePackage + "." + domain + ".record";

            StringBuilder sb = new StringBuilder();
            sb.append("package ").append(recordPackage).append(";").append("\n\n");

            boolean needsValidation = isRequest && selectedFields.stream()
                    .anyMatch(f -> !f.nullable() || ("String".equals(f.javaType()) && f.length() > 0));

            if (needsValidation) {
                sb.append("import jakarta.validation.constraints.*;\n\n");
            }
            if (hasTime(selectedFields)) {
                sb.append("import java.time.*;\n");
                sb.append("import com.fasterxml.jackson.annotation.JsonFormat;\n");
            }
            if (useSwagger) {
                sb.append("import io.swagger.v3.oas.annotations.media.Schema;\n\n");
                sb.append("@Schema(description = \"")
                        .append(pascalDomain).append(" ").append(CaseUtils.toPascalCase(action))
                        .append(isRequest ? " Request" : " Response")
                        .append(" Record\")\n");
            }

            sb.append("public record ").append(recordName).append("(\n");
            for (int i = 0; i < selectedFields.size(); i++) {
                FieldMetadata field = selectedFields.get(i);
//                String fieldType = convertFieldType(field.javaType(), isRequest);
                String fieldType = field.javaType();

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

                if (isRequest) {
                    if ("LocalDate".equals(fieldType)) {
                        sb.append("    @DateTimeFormat(pattern = \"yyyy-MM-dd\")\n");
                    } else if ("LocalDateTime".equals(fieldType)) {
                        sb.append("    @DateTimeFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")\n");
                    }
                } else {
                    if ("LocalDate".equals(fieldType)) {
                        sb.append("    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = \"yyyy-MM-dd\")\n");
                    } else if ("LocalDateTime".equals(fieldType)) {
                        sb.append("    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = \"yyyy-MM-dd HH:mm:ss\")\n");
                    }
                }

                sb.append("    ").append(fieldType).append(" ").append(NamingUtils.convertFullNaming(field.fieldName()));
                sb.append(i < selectedFields.size() - 1 ? ",\n\n" : "\n");
            }

            sb.append(") {}\n");

            return sb.toString();

        } catch (Exception e) {
            MainFrame.log("❌ Record 생성 실패: " + e.getMessage(), LogType.EXCEPTION);
            return "// ❌ Record 생성 실패: " + e.getMessage();
        }
    }

    /**
     * Request 일 때는 LocalDate/LocalDateTime을 String으로 변환
     * -> 2025-07-07
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

        if ("string".equals(type)) return "sample text";
        if (type.contains("int") || "long".equals(type)) return "1";
        if ("boolean".equals(type)) return "true";
        if ("localdate".equals(type)) return "2025-04-29";
        if ("localdatetime".equals(type) || "time".equals(type)) return "2025-04-29 00:00:00";
        if ("bigdecimal".equals(type) || "double".equals(type) || "float".equals(type)) return "12345.67";
        return "sample";
    }

    private static boolean hasTime(List<FieldMetadata> selectedFields) {

        for (FieldMetadata field : selectedFields) {
            if (field.javaType().contains("LocalDate")) {
                return true;
            }
        }
        return false;
    }
}