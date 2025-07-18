package studio.geonlee.auto_creator.generator;

import studio.geonlee.auto_creator.common.record.EntityMetadata;
import studio.geonlee.auto_creator.common.util.CaseUtils;
import studio.geonlee.auto_creator.common.util.NamingUtils;
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
        String tableName = meta.tableName().toLowerCase();
        String domain = NamingUtils.convertFullNaming(CaseUtils.extractDomain(tableName));
        String pascalDomain = CaseUtils.toUppercaseFirstLetter(NamingUtils.convertFullNaming(domain));
        String fullPackage = basePackage + "." + domain;

        StringBuilder sb = new StringBuilder();

        sb.append("package ").append(fullPackage).append(";\n\n");

        sb.append("import org.apache.ibatis.annotations.Mapper;\n");
        sb.append("import java.util.List;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("CreateRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("ModifyRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("DeleteRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("SearchRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("SearchResponseRecord;\n\n");

        sb.append("@Mapper\n");
        sb.append("public interface ").append(pascalDomain).append("Mapper {\n\n");

        // Insert
        sb.append("    void insert(").append(pascalDomain).append("CreateRequestRecord request);\n\n");

        // Update
        sb.append("    void update(").append(pascalDomain).append("ModifyRequestRecord request);\n\n");

        // Delete
        sb.append("    void delete(").append(pascalDomain).append("DeleteRequestRecord request);\n\n");

        // Search
        sb.append("    List<").append(pascalDomain).append("SearchResponseRecord> search(")
                .append(pascalDomain).append("SearchRequestRecord request);\n");

        sb.append("}\n");

        return sb.toString();
    }
}