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
        String tableName = meta.tableName().toLowerCase();
        String domain = tableName.substring(tableName.lastIndexOf('_') + 1);
        String pascalDomain = CaseUtils.toPascalCase(domain);
        String fullPackage = domainBasePackage + "." + domain;

        String entityName = meta.baseClassName();

        StringBuilder sb = new StringBuilder();

        sb.append("package ").append(fullPackage).append(";\n\n");

        sb.append("import lombok.RequiredArgsConstructor;\n");
        sb.append("import jakarta.validation.Valid;\n");
        sb.append("import org.springframework.web.bind.annotation.*;\n");
        sb.append("import org.springframework.http.ResponseEntity;\n");
        sb.append("import org.springframework.data.domain.Page;\n");
        sb.append("import org.springframework.data.domain.Pageable;\n");
        sb.append("import ").append(basePackage).append(".common.response").append(".ItemResponse;\n");
        sb.append("import ").append(basePackage).append(".common.response").append(".ItemsResponse;\n");
        sb.append("import ").append(basePackage).append(".common.response").append(".GridResponse;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("CreateRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("CreateResponseRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("ModifyRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("ModifyResponseRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("DeleteRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("DeleteResponseRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("SearchRequestRecord;\n");
        sb.append("import ").append(fullPackage).append(".record.").append(pascalDomain).append("SearchResponseRecord;\n");
        sb.append("import org.springframework.web.bind.annotation.*;\n");
        sb.append("import ").append(fullPackage).append(".").append(pascalDomain).append("Service;\n");

        if (useSwagger) {
            sb.append("import io.swagger.v3.oas.annotations.Operation;\n");
            sb.append("import io.swagger.v3.oas.annotations.tags.Tag;\n");
        }

        sb.append("import java.util.List;\n\n");

        if (useSwagger) {
            sb.append("@Tag(name = \"").append(pascalDomain).append("\")\n");
        }
        sb.append("@RestController\n");
        sb.append("@RequestMapping(\"/api/").append(domain).append("\")\n");
        sb.append("@RequiredArgsConstructor\n");
        sb.append("public class ").append(pascalDomain).append("Controller {\n\n");

        sb.append("    private final ").append(pascalDomain).append("Service ").append(domain).append("Service;\n\n");

        // Create
        if (useSwagger) {
            sb.append("    @Operation(summary = \"").append(pascalDomain).append(" 생성\", description = \"")
                    .append(pascalDomain).append(" 정보를 생성합니다.\")\n");
        }
        sb.append("    @PostMapping(\"/create\")\n");
        sb.append("    public ResponseEntity<").append("ItemResponse<").append(pascalDomain)
                .append("CreateResponseRecord>> create(\n            @RequestBody @Valid ")
                .append(pascalDomain).append("CreateRequestRecord request) {\n");
        sb.append("        return ResponseEntity.ok()\n")
                .append("               .body(ItemResponse.<").append(pascalDomain)
                .append("CreateResponseRecord>builder()\n")
                .append("                       .status(\"OK\")\n")
                .append("                       .message(\"정보를 생성하였습니다.\")\n")
                .append("                       .item(")
                .append(domain).append("Service.create(request))\n")
                .append("                       .build());\n");
        sb.append("    }\n\n");

        // Modify
        if (useSwagger) {
            sb.append("    @Operation(summary = \"").append(pascalDomain).append(" 수정\", description = \"")
                    .append(pascalDomain).append(" 정보를 수정합니다.\")\n");
        }
        sb.append("    @PostMapping(\"/modify\")\n");
        sb.append("    public ResponseEntity<").append("ItemResponse<").append(pascalDomain)
                .append("ModifyResponseRecord>> modify(\n            @RequestBody @Valid ")
                .append(pascalDomain).append("ModifyRequestRecord request) {\n");
        sb.append("        return ResponseEntity.ok()\n")
                .append("               .body(ItemResponse.<").append(pascalDomain)
                .append("ModifyResponseRecord>builder()\n")
                .append("                       .status(\"OK\")\n")
                .append("                       .message(\"정보를 수정하였습니다.\")\n")
                .append("                       .item(")
                .append(domain).append("Service.modify(request))\n")
                .append("                       .build());\n");
        sb.append("    }\n\n");

        // Delete
        if (useSwagger) {
            sb.append("    @Operation(summary = \"").append(pascalDomain).append(" 삭제\", description = \"")
                    .append(pascalDomain).append(" 정보를 삭제합니다.\")\n");
        }
        sb.append("    @PostMapping(\"/delete\")\n");
        sb.append("    public ResponseEntity<").append("ItemResponse<").append(pascalDomain)
                .append("DeleteResponseRecord>> delete(\n            @RequestBody @Valid ")
                .append(pascalDomain).append("DeleteRequestRecord request) {\n");
        sb.append("        return ResponseEntity.ok()\n")
                .append("               .body(ItemResponse.<").append(pascalDomain)
                .append("DeleteResponseRecord>builder()\n")
                .append("                       .status(\"OK\")\n")
                .append("                       .message(\"정보를 삭제하였습니다.\")\n")
                .append("                       .item(")
                .append(domain).append("Service.delete(request))\n")
                .append("                       .build());\n");
        sb.append("    }\n\n");

        // List Search
        if (useSwagger) {
            sb.append("    @Operation(summary = \"").append(pascalDomain).append(" 목록 조회\", description = \"")
                    .append(pascalDomain).append(" 목록을 조회합니다.\")\n");
        }
        sb.append("    @GetMapping(\"/").append(domain).append("s\")\n");
        sb.append("    public ResponseEntity<ItemsResponse<").append(pascalDomain)
                .append("SearchResponseRecord>> searchList(\n            @ModelAttribute @Valid ")
                .append(pascalDomain).append("SearchRequestRecord request) {\n");
        sb.append("        List<").append(pascalDomain).append("SearchResponseRecord> list = ")
                .append(pascalDomain).append("Service.searchList(request);\n");
        sb.append("        return ResponseEntity\n");
        sb.append("                 .ok()\n");
        sb.append("                 .body(ItemsResponse.<").append(pascalDomain).append("SearchResponseRecord>builder()\n");
        sb.append("                         .status(\"OK\")\n");
        sb.append("                         .message(\"데이터 목록을 조회하는데 성공하였습니다.\")\n");
        sb.append("                         .totalSize((long) list.size())\n");
        sb.append("                         .items(list)\n");
        sb.append("                         .build());\n");
        sb.append("    }\n\n");

        // Detail Search
        if (useSwagger) {
            sb.append("    @Operation(summary = \"").append(pascalDomain).append(" 상세 조회\", description = \"")
                    .append(pascalDomain).append(" 상세 정보를 조회합니다.\")\n");
        }
        sb.append("    @GetMapping(\"/").append(domain).append("s/{id}\")\n");
        sb.append("    public ResponseEntity<ItemResponse<").append(pascalDomain)
                .append("SearchResponseRecord>> searchDetail(\n            @PathVariable ")
                .append("String id) {\n");
        sb.append("        ").append(pascalDomain).append("SearchResponseRecord data = ")
                .append(pascalDomain).append("Service.searchDetail(id);\n");
        sb.append("        return ResponseEntity\n");
        sb.append("                 .ok()\n");
        sb.append("                 .body(ItemResponse.<").append(pascalDomain).append("SearchResponseRecord>builder()\n");
        sb.append("                         .status(\"OK\")\n");
        sb.append("                         .message(\"상세 데이터를 조회하는데 성공하였습니다.\")\n");
        sb.append("                         .item(data)\n");
        sb.append("                         .build());\n");
        sb.append("    }\n\n");

        // Grid Search
        if (useSwagger) {
            sb.append("    @Operation(summary = \"").append(pascalDomain).append(" 그리드 목록 조회\", description = \"")
                    .append(pascalDomain).append(" 그리드 목록 정보를 조회합니다.\")\n");
        }
        sb.append("    @GetMapping(\"/").append(domain).append("s/grid\")\n");
        sb.append("    public ResponseEntity<GridResponse<").append(pascalDomain)
                .append("SearchResponseRecord>> searchGrid(\n            @ModelAttribute @Valid ")
                .append(pascalDomain).append("SearchRequestRecord request,\n")
                .append("            @ParameterObject @PageableDefault(size = 10) Pageable pageable) {\n");
        sb.append("        Page<").append(pascalDomain).append("SearchResponseRecord> page = ")
                .append(domain).append("Service.searchGrid(request, pageable);\n");
        sb.append("        return ResponseEntity\n");
        sb.append("                 .ok()\n");
        sb.append("                 .body(GridResponse.<").append(pascalDomain).append("SearchResponseRecord>builder()\n");
        sb.append("                         .status(\"OK\")\n");
        sb.append("                         .message(\"그리드 목록 데이터를 조회하는데 성공하였습니다.\")\n");
        sb.append("                         .totalSize(page.getTotalElements())\n");
        sb.append("                         .totalPageSize(page.getTotalPages())\n");
        sb.append("                         .size(page.getNumberOfElements())\n");
        sb.append("                         .items(page.getContent())\n");
        sb.append("                         .build());\n");
        sb.append("    }\n");

        sb.append("}\n");


        return sb.toString();
    }
}

