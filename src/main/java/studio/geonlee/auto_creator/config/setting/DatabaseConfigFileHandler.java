package studio.geonlee.auto_creator.config.setting;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import lombok.extern.slf4j.Slf4j;
import studio.geonlee.auto_creator.common.enumeration.LogType;
import studio.geonlee.auto_creator.config.dto.DatabaseConfig;
import studio.geonlee.auto_creator.ui.frame.MainFrame;

import java.io.File;
import java.io.IOException;

/**
 * @author GEON
 * @since 2025-04-25
 **/
@Slf4j
public class DatabaseConfigFileHandler {

    private static final ObjectMapper mapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .defaultDateFormat(new StdDateFormat())
            .build();

    public DatabaseConfig load() throws IOException {
        try {
            File databaseConfigFile = ConfigPathHelper.getUserDatabaseConfigFile();
            return mapper.readValue(databaseConfigFile, DatabaseConfig.class);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
//            MainFrame.log("❌ 데이터베이스 설정 파일 로딩 실패: " + e.getMessage(), LogType.EXCEPTION);
            throw e;
        }
    }

    public void save(DatabaseConfig config) {
        try {
            File userFile = ConfigPathHelper.getUserDatabaseConfigFile();
            mapper.writeValue(userFile, config);
        } catch (IOException e) {
            MainFrame.log("❌ 데이터베이스 설정 파일 저장 실패: " + e.getMessage(), LogType.EXCEPTION);
        }
    }
}
