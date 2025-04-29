package studio.geonlee.auto_creator.config.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author GEON
 * @since 2025-04-26
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DefaultConfig {
    private String entityBasePackage;
    private String domainBasePackage;
    private String defaultSavePath;
    private String theme;
    private boolean autoLoadDatabaseOnStart;
    private boolean expandTree;
    private boolean useSwagger;
    private String language;
    private String architecture;
    private String orm;

    // ✅ 창 위치 및 크기 추가
    private int windowX = -1;
    private int windowY = -1;
    private int windowWidth = -1;
    private int windowHeight = -1;
}
