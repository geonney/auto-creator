package studio.geonlee.auto_creator.config.setting;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import lombok.extern.slf4j.Slf4j;
import studio.geonlee.auto_creator.common.enumeration.LogType;
import studio.geonlee.auto_creator.ui.frame.MainFrame;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author GEON
 * @since 2025-04-26
 **/
@Slf4j
public class DictionaryFileHandler {

    private static final ObjectMapper mapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)    // ✅ pretty print
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // ✅ 확장성
            .defaultDateFormat(new StdDateFormat())         // ✅ ISO 표준 날짜 포맷
            .build();

    public Map<String, String> load() {
        try {
            File dictionaryFile = ConfigPathHelper.getUserDictionaryFile();
            return mapper.readValue(dictionaryFile, mapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
//            MainFrame.log("❌ 사전 파일 로딩 실패: " + e.getMessage(), LogType.EXCEPTION);
            return new HashMap<>();
        }
    }

    public void save() {
        try {
            File userFile = ConfigPathHelper.getUserDictionaryFile();
            mapper.writeValue(userFile, GlobalConfig.dictionary);
        } catch (IOException e) {
            MainFrame.log("❌ 사전 파일 저장 실패: " + e.getMessage(), LogType.EXCEPTION);
        }
    }
}
