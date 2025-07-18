package studio.geonlee.auto_creator.generator;

import studio.geonlee.auto_creator.common.enumeration.DatabaseType;
import studio.geonlee.auto_creator.common.enumeration.LogType;
import studio.geonlee.auto_creator.common.record.FieldMetadata;
import studio.geonlee.auto_creator.common.util.CaseUtils;
import studio.geonlee.auto_creator.common.util.DatabaseMetaReader;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;
import studio.geonlee.auto_creator.config.setting.GlobalConfig;
import studio.geonlee.auto_creator.ui.frame.MainFrame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author GEON
 * @since 2025-04-25
 **/
public class EntityCodeGenerator {

    public static String generate(String className, String tableName, String schema, DatabaseType databaseType) {
        try {
            String domain = tableName.substring(tableName.lastIndexOf('_') + 1);
            List<FieldMetadata> fields = DatabaseMetaReader.readTableFields(schema, tableName, databaseType);
            List<FieldMetadata> pkFields = fields.stream().filter(FieldMetadata::primaryKey).toList();
            List<FieldMetadata> nonPkFields = fields.stream().filter(f -> !f.primaryKey()).toList();

            boolean isCompositePk = pkFields.size() > 1;

            StringBuilder sb = new StringBuilder();

            DefaultConfig config = GlobalConfig.defaultConfig;
            String entityBasePackage = config.getEntityBasePackage();
            String baseEntityPackage = entityBasePackage + (".base");
            List<String> baseEntityFields = new ArrayList<>();
            if (config.getBaseEntityColumnField().trim().isEmpty()) {
                baseEntityFields = Arrays.asList(config.getBaseEntityColumnField()
                        .replaceAll("\\s", "").split(","));
            }
            sb.append("package ").append(entityBasePackage).append(";").append("\n\n");
            if (config.isUseBaseEntity()) {
                sb.append("import ").append(baseEntityPackage).append(".BaseEntity;").append("\n\n");
            }
            sb.append("import jakarta.persistence.*;\n");
            sb.append("import lombok.Getter;\n");
            sb.append("import lombok.NoArgsConstructor;\n");
//            sb.append("import lombok.AllArgsConstructor;\n");
            sb.append("import java.time.*;\n\n");
            boolean usesBigDecimal = fields.stream()
                    .anyMatch(f -> f.javaType().equals("BigDecimal"));

            if (usesBigDecimal) {
                sb.append("import java.math.BigDecimal;\n");
            }

            sb.append("@Getter\n@Entity\n@NoArgsConstructor\n");
            sb.append("@Table(name = \"").append(tableName).append("\")\n");
            sb.append("public class ").append(className);
            if (config.isUseBaseEntity()) {
                sb.append(" extends BaseEntity");
            }
            sb.append(" {\n\n");

            // 복합키 처리
            if (isCompositePk) {
                sb.append("    @EmbeddedId\n");
                sb.append("    private ").append(className).append("Id id;\n\n");
            } else {
                for (FieldMetadata field : pkFields) {
                    if (field.comment() != null && !field.comment().isBlank()) {
                        sb.append("    /** ").append(field.comment()).append(" */\n");
                    }
                    sb.append("    @Id\n");
                    sb.append(generateColumnAnnotation(field));
                    sb.append("    private ").append(field.javaType()).append(" ").append(field.fieldName()).append(";\n\n");
                }
            }

            // 나머지 필드
            for (FieldMetadata field : nonPkFields) {
                if (config.isUseBaseEntity() && baseEntityFields.contains(field.fieldName())) {
                    continue;
                }
                if (field.comment() != null && !field.comment().isBlank()) {
                    sb.append("    /** ").append(field.comment()).append(" */\n");
                }
                sb.append(generateColumnAnnotation(field));
                sb.append("    private ").append(field.javaType()).append(" ").append(field.fieldName()).append(";\n\n");
            }

            //생성자
            sb.append("    /** ").append("""
                    생성자, modify 메서드에서 제외 해야하는 필드 목록
                            1. @CreatedBy, @LastModifiedBy, @CreatedDate, @LastModifiedDate가 추가된 Field.
                    """).append("   */\n");
            sb.append("    public ").append(className).append("(").append(CaseUtils.toUppercaseFirstLetter(domain))
                    .append("CreateRequestRecord").append(" request) {\n");
            for (FieldMetadata field : nonPkFields) {
                if (config.isUseBaseEntity() && baseEntityFields.contains(field.fieldName())) {
                    continue;
                }
                sb.append("        this.").append(field.fieldName())
                        .append(" = request.").append(field.fieldName()).append("();\n");
            }
            sb.append("    }\n\n");
            //수정 메서드 (immutable 하기 때문에 mapper 사용 X)
            sb.append("    public ").append("void modify(").append(CaseUtils.toUppercaseFirstLetter(domain))
                    .append("ModifyRequestRecord").append(" request) {\n");
            for (FieldMetadata field : nonPkFields) {
                if (config.isUseBaseEntity() && baseEntityFields.contains(field.fieldName())) {
                    continue;
                }
                sb.append("        this.").append(field.fieldName())
                        .append(" = request.").append(field.fieldName()).append("();\n");
            }
            sb.append("    }\n\n");

            sb.append("}\n");

            // 복합키 클래스도 함께 생성
            if (isCompositePk) {
                sb.append("\n\n").append(generateEmbeddedId(className + "Id", pkFields));
            }

            return sb.toString();

        } catch (Exception e) {
            MainFrame.log("// ❌ Entity 생성 실패: " + e.getMessage(), LogType.EXCEPTION);
            return "";
        }
    }

//    public static List<FieldMetadata> extractFieldMetadata(Connection conn, String schema, String tableName, DatabaseType dbType) throws SQLException {
//        List<FieldMetadata> list = new ArrayList<>();
//
//        Set<String> pkSet = new HashSet<>();
//        ResultSet pkRs = conn.getMetaData().getPrimaryKeys(null, schema, tableName);
//        while (pkRs.next()) {
//            pkSet.add(pkRs.getString("COLUMN_NAME"));
//        }
//
//        ResultSet rs = conn.getMetaData().getColumns(null, schema, tableName, null);
//        while (rs.next()) {
//            String columnName = rs.getString("COLUMN_NAME");
//            String typeName = rs.getString("TYPE_NAME");
//            boolean isPk = pkSet.contains(columnName);
//            boolean nullable = rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
//            int length = rs.getInt("COLUMN_SIZE");
//            String columnDefault = rs.getString("COLUMN_DEF"); // 기본값
//            String remarks = rs.getString("REMARKS");          // 주석
//
//            list.add(FieldMetadata.of(
//                    dbType, columnName, typeName, isPk, nullable, length, columnDefault, remarks
//            ));
//        }
//
//        return list;
//    }

