package studio.geonlee.auto_creator.generator.layered;

import studio.geonlee.auto_creator.common.record.EntityMetadata;
import studio.geonlee.auto_creator.common.util.CaseUtils;
import studio.geonlee.auto_creator.config.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;

public class ControllerGenerator {

    public static String generate(EntityMetadata meta) {
        DefaultConfig config = DefaultConfigFileHandler.load();
        boolean useSwagger = config.isUseSwagger();

        String domainBasePackage = config.getDomainBasePackage();
        String basePackage = domainBasePackage.replace(".domain", "");
        String domain = meta.tableName().toLowerCase();
        String fullPackage = domainBasePackage + "." + domain;

        String entityName = meta.baseClassName();
        String entityNameLower = CaseUtils.toCamelCase(entityName);

        StringBuilder sb = new StringBuilder();

        sb.append("package ").append(fullPackage).append(";\n\n");

        sb.append("import lombok.RequiredArgsConstructor;\n");
        sb.append("import jakarta.validation.Valid;\n");
        sb.append("import org.springframework.web.bind.annotation.*;\n");
        sb.append("import org.springframework.http.ResponseEntity;\n");
        sb.append("import ").append(basePackage).append(".common.response").append(".ItemResponse;\n");
        sb.append("import ").append(basePackage).append(".common.response").append(".ItemsResponse;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("CreateRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("CreateResponseRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("UpdateRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("UpdateResponseRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("DeleteRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("DeleteResponseRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("SearchRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(entityName).append("SearchResponseRecord;\n");
        sb.append("import ").append(fullPackage).append(".").append(entityName).append("Service;\n");

        if (useSwagger) {
            sb.append("import io.swagger.v3.oas.annotations.Operation;\n");
            sb.append("import io.swagger.v3.oas.annotations.tags.Tag;\n");
        }

        sb.append("import java.util.List;\n\n");

        if (useSwagger) {
            sb.append("@Tag(name = \"").append(entityName).append("\")\n");
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
        sb.append("    public ResponseEntity<").append(entityName)
                .append("CreateResponseRecord> create(\n            @RequestBody @Valid ")
                .append(entityName).append("CreateRequestRecord request) {\n");
        sb.append("        return ResponseEntity.ok(").append(entityNameLower).append("Service.create(request));\n");
        sb.append("    }\n\n");

        // Update
        if (useSwagger) {
            sb.append("    @Operation(summary = \"").append(entityName).append(" 수정\", description = \"")
                    .append(entityName).append(" 정보를 수정합니다.\")\n");
        }
        sb.append("    @PutMapping\n");
        sb.append("    public ResponseEntity<").append(entityName)
                .append("UpdateResponseRecord> update(\n            @RequestBody @Valid ")
                .append(entityName).append("UpdateRequestRecord request) {\n");
        sb.append("        return ResponseEntity.ok(").append(entityNameLower).append("Service.update(request));\n");
        sb.append("    }\n\n");

        // Delete
        if (useSwagger) {
            sb.append("    @Operation(summary = \"").append(entityName).append(" 삭제\", description = \"")
                    .append(entityName).append(" 정보를 삭제합니다.\")\n");
        }
        sb.append("    @PostMapping(\"/delete\")\n");
        sb.append("    public ResponseEntity<").append(entityName)
                .append("DeleteResponseRecord> delete(\n            @RequestBody @Valid ")
                .append(entityName).append("DeleteRequestRecord request) {\n");
        sb.append("        return ResponseEntity.ok(").append(entityNameLower).append("Service.delete(request));\n");
        sb.append("    }\n\n");

        // List Search
        if (useSwagger) {
            sb.append("    @Operation(summary = \"").append(entityName).append(" 목록 조회\", description = \"")
                    .append(entityName).append(" 목록을 조회합니다.\")\n");
        }
        sb.append("    @GetMapping\n");
        sb.append("    public ResponseEntity<ItemsResponse<").append(entityName)
                .append("SearchResponseRecord>> listSearch(\n            @ModelAttribute @Valid ")
                .append(entityName).append("SearchRequestRecord request) {\n");
        sb.append("        List<").append(entityName).append("SearchResponseRecord> list = ")
                .append(entityNameLower).append("Service.listSearch(request);\n");
        sb.append("        return ResponseEntity\n");
        sb.append("                 .ok()\n");
        sb.append("                 .body(ItemsResponse.<").append(entityName).append("SearchResponseRecord>builder()\n");
        sb.append("                         .status(\"OK\")\n");
        sb.append("                         .message(\"데이터 목록을 조회하는데 성공하였습니다.\")\n");
        sb.append("                         .totalSize((long) list.size())\n");
        sb.append("                         .items(list)\n");
        sb.append("                         .build());\n");
        sb.append("    }\n");

        sb.append("}\n");

        // single Search
        if (useSwagger) {
            sb.append("    @Operation(summary = \"").append(entityName).append(" 상세 조회\", description = \"")
                    .append(entityName).append(" 상세 조회합니다.\")\n");
        }
        sb.append("    @GetMapping\n");
        sb.append("    public ResponseEntity<ItemResponse<").append(entityName)
                .append("SearchResponseRecord>> detailSearch(\n            @ModelAttribute @Valid ")
                .append(entityName).append("SearchRequestRecord request) {\n");
        sb.append("        ").append(entityName).append("SearchResponseRecord> data = ")
                .append(entityNameLower).append("Service.detailSearch(request);\n");
        sb.append("        return ResponseEntity\n");
        sb.append("                 .ok()\n");
        sb.append("                 .body(ItemResponse.<").append(entityName).append("SearchResponseRecord>builder()\n");
        sb.append("                         .status(\"OK\")\n");
        sb.append("                         .message(\"상세 데이터를 조회하는데 성공하였습니다.\")\n");
        sb.append("                         .item(data)\n");
        sb.append("                         .build());\n");
        sb.append("    }\n");

        sb.append("}\n");


        return sb.toString();
    }
}

