package studio.geonlee.auto_creator.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import lombok.extern.slf4j.Slf4j;
import studio.geonlee.auto_creator.common.enumeration.LogType;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;
import studio.geonlee.auto_creator.ui.frame.MainFrame;

import java.io.File;
import java.io.IOException;

/**
 * @author GEON
 * @since 2025-04-26
 **/
@Slf4j
public class DefaultConfigFileHandler {

    private static final ObjectMapper mapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)    // ✅ pretty print
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // ✅ 확장성
            .defaultDateFormat(new StdDateFormat())         // ✅ ISO 표준 날짜 포맷
            .build();

    public static DefaultConfig load() {
        try {
            File defaultConfigFile = ConfigPathHelper.getUserDefaultConfigFile();
            return mapper.readValue(defaultConfigFile, DefaultConfig.class);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            MainFrame.log("❌ 기본 설정 파일 로딩 실패: " + e.getMessage(), LogType.EXCEPTION);
            return new DefaultConfig();
        }
    }

    public static void save(DefaultConfig config) {
        try {
            File userFile = ConfigPathHelper.getUserDefaultConfigFile();
            mapper.writeValue(userFile, config);
        } catch (IOException e) {
            MainFrame.log("❌ 기본 설정 파일 저장 실패: " + e.getMessage(), LogType.EXCEPTION);
        }
    }
}
