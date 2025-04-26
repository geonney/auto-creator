package studio.geonlee.auto_creator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;
import studio.geonlee.auto_creator.frame.MainFrame;

import java.io.File;
import java.io.IOException;

/**
 * @author GEON
 * @since 2025-04-26
 **/
public class DefaultConfigFileHandler {
    private static final String CONFIG_FILE = "config/default-config.json";

    public static DefaultConfig load() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(CONFIG_FILE);
            if (file.exists()) {
                return mapper.readValue(file, DefaultConfig.class);
            } else {
                DefaultConfig config = new DefaultConfig();
                save(config);
                return config;
            }
        } catch (IOException e) {
            MainFrame.log("❌ [default-config.json] Loading Failure: " + e.getMessage());
            return new DefaultConfig();
        }
    }

    public static void save(DefaultConfig config) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(CONFIG_FILE), config);
        } catch (IOException e) {
            MainFrame.log("❌ [default-config.json] Save Failure: " + e.getMessage());
        }
    }
}
