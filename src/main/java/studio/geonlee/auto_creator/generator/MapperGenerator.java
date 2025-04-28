package studio.geonlee.auto_creator.generator;

import studio.geonlee.auto_creator.common.record.EntityMetadata;
import studio.geonlee.auto_creator.config.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;

/**
 * @author GEON
 * @since 2025-04-28
 **/
public class MapperGenerator {

    public static String generate(EntityMetadata meta) {
        DefaultConfig config = DefaultConfigFileHandler.load();
        String basePackage = config.getDomainBasePackage();
        String domain = meta.tableName().toLowerCase();
        String fullPackage = basePackage + "." + domain;

        String entityName = meta.baseClassName();

        StringBuilder sb = new StringBuilder();

        sb.append("package ").append(fullPackage).append(";\n\n");

        sb.append("import org.apache.ibatis.annotations.Mapper;\n");
        sb.append("import java.util.List;\n");
        sb.append("\n");

        sb.append("@Mapper\n");
        sb.append("public interface ").append(entityName).append("Mapper {\n\n");

        // ✅ Insert
        sb.append("    void insert(").append(entityName).append("CreateRecord request);\n\n");

        // ✅ Update
        sb.append("    void update(").append(entityName).append("UpdateRecord request);\n\n");

        // ✅ Delete (복합키 여부 상관 없이 DeleteRecord로 고정)
        sb.append("    void delete(").append(entityName).append("DeleteRecord request);\n\n");

        // ✅ Search
        sb.append("    List<").append(entityName).append("SearchRecord> search(")
                .append(entityName).append("SearchRecord request);\n");

        sb.append("}\n");

        return sb.toString();
    }
}