package studio.geonlee.auto_creator.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author GEON
 * @since 2025-04-25
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseConfig {
    private String databaseType;
    private String host;
    private int port;
    private String user;
    private String password;
    private String databaseName;
}