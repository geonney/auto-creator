package studio.geonlee.auto_creator.generator;

import studio.geonlee.auto_creator.common.record.EntityMetadata;
import studio.geonlee.auto_creator.common.util.CaseUtils;
import studio.geonlee.auto_creator.config.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;

/**
 * @author GEON
 * @since 2025-04-30
 **/
public class MapStructMapperGenerator {

    public static String generate(EntityMetadata meta) {
        DefaultConfig config = DefaultConfigFileHandler.load();
        String basePackage = config.getDomainBasePackage();
        String domain = meta.tableName().toLowerCase();
        String fullPackage = basePackage + "." + domain;

        String entityName = meta.baseClassName();
        String entityVar = CaseUtils.toCamelCase(entityName);

        StringBuilder sb = new StringBuilder();

        sb.append("package ").append(fullPackage).append(";\n\n");

        sb.append("import org.mapstruct.*;\n");
        sb.append("import ").append(basePackage).append(".").append(domain).append(".").append(entityName).append(";\n");
        sb.append("import ").append(basePackage).append(".").append(domain).append(".record.").append(entityName).append("CreateResponseRecord;\n");
        sb.append("import ").append(basePackage).append(".").append(domain).append(".record.").append(entityName).append("UpdateResponseRecord;\n");
        sb.append("import ").append(basePackage).append(".").append(domain).append(".record.").append(entityName).append("SearchResponseRecord;\n");
        sb.append("import ").append(basePackage).append(".").append(domain).append(".record.").append(entityName).append("UpdateRequestRecord;\n\n");

        sb.append("import java.util.List;\n\n");

        sb.append("@Mapper(componentModel = \"spring\")\n");
        sb.append("public interface ").append(entityName).append("Mapper {\n\n");

        sb.append("    ").append(entityName).append("SearchResponseRecord toSearchResponse(").append(entityName).append(" ").append(entityVar).append(");\n\n");
        sb.append("    List<").append(entityName).append("SearchResponseRecord> toSearchResponseList(List<")
                .append(entityName).append("> ").append(entityVar).append("List);\n\n");

        sb.append("    ").append(entityName).append("CreateResponseRecord toCreateResponse(").append(entityName).append(" ").append(entityVar).append(");\n\n");

        sb.append("    ").append(entityName).append("UpdateResponseRecord toUpdateResponse(").append(entityName).append(" ").append(entityVar).append(");\n\n");

        sb.append("    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)\n");
        sb.append("    ").append(entityName).append(" updateFromRequest(")
                .append(entityName).append("UpdateRequestRecord request, @MappingTarget ")
                .append(entityName).append(" ").append(entityVar).append(");\n\n");

        sb.append("}\n");

        return sb.toString();
    }
}


