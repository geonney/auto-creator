package studio.geonlee.auto_creator.generator;

import studio.geonlee.auto_creator.common.record.EntityMetadata;
import studio.geonlee.auto_creator.config.setting.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;
import studio.geonlee.auto_creator.config.setting.GlobalConfig;

/**
 * @author GEON
 * @since 2025-04-28
 **/
public class MapperGenerator {

    public static String generate(EntityMetadata meta) {
        DefaultConfig config = GlobalConfig.defaultConfig;
        String basePackage = config.getDomainBasePackage();
        String domain = meta.tableName().toLowerCase();
        String fullPackage = basePackage + "." + domain;

        String entityName = meta.baseClassName();

        StringBuilder sb = new StringBuilder();

        sb.append("package ").append(fullPackage).append(";\n\n");

        sb.append("import org.apache.ibatis.annotations.Mapper;\n");
        sb.append("import java.util.List;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("CreateRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("ModifyRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("DeleteRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("SearchRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("SearchResponseRecord;\n\n");

        sb.append("@Mapper\n");
        sb.append("public interface ").append(entityName).append("Mapper {\n\n");

        // Insert
        sb.append("    void insert(").append(entityName).append("CreateRequestRecord request);\n\n");

        // Update
        sb.append("    void update(").append(entityName).append("ModifyRequestRecord request);\n\n");

        // Delete
        sb.append("    void delete(").append(entityName).append("DeleteRequestRecord request);\n\n");

        // Search
        sb.append("    List<").append(entityName).append("SearchResponseRecord> search(")
                .append(entityName).append("SearchRequestRecord request);\n");

        sb.append("}\n");

        return sb.toString();
    }
}