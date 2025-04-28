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

        // XML Header
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n\n");

        // 시작 <mapper> 태그
        String basePackage = DefaultConfigFileHandler.load().getDomainBasePackage(); // 설정에서 읽기
        String entityName = CaseUtils.toPascalCase(meta.tableName());
        sb.append("<mapper namespace=\"").append(basePackage).append(".").append(meta.tableName().toLowerCase()).append(".mapper.").append(entityName).append("Mapper\">\n\n");

        sb.append(generateSelect(meta, entityName)).append("\n\n");
        sb.append(generateInsert(meta, entityName)).append("\n\n");
        sb.append(generateUpdate(meta, entityName)).append("\n\n");
        sb.append(generateDelete(meta, entityName)).append("\n");

        sb.append("</mapper>\n");

        return sb.toString();
    }

    private static String generateSelect(EntityMetadata meta, String entityName) {
        StringBuilder sb = new StringBuilder();
        sb.append("    <select id=\"search").append(entityName).append("\" ")
                .append("parameterType=\"").append(entityName).append("SearchRecord\" ")
                .append("resultType=\"").append(entityName).append("SearchRecord\">\n");

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

    private static String generateInsert(EntityMetadata meta, String entityName) {
        StringBuilder sb = new StringBuilder();
        sb.append("    <insert id=\"insert").append(entityName).append("\" ")
                .append("parameterType=\"").append(entityName).append("CreateRecord\">\n");

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

    private static String generateUpdate(EntityMetadata meta, String entityName) {
        StringBuilder sb = new StringBuilder();
        sb.append("    <update id=\"update").append(entityName).append("\" ")
                .append("parameterType=\"").append(entityName).append("UpdateRecord\">\n");

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

    private static String generateDelete(EntityMetadata meta, String entityName) {
        StringBuilder sb = new StringBuilder();
        sb.append("    <delete id=\"delete").append(entityName).append("\" ")
                .append("parameterType=\"").append(entityName).append("DeleteRecord\">\n");

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