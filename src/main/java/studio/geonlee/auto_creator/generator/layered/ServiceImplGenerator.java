package studio.geonlee.auto_creator.generator.layered;

import studio.geonlee.auto_creator.common.record.EntityMetadata;
import studio.geonlee.auto_creator.common.record.FieldMetadata;
import studio.geonlee.auto_creator.common.util.CaseUtils;
import studio.geonlee.auto_creator.config.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;

import java.util.List;

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
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("CreateRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("UpdateRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("DeleteRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("SearchRecord;\n\n");

        sb.append("@Service\n");
        sb.append("@RequiredArgsConstructor\n");
        sb.append("public class ").append(entityName).append("ServiceImpl implements ").append(entityName).append("Service {\n\n");

        sb.append("    private final ").append(entityName).append("Repository ").append(entityNameLower).append("Repository;\n\n");

        sb.append("    @Override\n");
        sb.append("    public void create(").append(entityName).append("CreateRecord request) {\n");
        sb.append("        // TODO: Create logic\n");
        sb.append("    }\n\n");

        sb.append("    @Override\n");
        sb.append("    public void update(").append(entityName).append("UpdateRecord request) {\n");
        sb.append("        // TODO: Update logic\n");
        sb.append("    }\n\n");

        sb.append("    @Override\n");
        sb.append("    public void delete(").append(entityName).append("DeleteRecord request) {\n");
        sb.append("        // TODO: Delete logic\n");
        sb.append("    }\n\n");

        sb.append("    @Override\n");
        sb.append("    public List<").append(entityName).append("SearchRecord> search(")
                .append(entityName).append("SearchRecord request) {\n");
        sb.append("        // TODO: Search logic\n");
        sb.append("        return null;\n");
        sb.append("    }\n");

        sb.append("}\n");

        return sb.toString();
    }
}