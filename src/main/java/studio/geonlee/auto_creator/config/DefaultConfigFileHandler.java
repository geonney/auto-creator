package studio.geonlee.auto_creator.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;
import studio.geonlee.auto_creator.ui.frame.MainFrame;

import java.io.File;
import java.io.IOException;

/**
 * @author GEON
 * @since 2025-04-26
 **/
public class DefaultConfigFileHandler {

    private static final ObjectMapper mapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)    // ✅ pretty print
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // ✅ 확장성
            .defaultDateFormat(new StdDateFormat())         // ✅ ISO 표준 날짜 포맷
            .build();

    public static DefaultConfig load() {
        try {
            File userFile = ConfigPathHelper.getUserDefaultConfigFile();
            if (userFile.exists()) {
                return mapper.readValue(userFile, DefaultConfig.class);
            }
            return mapper.readValue(ConfigPathHelper.getInternalDefaultConfigFile(), DefaultConfig.class);
        } catch (IOException e) {
            MainFrame.log("❌ 기본 설정 파일 로딩 실패: " + e.getMessage());
            return new DefaultConfig();
        }
    }

    public static void save(DefaultConfig config) {
        try {
            File userFile = ConfigPathHelper.getUserDefaultConfigFile();
            userFile.getParentFile().mkdirs(); // 폴더 없으면 생성
            mapper.writeValue(userFile, config);
        } catch (IOException e) {
            MainFrame.log("❌ 기본 설정 파일 저장 실패: " + e.getMessage());
        }
    }
}
