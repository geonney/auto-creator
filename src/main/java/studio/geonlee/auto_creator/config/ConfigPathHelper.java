package studio.geonlee.auto_creator.config;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @author GEON
 * @since 2025-04-29
 **/
@Slf4j
public class ConfigPathHelper {

    private static final String userHome = System.getProperty("user.home");
    private static final String settingPath = userHome + "/" + ".auto-code";

    public static File getUserDefaultConfigFile() {
        log.info("✅ 기본 설정 파일을 로드합니다.");
        return getSettingFile("default");
    }

    public static File getUserDatabaseConfigFile() {
        log.info("✅ 데이터베이스 설정 파일을 로드합니다.");
        return getSettingFile("database");
    }

    public static File getSettingFile(String type) {
        String settingFilename = type + "-config.json";
        File settingConfigFile = new File(settingPath + "/" + settingFilename);
        if (!settingConfigFile.exists()) {
            boolean result = settingConfigFile.getParentFile().mkdirs();
            log.info("✅{} 설정파일이 존재하지 않아 기본 설정 파일로 초기화 합니다.", type);
            if (result) log.info("{} 폴더를 생성하였습니다.", settingConfigFile.getParentFile().getAbsolutePath());
            try (InputStream in = ConfigPathHelper.class.getClassLoader()
                    .getResourceAsStream("config/" + settingFilename);
                 OutputStream out = new FileOutputStream(settingConfigFile)) {

                if (in == null) {
                    throw new FileNotFoundException(type + " 설정파일이 resources/config 경로에 없습니다.");
                }
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                log.info("설정파일을 생성했습니다.: {}", settingConfigFile.getAbsolutePath());
            } catch (IOException e) {
                log.error("설정 파일을 생성하는데 실패하였습니다.", e);
            }
        }
        return settingConfigFile;
    }
}
