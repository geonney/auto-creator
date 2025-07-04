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
public class RepositoryGenerator {

    public static String generate(EntityMetadata meta) {
        DefaultConfig config = DefaultConfigFileHandler.load();
        String basePackage = config.getDomainBasePackage();
        String tableName = meta.tableName().toLowerCase();
        String domain = tableName.substring(tableName.lastIndexOf('_') + 1);
        String pascalDomain = CaseUtils.toPascalCase(domain);
        String fullPackage = basePackage + "." + domain;

        String entityName = meta.baseClassName();

        // ✅ PK 타입 결정 로직
        List<FieldMetadata> pkFields = meta.fields().stream()
                .filter(FieldMetadata::primaryKey)
                .toList();

        String pkType;
        if (pkFields.size() == 1) {
            pkType = pkFields.get(0).javaType(); // 단일 PK 타입 그대로
        } else {
            pkType = entityName + "Id"; // 복합키: 엔티티명 + "Id"
        }

        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(fullPackage).append(";\n\n");

        sb.append("import org.springframework.data.jpa.repository.JpaRepository;\n");
        sb.append("import org.springframework.stereotype.Repository;\n\n");

        sb.append("@Repository\n");
        sb.append("public interface ").append((pascalDomain))
                .append("Repository extends JpaRepository<")
                .append(CaseUtils.toUppercaseFirstLetter(tableName)).append(", ").append(pkType).append("> {\n");
        sb.append("}\n");

        return sb.toString();
    }
}