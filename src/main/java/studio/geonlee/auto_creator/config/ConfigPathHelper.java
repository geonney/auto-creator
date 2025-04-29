package studio.geonlee.auto_creator.config;

import java.io.File;

/**
 * @author GEON
 * @since 2025-04-29
 **/
public class ConfigPathHelper {

    public static File getUserDefaultConfigFile() {
        String userHome = System.getProperty("user.home");
        return new File(userHome, ".auto-code/default-config.json");
    }

    public static File getUserDatabaseConfigFile() {
        String userHome = System.getProperty("user.home");
        return new File(userHome, ".auto-code/database-config.json");
    }

    public static File getInternalDefaultConfigFile() {
        return new File("config/default-config.json");
    }

    public static File getInternalDatabaseConfigFile() {
        return new File("config/database-config.json");
    }
}
