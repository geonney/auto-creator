package studio.geonlee.auto_creator.generator;

import studio.geonlee.auto_creator.common.record.EntityMetadata;
import studio.geonlee.auto_creator.common.record.FieldMetadata;
import studio.geonlee.auto_creator.common.util.CaseUtils;
import studio.geonlee.auto_creator.common.util.NamingUtils;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;
import studio.geonlee.auto_creator.config.setting.GlobalConfig;

import java.util.List;

/**
 * @author GEON
 * @since 2025-04-28
 **/
public class MybatisXmlGenerator {

    public static String generate(EntityMetadata meta) {
        DefaultConfig config = GlobalConfig.defaultConfig;
        String basePackage = config.getDomainBasePackage();
        String entityName = meta.baseClassName();
        String tableName = meta.tableName();
        String domain = NamingUtils.convertFullNaming(CaseUtils.extractDomain(tableName));
        String pascalDomain = CaseUtils.toUppercaseFirstLetter(NamingUtils.convertFullNaming(domain));
        String domainPackage = basePackage + "." + domain;
        String schema = meta.schema();

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        sb.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n\n");

        sb.append("<mapper namespace=\"").append(domainPackage).append(".").append(pascalDomain).append("Mapper\">\n\n");

        // INSERT
        sb.append("    <insert id=\"insert\" parameterType=\"")
                .append(domainPackage).append(".record.").append(pascalDomain).append("CreateRequestRecord\">\n");
        sb.append("        INSERT INTO ").append(tableName).append(" (\n");
        List<FieldMetadata> insertFields = meta.fields().stream().filter(f -> !f.primaryKey()).toList();
        for (int i = 0; i < insertFields.size(); i++) {
            sb.append("            ").append(insertFields.get(i).columnName());
            if (i < insertFields.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("        ) VALUES (\n");
        for (int i = 0; i < insertFields.size(); i++) {
            sb.append("            #{").append(NamingUtils.convertFullNaming(insertFields.get(i).fieldName())).append("}");
            if (i < insertFields.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("        )\n");
        sb.append("    </insert>\n\n");

        // UPDATE
        sb.append("    <update id=\"update\" parameterType=\"")
                .append(domainPackage).append(".record.").append(pascalDomain).append("ModifyRequestRecord\">\n");
        sb.append("        UPDATE ").append(tableName).append("\n");
        sb.append("        SET\n");
        List<FieldMetadata> updateFields = meta.fields().stream().filter(f -> !f.primaryKey()).toList();
        for (int i = 0; i < updateFields.size(); i++) {
            FieldMetadata field = updateFields.get(i);
            sb.append("            ").append(field.columnName())
                    .append(" = #{").append(NamingUtils.convertFullNaming(field.fieldName())).append("}");
            if (i < updateFields.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("        WHERE\n");
        List<FieldMetadata> pkFields = meta.fields().stream().filter(FieldMetadata::primaryKey).toList();
        for (int i = 0; i < pkFields.size(); i++) {
            FieldMetadata pk = pkFields.get(i);
            sb.append("            ").append(pk.columnName())
                    .append(" = #{").append(NamingUtils.convertFullNaming(pk.fieldName())).append("}");
            if (i < pkFields.size() - 1) sb.append(" AND");
            sb.append("\n");
        }
        sb.append("    </update>\n\n");

        // DELETE
        sb.append("    <delete id=\"delete\" parameterType=\"")
                .append(domainPackage).append(".record.").append(pascalDomain).append("DeleteRequestRecord\">\n");
        sb.append("        DELETE FROM ").append(".").append(tableName).append("\n");
        sb.append("        WHERE\n");
        for (int i = 0; i < pkFields.size(); i++) {
            FieldMetadata pk = pkFields.get(i);
            sb.append("            ").append(pk.columnName())
                    .append(" = #{").append(NamingUtils.convertFullNaming(pk.fieldName())).append("}");
            if (i < pkFields.size() - 1) sb.append(" AND");
            sb.append("\n");
        }
        sb.append("    </delete>\n\n");

        // SELECT
        sb.append("    <select id=\"search\" parameterType=\"")
                .append(domainPackage).append(".record.").append(pascalDomain).append("SearchRequestRecord\" ")
                .append("resultType=\"").append(domainPackage)
                .append(".record.").append(pascalDomain).append("SearchResponseRecord\">\n");
        sb.append("        SELECT\n");
        for (int i = 0; i < meta.fields().size(); i++) {
            FieldMetadata field = meta.fields().get(i);
            sb.append("            ").append(field.columnName())
                    .append(" AS ").append(NamingUtils.convertFullNaming(field.fieldName()));
            if (i < meta.fields().size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("        FROM ").append(".").append(tableName).append("\n");
        sb.append("        WHERE 1=1\n");

        // ✅ 동적 조건 with null + empty check for String
        for (FieldMetadata field : meta.fields()) {
            String fieldName = NamingUtils.convertFullNaming(field.fieldName());
            sb.append("            <if test=\"").append(fieldName).append(" != null");
            if ("String".equals(field.javaType())) {
                sb.append(" and ").append(fieldName).append(" != ''");
            }
            sb.append("\">\n");
            sb.append("                AND ").append(field.columnName()).append(" = #{").append(fieldName).append("}\n");
            sb.append("            </if>\n");
        }

        sb.append("    </select>\n\n");

        sb.append("</mapper>\n");
        return sb.toString();
    }
}