package studio.geonlee.auto_creator.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import studio.geonlee.auto_creator.config.dto.DatabaseConfig;
import studio.geonlee.auto_creator.ui.frame.MainFrame;

import java.io.File;
import java.io.IOException;

/**
 * @author GEON
 * @since 2025-04-25
 **/
public class DatabaseConfigFileHandler {

    private static final ObjectMapper mapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .defaultDateFormat(new StdDateFormat())
            .build();

    public static DatabaseConfig load() {
        try {
            File userFile = ConfigPathHelper.getUserDatabaseConfigFile();
            if (userFile.exists()) {
                return mapper.readValue(userFile, DatabaseConfig.class);
            }
            return mapper.readValue(ConfigPathHelper.getInternalDatabaseConfigFile(), DatabaseConfig.class);
        } catch (IOException e) {
            MainFrame.log("❌ 데이터베이스 설정 파일 로딩 실패: " + e.getMessage());
            return null;
        }
    }

    public static void save(DatabaseConfig config) {
        try {
            File userFile = ConfigPathHelper.getUserDatabaseConfigFile();
            userFile.getParentFile().mkdirs();
            mapper.writeValue(userFile, config);
        } catch (IOException e) {
            MainFrame.log("❌ 데이터베이스 설정 파일 저장 실패: " + e.getMessage());
        }
    }
}
