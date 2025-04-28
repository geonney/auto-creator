package studio.geonlee.auto_creator.generator.layered;

import studio.geonlee.auto_creator.common.record.EntityMetadata;
import studio.geonlee.auto_creator.config.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;

/**
 * @author GEON
 * @since 2025-04-28
 **/
public class ServiceInterfaceGenerator {

    public static String generate(EntityMetadata meta) {
        DefaultConfig config = DefaultConfigFileHandler.load();
        String basePackage = config.getDomainBasePackage();
        String domain = meta.tableName().toLowerCase();
        String fullPackage = basePackage + "." + domain;

        String entityName = meta.baseClassName();

        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(fullPackage).append(";\n\n");

        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("CreateRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("UpdateRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("DeleteRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("SearchRecord;\n");
        sb.append("import java.util.List;\n\n");

        sb.append("public interface ").append(entityName).append("Service {\n\n");

        sb.append("    void create(").append(entityName).append("CreateRecord request);\n\n");
        sb.append("    void update(").append(entityName).append("UpdateRecord request);\n\n");
        sb.append("    void delete(").append(entityName).append("DeleteRecord request);\n\n");
        sb.append("    List<").append(entityName).append("SearchRecord> search(")
                .append(entityName).append("SearchRecord request);\n");

        sb.append("}\n");

        return sb.toString();
    }
}