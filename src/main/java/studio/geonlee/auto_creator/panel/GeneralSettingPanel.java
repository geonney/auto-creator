package studio.geonlee.auto_creator.panel;

import studio.geonlee.auto_creator.config.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;
import studio.geonlee.auto_creator.frame.MainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * @author GEON
 * @since 2025-04-26
 **/
public class GeneralSettingPanel extends JPanel {

    private final JTextField entityPackageField = new JTextField(30);
    private final JTextField recordPackageField = new JTextField(30);
    private final JTextField savePathField = new JTextField(30);
    private final JLabel themeLabel = new JLabel();
    private final JCheckBox autoLoadDatabaseCheck = new JCheckBox("프로그램 시작 시 마지막 DB 연결 복원");
    private final JCheckBox useSwaggerCheck = new JCheckBox("Swagger 관련 어노테이션 자동 포함");


    public GeneralSettingPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        // 입력 필드 크기 제한
        entityPackageField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        recordPackageField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        savePathField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        // 항목 추가
        add(createSection(
                "기본 Entity 패키지명",
                "Entity 클래스를 생성할 기본 패키지입니다.",
                entityPackageField
        ));

        add(createSection(
                "기본 Record 패키지명",
                "Record 클래스를 생성할 기본 패키지입니다. (도메인별 서브 패키지가 추가됩니다)",
                recordPackageField
        ));

        add(createSection(
                "기본 저장 경로",
                "파일로 저장할 때 기본으로 사용할 디렉토리 경로입니다.",
                savePathField,
                createBrowseButton()
        ));

        add(createSection(
                "현재 테마",
                "현재 적용된 UI 테마입니다. (변경은 추후 제공 예정)",
                themeLabel
        ));

        // DB 연결 복원 체크박스
        autoLoadDatabaseCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(Box.createVerticalStrut(10));
        add(autoLoadDatabaseCheck);

        // swagger 사용 여부 체크박스
        useSwaggerCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(useSwaggerCheck); // ✅ 여기에 추가
        add(Box.createVerticalStrut(20));

        JButton saveBtn = new JButton("저장");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> saveSettings());

        add(Box.createVerticalStrut(20));
        add(saveBtn);
        add(Box.createVerticalGlue()); // 하단 여백 채움

        loadSettings();
    }

    private JPanel createSection(String titleText, String description, JComponent input) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setOpaque(false);

        JLabel title = new JLabel(titleText);
        title.setFont(title.getFont().deriveFont(Font.BOLD));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel desc = new JLabel(description);
        desc.setFont(desc.getFont().deriveFont(Font.PLAIN, 11f));
        desc.setForeground(Color.GRAY);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        input.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(desc);
        panel.add(Box.createVerticalStrut(5));
        panel.add(input);
        panel.add(Box.createVerticalStrut(15));

        return panel;
    }

    private JPanel createSection(String titleText, String description, JComponent input, JButton button) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.X_AXIS));
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldPanel.setOpaque(false);

        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        input.setAlignmentY(Component.CENTER_ALIGNMENT);
        button.setAlignmentY(Component.CENTER_ALIGNMENT);

        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28)); // ✅ 추가

        fieldPanel.add(input);
        fieldPanel.add(Box.createHorizontalStrut(8));
        fieldPanel.add(button);

        return createSection(titleText, description, fieldPanel);
    }

    private JButton createBrowseButton() {
        JButton btn = new JButton("찾아보기");
        btn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                savePathField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        return btn;
    }

    private void loadSettings() {
        DefaultConfig config = DefaultConfigFileHandler.load();
        if (config != null) {
            entityPackageField.setText(config.getEntityBasePackage());
            recordPackageField.setText(config.getRecordBasePackage());
            savePathField.setText(config.getDefaultSavePath());
            themeLabel.setText(config.getTheme());
            autoLoadDatabaseCheck.setSelected(config.isAutoLoadDatabaseOnStart());
            useSwaggerCheck.setSelected(config.isUseSwagger());
        }
    }

    private void saveSettings() {
        try {
            DefaultConfig config = DefaultConfigFileHandler.load();
            if (config == null) config = new DefaultConfig();

            config.setEntityBasePackage(entityPackageField.getText().trim());
            config.setRecordBasePackage(recordPackageField.getText().trim());
            config.setDefaultSavePath(savePathField.getText().trim());
            config.setAutoLoadDatabaseOnStart(autoLoadDatabaseCheck.isSelected());
            config.setUseSwagger(useSwaggerCheck.isSelected());
            DefaultConfigFileHandler.save(config);

            MainFrame.log("✅ General Settings 저장 완료");
            JOptionPane.showMessageDialog(this, "설정이 저장되었습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            MainFrame.log("❌ General Settings 저장 실패: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "설정 저장 실패\n" + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}
