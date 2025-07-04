package studio.geonlee.auto_creator.generator.layered;

import studio.geonlee.auto_creator.common.record.EntityMetadata;
import studio.geonlee.auto_creator.common.util.CaseUtils;
import studio.geonlee.auto_creator.config.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;

/**
 * @author GEON
 * @since 2025-04-28
 **/
public class ServiceImplGenerator {

    public static String generate(EntityMetadata meta) {
        DefaultConfig config = DefaultConfigFileHandler.load();
        String basePackage = config.getDomainBasePackage();
        String domain = meta.tableName().toLowerCase();
        String fullPackage = basePackage + "." + domain;

        String entityName = meta.baseClassName();
        String entityNameLower = CaseUtils.toCamelCase(entityName);

        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(fullPackage).append(";\n\n");

        sb.append("import lombok.RequiredArgsConstructor;\n");
        sb.append("import org.springframework.stereotype.Service;\n");
        sb.append("import java.util.List;\n");
        sb.append("import ").append(fullPackage).append(".").append(entityName).append("Repository;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("CreateRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("CreateResponseRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("UpdateRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("UpdateResponseRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("DeleteRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("DeleteResponseRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("SearchRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("SearchResponseRecord;\n\n");

        sb.append("@Service\n");
        sb.append("@RequiredArgsConstructor\n");
        sb.append("public class ").append(entityName).append("ServiceImpl implements ").append(entityName).append("Service {\n\n");

        sb.append("    private final ").append(entityName).append("Repository ").append(entityNameLower).append("Repository;\n\n");

        sb.append("    @Override\n");
        sb.append("    public ").append(entityName).append("CreateResponseRecord create(")
                .append(entityName).append("CreateRequestRecord request) {\n");
        sb.append("        // TODO: Create logic\n");
        sb.append("    }\n\n");

        sb.append("    @Override\n");
        sb.append("    public ").append(entityName).append("UpdateResponseRecord update(")
                .append(entityName).append("UpdateRequestRecord request) {\n");
        sb.append("        // TODO: Update logic\n");
        sb.append("    }\n\n");

        sb.append("    @Override\n");
        sb.append("    public ").append(entityName).append("DeleteResponseRecord delete(")
                .append(entityName).append("DeleteRequestRecord request) {\n");
        sb.append("        // TODO: Delete logic\n");
        sb.append("    }\n\n");

        sb.append("    @Override\n");
        sb.append("    public List<").append(entityName).append("SearchResponseRecord> listSearch(")
                .append(entityName).append("SearchRequestRecord request) {\n");
        sb.append("        // TODO: list search logic\n");
        sb.append("        return List.of();\n");
        sb.append("    }\n");

        sb.append("    @Override\n");
        sb.append("    public ").append(entityName).append("SearchResponseRecord detailSearch(")
                .append(entityName).append("SearchRequestRecord request) {\n");
        sb.append("        // TODO: Detail search logic\n");
        sb.append("        return null;\n");
        sb.append("    }\n");

        sb.append("}\n");

        return sb.toString();
    }
}