    private static String generateEmbeddedId(String className, List<FieldMetadata> pkFields) {
        StringBuilder sb = new StringBuilder();

        sb.append("@Getter\n@EqualsAndHashCode\n@Embeddable\n@NoArgsConstructor\n@AllArgsConstructor\n");
        sb.append("public class ").append(className).append(" {\n\n");

        for (FieldMetadata field : pkFields) {
            if (field.comment() != null && !field.comment().isBlank()) {
                sb.append("    /** ").append(field.comment()).append(" */\n");
            }
            sb.append(generateColumnAnnotation(field));
            sb.append("    private ").append(field.javaType()).append(" ").append(field.fieldName()).append(";\n\n");
        }

        sb.append("}\n");
        return sb.toString();
    }

    private static String generateColumnAnnotation(FieldMetadata field) {
        StringBuilder sb = new StringBuilder("    @Column(name = \"")
                .append(field.columnName()).append("\"");

        if (!field.nullable()) {
            sb.append(", nullable = false");
        }

        if (field.javaType().equals("String") && field.length() > 0) {
            sb.append(", length = ").append(field.length());
        }

        if (field.columnDefault() != null && !field.columnDefault().isBlank()) {
            String def = field.columnDefault().replace("\"", "\\\"");
            sb.append(", columnDefinition = \"default ").append(def).append("\"");
        }

        sb.append(")\n");
        return sb.toString();
    }
}
