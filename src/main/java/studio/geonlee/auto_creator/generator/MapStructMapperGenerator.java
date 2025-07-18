package studio.geonlee.auto_creator.generator;

import studio.geonlee.auto_creator.common.record.EntityMetadata;
import studio.geonlee.auto_creator.common.util.CaseUtils;
import studio.geonlee.auto_creator.common.util.NamingUtils;
import studio.geonlee.auto_creator.config.setting.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;
import studio.geonlee.auto_creator.config.setting.GlobalConfig;

/**
 * @author GEON
 * @since 2025-04-30
 **/
public class MapStructMapperGenerator {

    public static String generate(EntityMetadata meta) {
        DefaultConfig config = GlobalConfig.defaultConfig;
        String basePackage = config.getDomainBasePackage();
        String entityPackage = config.getEntityBasePackage();
        String tableName = meta.tableName().toLowerCase();
        String domain = NamingUtils.convertFullNaming(CaseUtils.extractDomain(tableName));
        String pascalDomain = CaseUtils.toUppercaseFirstLetter(NamingUtils.convertFullNaming(domain));
        String fullPackage = basePackage + "." + domain;

        StringBuilder sb = new StringBuilder();

        sb.append("package ").append(fullPackage).append(";\n\n");

        sb.append("import org.mapstruct.*;\n");
        sb.append("import ").append(entityPackage).append(".").append(CaseUtils.toUppercaseFirstLetter(tableName)).append(";\n");
        sb.append("import ").append(basePackage).append(".").append(domain).append(".record.").append(pascalDomain).append("CreateResponseRecord;\n");
        sb.append("import ").append(basePackage).append(".").append(domain).append(".record.").append(pascalDomain).append("ModifyResponseRecord;\n");
        sb.append("import ").append(basePackage).append(".").append(domain).append(".record.").append(pascalDomain).append("SearchResponseRecord;\n");
        sb.append("import ").append(basePackage).append(".").append(domain).append(".record.").append(pascalDomain).append("ModifyRequestRecord;\n\n");

        sb.append("import java.util.List;\n\n");

        sb.append("@Mapper(componentModel = \"spring\")\n");
        sb.append("public interface ").append(pascalDomain).append("Mapper {\n\n");

        // toEntity -> entity immutable 정책으로 제거
//        sb.append("    ").append(CaseUtils.toUppercaseFirstLetter(tableName)).append(" toEntity(")
//                .append(pascalDomain).append("CreateRequestRecord record);\n\n");

//        sb.append("    ").append(pascalDomain).append("SearchResponseRecord toSearchResponse(")
//                .append(CaseUtils.toUppercaseFirstLetter(tableName))
//                .append(" entity").append(");\n\n");
        sb.append("    List<").append(pascalDomain).append("SearchResponseRecord> toSearchResponseList(List<")
                .append(CaseUtils.toUppercaseFirstLetter(tableName)).append("> entityList);\n\n");

        sb.append("    ").append(pascalDomain).append("CreateResponseRecord toCreateResponse(")
                .append(CaseUtils.toUppercaseFirstLetter(tableName))
                .append(" entity").append(");\n\n");

        sb.append("    ").append(pascalDomain).append("ModifyResponseRecord toModifyResponse(")
                .append(CaseUtils.toUppercaseFirstLetter(tableName))
                .append(" entity").append(");\n\n");

        //entity immutable 정책으로 제거
//        sb.append("    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)\n");
//        sb.append("    ").append(CaseUtils.toUppercaseFirstLetter(tableName)).append(" updateFromRequest(")
//                .append(pascalDomain).append("ModifyRequestRecord request, @MappingTarget ")
//                .append(CaseUtils.toUppercaseFirstLetter(tableName)).append(" entity").append(");\n\n");

        sb.append("}\n");

        return sb.toString();
    }
}


