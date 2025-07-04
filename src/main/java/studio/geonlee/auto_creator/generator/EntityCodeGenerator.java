package studio.geonlee.auto_creator.generator;

import studio.geonlee.auto_creator.common.enumeration.DatabaseType;
import studio.geonlee.auto_creator.common.record.FieldMetadata;
import studio.geonlee.auto_creator.common.util.DatabaseMetaReader;
import studio.geonlee.auto_creator.config.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;

import java.util.List;

/**
 * @author GEON
 * @since 2025-04-25
 **/
public class EntityCodeGenerator {

    public static String generate(String className, String tableName, String schema, DatabaseType databaseType) {
        try {
            List<FieldMetadata> fields = DatabaseMetaReader.readTableFields(schema, tableName, databaseType);

            List<FieldMetadata> pkFields = fields.stream().filter(FieldMetadata::primaryKey).toList();
            List<FieldMetadata> nonPkFields = fields.stream().filter(f -> !f.primaryKey()).toList();

            boolean isCompositePk = pkFields.size() > 1;

            StringBuilder sb = new StringBuilder();

            DefaultConfig config = DefaultConfigFileHandler.load();
            String entityBasePackage = config.getEntityBasePackage();

            sb.append("package ").append(entityBasePackage).append(";").append("\n\n");
            sb.append("import jakarta.persistence.*;\n");
            sb.append("import lombok.Getter;\n");
            sb.append("import java.time.*;\n\n");
            boolean usesBigDecimal = fields.stream()
                    .anyMatch(f -> f.javaType().equals("BigDecimal"));

            if (usesBigDecimal) {
                sb.append("import java.math.BigDecimal;\n");
            }

            sb.append("@Getter\n@Entity\n");
            sb.append("@Table(name = \"").append(tableName).append("\")\n");
            sb.append("public class ").append(className).append(" {\n\n");

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
                if (field.comment() != null && !field.comment().isBlank()) {
                    sb.append("    /** ").append(field.comment()).append(" */\n");
                }
                sb.append(generateColumnAnnotation(field));
                sb.append("    private ").append(field.javaType()).append(" ").append(field.fieldName()).append(";\n\n");
            }

            sb.append("}\n");

            // 복합키 클래스도 함께 생성
            if (isCompositePk) {
                sb.append("\n\n").append(generateEmbeddedId(className + "Id", pkFields));
            }

            return sb.toString();

        } catch (Exception e) {
            return "// ❌ Entity 생성 실패: " + e.getMessage();
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

        sb.append("@Getter\n@EqualsAndHashCode\n@Embeddable\n");
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
