package studio.geonlee.auto_creator.generator.layered;

import studio.geonlee.auto_creator.common.record.EntityMetadata;
import studio.geonlee.auto_creator.common.util.CaseUtils;
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
        String tableName = meta.tableName().toLowerCase();
        String domain = tableName.substring(tableName.lastIndexOf('_') + 1);
        String pascalDomain = CaseUtils.toPascalCase(domain);
        String fullPackage = basePackage + "." + domain;

        StringBuilder sb = new StringBuilder();

        sb.append("package ").append(fullPackage).append(";\n\n");

        sb.append("import java.util.List;\n");
        sb.append("import org.springframework.data.domain.Page;\n");
        sb.append("import org.springframework.data.domain.Pageable;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("CreateRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("CreateResponseRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("ModifyRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("ModifyResponseRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("DeleteRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("DeleteResponseRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("SearchRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("SearchResponseRecord;\n\n");

        sb.append("public interface ").append(pascalDomain).append("Service {\n\n");

        // Create
        sb.append("    ").append(pascalDomain).append("CreateResponseRecord create(")
                .append(pascalDomain).append("CreateRequestRecord request);\n\n");

        // Modify
        sb.append("    ").append(pascalDomain).append("ModifyResponseRecord modify(")
                .append(pascalDomain).append("ModifyRequestRecord request);\n\n");

        // Delete
        sb.append("    ").append(pascalDomain).append("DeleteResponseRecord delete(")
                .append(pascalDomain).append("DeleteRequestRecord request);\n\n");

        // List search
        sb.append("    List<").append(pascalDomain).append("SearchResponseRecord> searchList(")
                .append(pascalDomain).append("SearchRequestRecord request);\n\n");

        // Grid search
        sb.append("    Page<").append(pascalDomain).append("SearchResponseRecord> searchGrid(")
                .append(pascalDomain).append("SearchRequestRecord request, Pageable pageable);\n\n");

        // Detail search
        sb.append("    ").append(pascalDomain).append("SearchResponseRecord searchDetail(")
                .append("String id);\n");

        sb.append("}\n");

        return sb.toString();
    }
}