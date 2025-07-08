package studio.geonlee.auto_creator.generator;

import studio.geonlee.auto_creator.common.record.EntityMetadata;
import studio.geonlee.auto_creator.common.util.CaseUtils;
import studio.geonlee.auto_creator.config.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;

/**
 * @author GEON
 * @since 2025-07-08
 **/
public class QueryDslRepositoryImplGenerator {

    public static String generate(EntityMetadata meta) {
        DefaultConfig config = DefaultConfigFileHandler.load();
        String basePackage = config.getDomainBasePackage();
        String entityBasePackage = config.getEntityBasePackage();
        String tableName = meta.tableName().toLowerCase();
        String domain = tableName.substring(tableName.lastIndexOf('_') + 1);
        String pascalDomain = CaseUtils.toPascalCase(domain);
        String fullPackage = basePackage + "." + domain;

        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(fullPackage).append(";\n\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("SearchRequestRecord;\n");
        sb.append("import ").append(entityBasePackage).append(".").append(CaseUtils.toUppercaseFirstLetter(tableName))
                .append(";\n");
        sb.append("import ").append(entityBasePackage).append(".Q").append(CaseUtils.toUppercaseFirstLetter(tableName))
                .append(";\n");
        sb.append("import com.querydsl.jpa.JPQLQuery;\n");
        sb.append("import com.querydsl.jpa.impl.JPAQueryFactory;\n");
        sb.append("import import lombok.RequiredArgsConstructor;\n");
        sb.append("import org.springframework.data.domain.Page;\n");
        sb.append("import org.springframework.data.domain.PageImpl;\n");
        sb.append("import org.springframework.data.domain.Pageable;\n");
        sb.append("import import org.springframework.stereotype.Repository;\n");
        sb.append("\n");

        sb.append("@RequiredArgsConstructor\n@Repository\n");
        sb.append("public class ").append(pascalDomain).append("RepositoryDslImpl")
                .append(" implements ").append(pascalDomain).append("RepositoryDsl {\n\n")
                .append("    private final JPAQueryFactory jpaQueryFactory;\n\n");

        sb.append("    @Override\n");
        sb.append("    public Page<").append(CaseUtils.toUppercaseFirstLetter(tableName)).append("> searchGrid(")
                .append(CaseUtils.toUppercaseFirstLetter(domain)).append("SearchRequestRecord request, ")
                .append("Pageable pageable) {\n");

        sb.append("        Q").append(CaseUtils.toUppercaseFirstLetter(tableName)).append(" qEntity = ")
                .append("Q").append(CaseUtils.toUppercaseFirstLetter(tableName)).append(".")
                .append(tableName).append(";\n");

        sb.append("        JPQLQuery<").append(CaseUtils.toUppercaseFirstLetter(tableName)).append("> query = ")
                .append("jpaQueryFactory\n")
                .append("               .selectFrom(qEntity)\n")
                .append("               .where(\n")
                .append("                   /*조회 조건 추가*/\n")
                .append("               );\n\n");

        sb.append("        long totalCount = query.fetchCount();\n\n");

        sb.append("        List<").append(CaseUtils.toUppercaseFirstLetter(tableName)).append("> list = query\n")
                .append("               .offset(pageable.getOffset())\n")
                .append("               .limit(pageable.getPageSize())\n")
                .append("               .orderBy(/*정렬 추가*/)\n")
                .append("               .fetch();\n\n");

        sb.append("        return new PageImpl<>(list, pageable, totalCount);\n");
        sb.append("    }\n");

        sb.append("}\n");

        return sb.toString();
    }
}