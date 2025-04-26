package studio.geonlee.auto_creator.generator;

import studio.geonlee.auto_creator.context.DatabaseContext;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author GEON
 * @since 2025-04-25
 **/
public class EntityGenerator {
    private static final Map<String, String> typeMap = new HashMap<>();
    static {
        typeMap.put("varchar", "String");
        typeMap.put("text", "String");
        typeMap.put("char", "String");
        typeMap.put("uuid", "String");

        typeMap.put("int4", "Integer");
        typeMap.put("integer", "Integer");
        typeMap.put("int8", "Long");
        typeMap.put("bigint", "Long");

        typeMap.put("bool", "Boolean");
        typeMap.put("boolean", "Boolean");

        typeMap.put("date", "LocalDate");
        typeMap.put("timestamp", "LocalDateTime");
        typeMap.put("timestamp without time zone", "LocalDateTime");
    }

    public static String generate(String className, String tableName) {
        StringBuilder sb = new StringBuilder();
        sb.append("package com.example.entity;\n\n");
        sb.append("import lombok.Getter;\n");
        sb.append("import lombok.Setter;\n\n");
        sb.append("import java.time.*;\n\n");
        sb.append("@Getter\n@Setter\n");
        sb.append("public class ").append(className).append(" {\n\n");

        try {
            Connection conn = DatabaseContext.getConnection();
            String schema = "public"; // 필요시 사용자 설정 반영 가능
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getColumns(null, schema, tableName, null);

            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String typeName = rs.getString("TYPE_NAME").toLowerCase();

                String javaType = typeMap.getOrDefault(typeName, "String"); // fallback: String

                sb.append("    private ").append(javaType)
                        .append(" ").append(columnName).append(";\n");
            }

        } catch (Exception e) {
            return "// 오류 발생: " + e.getMessage();
        }

        sb.append("}\n");
        return sb.toString();
    }
}
