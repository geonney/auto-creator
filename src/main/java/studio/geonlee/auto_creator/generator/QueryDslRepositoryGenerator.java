package studio.geonlee.auto_creator.generator;

import studio.geonlee.auto_creator.common.record.EntityMetadata;
import studio.geonlee.auto_creator.common.util.CaseUtils;
import studio.geonlee.auto_creator.config.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;

/**
 * @author GEON
 * @since 2025-07-08
 **/
public class QueryDslRepositoryGenerator {

    public static String generate(EntityMetadata meta) {
        DefaultConfig config = DefaultConfigFileHandler.load();
        String basePackage = config.getDomainBasePackage();
        String entityBasePackage = config.getEntityBasePackage();
        String tableName = meta.tableName().toLowerCase();
        String domain = CaseUtils.extractDomain(tableName);
        String pascalDomain = CaseUtils.toUppercaseFirstLetter(domain);
        String fullPackage = basePackage + "." + domain;

        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(fullPackage).append(";\n\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("SearchRequestRecord;\n");
        sb.append("import ").append(entityBasePackage).append(".").append(CaseUtils.toUppercaseFirstLetter(tableName))
                .append(";\n");
        sb.append("import org.springframework.data.domain.Page;\n");
        sb.append("import org.springframework.data.domain.Pageable;\n");

        sb.append("\n");

        sb.append("public interface ").append((pascalDomain))
                .append("RepositoryDsl {\n\n")
                .append("   Page<").append(CaseUtils.toUppercaseFirstLetter(tableName)).append(">")
                .append(" searchGrid(").append(pascalDomain).append("SearchRequestRecord request")
                .append(", Pageable pageable);\n\n");
        sb.append("}\n");

        return sb.toString();
    }
}