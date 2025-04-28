package studio.geonlee.auto_creator.generator.layered;

import studio.geonlee.auto_creator.common.record.EntityMetadata;
import studio.geonlee.auto_creator.common.util.CaseUtils;
import studio.geonlee.auto_creator.config.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;

/**
 * @author GEON
 * @since 2025-04-28
 **/
import java.util.List;

public class ControllerGenerator {

    public static String generate(EntityMetadata meta) {
        DefaultConfig config = DefaultConfigFileHandler.load();
        boolean useSwagger = config.isUseSwagger();

        String basePackage = config.getDomainBasePackage();
        String domain = meta.tableName().toLowerCase();
        String fullPackage = basePackage + "." + domain;

        String entityName = meta.baseClassName();
        String entityNameLower = CaseUtils.toCamelCase(entityName);

        StringBuilder sb = new StringBuilder();

        sb.append("package ").append(fullPackage).append(";\n\n");

        sb.append("import lombok.RequiredArgsConstructor;\n");
        sb.append("import org.springframework.web.bind.annotation.*;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("CreateRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("UpdateRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("DeleteRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("SearchRecord;\n");
        sb.append("import ").append(fullPackage).append(".").append(entityName).append("Service;\n");

        if (useSwagger) {
            sb.append("import io.swagger.v3.oas.annotations.Operation;\n");
            sb.append("import io.swagger.v3.oas.annotations.tags.Tag;\n");
        }

        sb.append("import java.util.List;\n\n");

        if (useSwagger) {
            sb.append("@Tag(name = \"").append(entityName).append(" Controller\")\n");
        }
        sb.append("@RestController\n");
        sb.append("@RequestMapping(\"/api/").append(domain).append("\")\n");
        sb.append("@RequiredArgsConstructor\n");
        sb.append("public class ").append(entityName).append("Controller {\n\n");

        sb.append("    private final ").append(entityName).append("Service ").append(entityNameLower).append("Service;\n\n");

        // Create
        if (useSwagger) {
            sb.append("    @Operation(summary = \"").append(entityName).append(" 생성\", description = \"")
                    .append(entityName).append(" 정보를 생성합니다.\")\n");
        }
        sb.append("    @PostMapping\n");
        sb.append("    public void create(@RequestBody ").append(entityName).append("CreateRecord request) {\n");
        sb.append("        ").append(entityNameLower).append("Service.create(request);\n");
        sb.append("    }\n\n");

        // Update
        if (useSwagger) {
            sb.append("    @Operation(summary = \"").append(entityName).append(" 수정\", description = \"")
                    .append(entityName).append(" 정보를 수정합니다.\")\n");
        }
        sb.append("    @PutMapping\n");
        sb.append("    public void update(@RequestBody ").append(entityName).append("UpdateRecord request) {\n");
        sb.append("        ").append(entityNameLower).append("Service.update(request);\n");
        sb.append("    }\n\n");

        // Delete
        if (useSwagger) {
            sb.append("    @Operation(summary = \"").append(entityName).append(" 삭제\", description = \"")
                    .append(entityName).append(" 정보를 삭제합니다.\")\n");
        }
        sb.append("    @PostMapping(\"/delete\")\n");
        sb.append("    public void delete(@RequestBody ").append(entityName).append("DeleteRecord request) {\n");
        sb.append("        ").append(entityNameLower).append("Service.delete(request);\n");
        sb.append("    }\n\n");

        // Search
        if (useSwagger) {
            sb.append("    @Operation(summary = \"").append(entityName).append(" 조회\", description = \"")
                    .append(entityName).append(" 목록을 조회합니다.\")\n");
        }
        sb.append("    @GetMapping\n");
        sb.append("    public List<").append(entityName).append("SearchRecord> search(@ModelAttribute ")
                .append(entityName).append("SearchRecord request) {\n");
        sb.append("        return ").append(entityNameLower).append("Service.search(request);\n");
        sb.append("    }\n");

        sb.append("}\n");

        return sb.toString();
    }
}

