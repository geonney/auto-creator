package studio.geonlee.auto_creator.generator;

import studio.geonlee.auto_creator.common.record.EntityMetadata;
import studio.geonlee.auto_creator.common.record.FieldMetadata;
import studio.geonlee.auto_creator.common.util.CaseUtils;
import studio.geonlee.auto_creator.config.DefaultConfigFileHandler;

import java.util.List;

/**
 * @author GEON
 * @since 2025-04-28
 **/
public class MybatisXmlGenerator {

    public static String generate(EntityMetadata meta) {
        StringBuilder sb = new StringBuilder();

        // XML 헤더
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n\n");

        // 시작 <mapper> 태그
        String basePackage = DefaultConfigFileHandler.load().getDomainBasePackage(); // base package 기준
        String tableNameCamel = CaseUtils.toPascalCase(meta.tableName());
        sb.append("<mapper namespace=\"").append(basePackage).append(".").append(meta.tableName().toLowerCase()).append(".mapper.").append(tableNameCamel).append("Mapper\">\n\n");

        sb.append(generateSelect(meta)).append("\n\n");
        sb.append(generateInsert(meta)).append("\n\n");
        sb.append(generateUpdate(meta)).append("\n\n");
        sb.append(generateDelete(meta)).append("\n");

        // 끝 </mapper> 태그
        sb.append("</mapper>\n");

        return sb.toString();
    }

    private static String generateSelect(EntityMetadata meta) {
        StringBuilder sb = new StringBuilder();
        String id = "select" + CaseUtils.toPascalCase(meta.tableName());

        sb.append("    <select id=\"").append(id).append("\" resultType=\"")
                .append(CaseUtils.toPascalCase(meta.tableName())).append("\">\n");
        sb.append("        SELECT\n");

        List<FieldMetadata> fields = meta.fields();
        for (int i = 0; i < fields.size(); i++) {
            sb.append("            ").append(fields.get(i).columnName());
            if (i < fields.size() - 1) sb.append(",");
            sb.append("\n");
        }

        sb.append("        FROM ").append(meta.schema()).append(".").append(meta.tableName()).append("\n");
        sb.append("        WHERE 1=1\n");
        sb.append("    </select>");
        return sb.toString();
    }

    private static String generateInsert(EntityMetadata meta) {
        StringBuilder sb = new StringBuilder();
        String id = "insert" + CaseUtils.toPascalCase(meta.tableName());

        sb.append("    <insert id=\"").append(id).append("\">\n");
        sb.append("        INSERT INTO ").append(meta.schema()).append(".").append(meta.tableName()).append(" (\n");

        List<FieldMetadata> fields = meta.fields().stream()
                .filter(f -> !f.primaryKey())
                .toList();

        for (int i = 0; i < fields.size(); i++) {
            sb.append("            ").append(fields.get(i).columnName());
            if (i < fields.size() - 1) sb.append(",");
            sb.append("\n");
        }

        sb.append("        ) VALUES (\n");

        for (int i = 0; i < fields.size(); i++) {
            sb.append("            #{").append(CaseUtils.toCamelCase(fields.get(i).columnName())).append("}");
            if (i < fields.size() - 1) sb.append(",");
            sb.append("\n");
        }

        sb.append("        )\n");
        sb.append("    </insert>");
        return sb.toString();
    }

    private static String generateUpdate(EntityMetadata meta) {
        StringBuilder sb = new StringBuilder();
        String id = "update" + CaseUtils.toPascalCase(meta.tableName());

        sb.append("    <update id=\"").append(id).append("\">\n");
        sb.append("        UPDATE ").append(meta.schema()).append(".").append(meta.tableName()).append("\n");
        sb.append("        SET\n");

        List<FieldMetadata> fields = meta.fields().stream()
                .filter(f -> !f.primaryKey())
                .toList();

        for (int i = 0; i < fields.size(); i++) {
            sb.append("            ").append(fields.get(i).columnName())
                    .append(" = #{").append(CaseUtils.toCamelCase(fields.get(i).columnName())).append("}");
            if (i < fields.size() - 1) sb.append(",");
            sb.append("\n");
        }

        List<FieldMetadata> pkFields = meta.fields().stream()
                .filter(FieldMetadata::primaryKey)
                .toList();

        sb.append("        WHERE\n");
        for (int i = 0; i < pkFields.size(); i++) {
            sb.append("            ").append(pkFields.get(i).columnName())
                    .append(" = #{").append(CaseUtils.toCamelCase(pkFields.get(i).columnName())).append("}");
            if (i < pkFields.size() - 1) sb.append(" AND");
            sb.append("\n");
        }

        sb.append("    </update>");
        return sb.toString();
    }

    private static String generateDelete(EntityMetadata meta) {
        StringBuilder sb = new StringBuilder();
        String id = "delete" + CaseUtils.toPascalCase(meta.tableName());

        sb.append("    <delete id=\"").append(id).append("\">\n");
        sb.append("        DELETE FROM ").append(meta.schema()).append(".").append(meta.tableName()).append("\n");
        sb.append("        WHERE\n");

        List<FieldMetadata> pkFields = meta.fields().stream()
                .filter(FieldMetadata::primaryKey)
                .toList();

        for (int i = 0; i < pkFields.size(); i++) {
            sb.append("            ").append(pkFields.get(i).columnName())
                    .append(" = #{").append(CaseUtils.toCamelCase(pkFields.get(i).columnName())).append("}");
            if (i < pkFields.size() - 1) sb.append(" AND");
            sb.append("\n");
        }

        sb.append("    </delete>");
        return sb.toString();
    }
}
