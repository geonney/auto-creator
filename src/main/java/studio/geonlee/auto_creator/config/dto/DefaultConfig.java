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
    private String defaultPackageName;
    private String entityBasePackage;
    private String recordBasePackage;
    private String defaultSavePath;
    private String theme;
    private boolean autoLoadDatabaseOnStart;
    private boolean useSwagger;
}
