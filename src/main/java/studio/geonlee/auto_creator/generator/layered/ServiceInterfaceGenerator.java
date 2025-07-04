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

        sb.append("import java.util.List;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("CreateRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("CreateResponseRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("UpdateRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("UpdateResponseRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("DeleteRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("DeleteResponseRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("SearchRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("SearchResponseRecord;\n\n");

        sb.append("public interface ").append(entityName).append("Service {\n\n");

        // Create
        sb.append("    ").append(entityName).append("CreateResponseRecord create(")
                .append(entityName).append("CreateRequestRecord request);\n\n");

        // Update
        sb.append("    ").append(entityName).append("UpdateResponseRecord update(")
                .append(entityName).append("UpdateRequestRecord request);\n\n");

        // Delete
        sb.append("    ").append(entityName).append("DeleteResponseRecord delete(")
                .append(entityName).append("DeleteRequestRecord request);\n\n");

        // List search
        sb.append("    List<").append(entityName).append("SearchResponseRecord> listSearch(")
                .append(entityName).append("SearchRequestRecord request);\n");

        sb.append("}\n");

        // List search
        sb.append("    ").append(entityName).append("SearchResponseRecord detailSearch(")
                .append(entityName).append("SearchRequestRecord request);\n");

        sb.append("}\n");

        return sb.toString();
    }
}