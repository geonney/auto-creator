package studio.geonlee.auto_creator;

import com.formdev.flatlaf.FlatDarculaLaf;
import lombok.extern.slf4j.Slf4j;
import studio.geonlee.auto_creator.config.setting.DatabaseConfigFileHandler;
import studio.geonlee.auto_creator.config.setting.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.setting.DictionaryFileHandler;
import studio.geonlee.auto_creator.config.setting.GlobalConfig;
import studio.geonlee.auto_creator.ui.frame.MainFrame;

import javax.swing.*;
import java.io.IOException;
import java.util.Objects;

@Slf4j
public class AutoCreatorApplication {

    public static void main(String[] args) {

        // 테마 적용
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());

            log.info("The application has been successfully run.");
        } catch (Exception e) {
            log.error("FlatLaf 테마 적용 실패");
        }

        JWindow splash = new JWindow();
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(
                AutoCreatorApplication.class.getResource("/icon/splash_screen.png"))); // 아이콘 경로 맞게
        JLabel splashLabel = new JLabel(icon); // 이미지 경로
        splash.getContentPane().add(splashLabel);
        splash.setSize(450, 300); // 이미지 크기에 맞게 조절
        splash.setLocationRelativeTo(null); // 중앙에 표시
        splash.setVisible(true);

        // 2. 로딩 작업 (예: 초기화, DB 연결 등)
        // 백그라운드에서 로딩하기 (EDT 방지)
        SwingUtilities.invokeLater(() -> {
            try {
                //설정 파일 로드
                DatabaseConfigFileHandler databaseConfigFileHandler = new DatabaseConfigFileHandler();
                DefaultConfigFileHandler defaultConfigFileHandler = new DefaultConfigFileHandler();
                DictionaryFileHandler dictionaryFileHandler = new DictionaryFileHandler();
                GlobalConfig.defaultConfig = defaultConfigFileHandler.load();
                GlobalConfig.databaseConfig = databaseConfigFileHandler.load();
                GlobalConfig.dictionary = dictionaryFileHandler.load();
                Thread.sleep(2000); // 예시로 3초 대기
                // 여기에 초기화 로직 넣기
            } catch (InterruptedException | IOException e) {
                log.error("Setting file load failure.");
                System.exit(1);
            }

            // 3. 스플래시 닫고 메인 프레임 띄우기
            splash.setVisible(false);
            splash.dispose();

            SwingUtilities.invokeLater(MainFrame::new); //MainFrame 실행
        });
    }

}
