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
        String tableName = meta.tableName().toLowerCase();
        String domain = tableName.substring(tableName.lastIndexOf('_') + 1);
        String pascalDomain = CaseUtils.toPascalCase(domain);
        String fullPackage = basePackage + "." + domain;

        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(fullPackage).append(";\n\n");

        sb.append("import lombok.RequiredArgsConstructor;\n");
        sb.append("import org.springframework.stereotype.Service;\n");
        sb.append("import java.util.List;\n");
        sb.append("import ").append(fullPackage).append(".").append(pascalDomain).append("Repository;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("CreateRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("CreateResponseRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("UpdateRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("UpdateResponseRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("DeleteRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("DeleteResponseRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("SearchRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("SearchResponseRecord;\n\n");

        sb.append("@Service\n");
        sb.append("@RequiredArgsConstructor\n");
        sb.append("public class ").append(pascalDomain).append("ServiceImpl implements ").append(pascalDomain).append("Service {\n\n");

        sb.append("    private final ").append(pascalDomain).append("Repository ").append(pascalDomain).append("Repository;\n\n");

        sb.append("    @Override\n");
        sb.append("    public ").append(pascalDomain).append("CreateResponseRecord create(")
                .append(pascalDomain).append("CreateRequestRecord request) {\n");
        sb.append("        // TODO: Create logic\n");
        sb.append("        return null;\n");
        sb.append("    }\n\n");

        sb.append("    @Override\n");
        sb.append("    public ").append(pascalDomain).append("ModifyResponseRecord modify(")
                .append(pascalDomain).append("ModifyRequestRecord request) {\n");
        sb.append("        // TODO: Modify logic\n");
        sb.append("        return null;\n");
        sb.append("    }\n\n");

        sb.append("    @Override\n");
        sb.append("    public ").append(pascalDomain).append("DeleteResponseRecord delete(")
                .append(pascalDomain).append("DeleteRequestRecord request) {\n");
        sb.append("        // TODO: Delete logic\n");
        sb.append("        return null;\n");
        sb.append("    }\n\n");

        sb.append("    @Override\n");
        sb.append("    public List<").append(pascalDomain).append("SearchResponseRecord> searchList(")
                .append(pascalDomain).append("SearchRequestRecord request) {\n");
        sb.append("        // TODO: list search logic\n");
        sb.append("        return List.of();\n");
        sb.append("    }\n\n");

        sb.append("    @Override\n");
        sb.append("    public ").append(pascalDomain).append("SearchResponseRecord searchDetail(")
                .append(pascalDomain).append("String id) {\n");
        sb.append("        // TODO: Detail search logic\n");
        sb.append("        return null;\n");
        sb.append("    }\n");

        sb.append("}\n");

        return sb.toString();
    }
}