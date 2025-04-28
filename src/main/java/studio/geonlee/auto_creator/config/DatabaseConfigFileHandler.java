package studio.geonlee.auto_creator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import studio.geonlee.auto_creator.config.dto.DatabaseConfig;
import studio.geonlee.auto_creator.ui.frame.MainFrame;

import java.io.File;

/**
 * @author GEON
 * @since 2025-04-25
 **/
public class DatabaseConfigFileHandler {
    private static final File FILE = new File("config/database-config.json");
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void save(DatabaseConfig config) {
        try {
            FILE.getParentFile().mkdirs(); // config/ 디렉토리 생성
            mapper.writerWithDefaultPrettyPrinter().writeValue(FILE, config);
        } catch (Exception e) {
            MainFrame.log("❌ [database-config.json] ⚠️ Setting Save Failure: " + e.getMessage());
        }
    }

    public static DatabaseConfig load() {
        try {
            if (FILE.exists()) {
                return mapper.readValue(FILE, DatabaseConfig.class);
            }
        } catch (Exception e) {
            MainFrame.log("❌ [database-config.json] ⚠️ Setting Save Failure: " + e.getMessage());
        }
        return null;
    }
}
