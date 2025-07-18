package studio.geonlee.auto_creator.generator.layered;

import studio.geonlee.auto_creator.common.record.EntityMetadata;
import studio.geonlee.auto_creator.common.util.CaseUtils;
import studio.geonlee.auto_creator.common.util.NamingUtils;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;
import studio.geonlee.auto_creator.config.setting.GlobalConfig;

/**
 * @author GEON
 * @since 2025-04-28
 **/
public class ServiceImplGenerator {

    public static String generate(EntityMetadata meta) {
        DefaultConfig config = GlobalConfig.defaultConfig;
        String basePackage = config.getDomainBasePackage();
        String tableName = meta.tableName().toLowerCase();
        String domain = NamingUtils.convertFullNaming(CaseUtils.extractDomain(tableName));
        String pascalDomain = CaseUtils.toUppercaseFirstLetter(NamingUtils.convertFullNaming(domain));
        String fullPackage = basePackage + "." + domain;

        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(fullPackage).append(";\n\n");

        sb.append("import lombok.RequiredArgsConstructor;\n");
        sb.append("import org.springframework.stereotype.Service;\n");
        sb.append("import java.util.List;\n");
        sb.append("import org.springframework.data.domain.Page;\n");
        sb.append("import org.springframework.data.domain.Pageable;\n");
        sb.append("import ").append(fullPackage).append(".").append(pascalDomain).append("Repository;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("CreateRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("CreateResponseRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("ModifyRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("ModifyResponseRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("DeleteRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("DeleteResponseRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("SearchRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("SearchResponseRecord;\n\n");

        sb.append("@Service\n");
        sb.append("@RequiredArgsConstructor\n");
        sb.append("public class ").append(pascalDomain).append("ServiceImpl implements ").append(pascalDomain).append("Service {\n\n");

        sb.append("    private final ").append(pascalDomain).append("Repository ").append(domain).append("Repository;\n\n");

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
        sb.append("        // TODO: List search logic\n");
        sb.append("        return List.of();\n");
        sb.append("    }\n\n");

        sb.append("    @Override\n");
        sb.append("    public ").append(pascalDomain).append("SearchResponseRecord searchDetail(")
                .append("String id) {\n");
        sb.append("        // TODO: Detail search logic\n");
        sb.append("        return null;\n");
        sb.append("    }\n\n");

        sb.append("    @Override\n");
        sb.append("    public Page<").append(pascalDomain).append("SearchResponseRecord> searchGrid(")
                .append(pascalDomain).append("SearchRequestRecord request, Pageable pageable) {\n");
        sb.append("        // TODO: Grid search logic\n");
        sb.append("        return null;\n");
        sb.append("    }\n\n");

        sb.append("}\n");

        return sb.toString();
    }
}