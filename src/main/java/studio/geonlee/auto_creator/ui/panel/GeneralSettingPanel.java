package studio.geonlee.auto_creator.ui.panel;

import studio.geonlee.auto_creator.common.enumeration.LogType;
import studio.geonlee.auto_creator.config.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;
import studio.geonlee.auto_creator.config.message.MessageUtil;
import studio.geonlee.auto_creator.ui.frame.MainFrame;

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
    //프로그램 시작시 마지막 DB 연결의 복원
    private final JCheckBox autoLoadDatabaseCheck = new JCheckBox(MessageUtil.get("checkbox.reload.last.database"));
    private final JCheckBox expandTreeCheck = new JCheckBox(MessageUtil.get("checkbox.expand.tree"));
    private final JComboBox<String> languageComboBox;

    public GeneralSettingPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        // 입력 필드 크기 제한
        entityPackageField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        recordPackageField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        savePathField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));


        String[] languages = {"Korean", "English"};
        languageComboBox = new JComboBox<>(languages);
        languageComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));


        // 항목 추가
        add(createSection(
                MessageUtil.get("entity.base.package.title"),
                MessageUtil.get("entity.base.package.description"),
                entityPackageField
        ));

        add(createSection(
                MessageUtil.get("domain.base.package.title"),
                MessageUtil.get("domain.base.package.description"),
                recordPackageField
        ));

        add(createSection(
                MessageUtil.get("save.file.path"),
                MessageUtil.get("save.file.path.description"),
                savePathField,
                createBrowseButton()
        ));

        add(createSection(
                MessageUtil.get("language.setting"),
                MessageUtil.get("language.setting.description"),
                languageComboBox
        ));

        add(createSection(
                MessageUtil.get("theme.setting"),
                MessageUtil.get("theme.setting.description"),
                themeLabel
        ));

        // DB 연결 복원 체크박스
        autoLoadDatabaseCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(Box.createVerticalStrut(10));
        add(autoLoadDatabaseCheck);

        //트리 확장 체크박스
        expandTreeCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(Box.createVerticalStrut(10));
        add(expandTreeCheck);

        JButton saveBtn = new JButton(MessageUtil.get("button.save"));
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
        JButton btn = new JButton(MessageUtil.get("button.search"));
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
            recordPackageField.setText(config.getDomainBasePackage());
            savePathField.setText(config.getDefaultSavePath());
            themeLabel.setText(config.getTheme());
            autoLoadDatabaseCheck.setSelected(config.isAutoLoadDatabaseOnStart());
            expandTreeCheck.setSelected(config.isExpandTree());
            languageComboBox.setSelectedItem(config.getLanguage().equalsIgnoreCase("ko") ? "Korean" : "English");
        }
    }

    private void saveSettings() {
        try {
            DefaultConfig config = DefaultConfigFileHandler.load();
            if (config == null) config = new DefaultConfig();

            config.setEntityBasePackage(entityPackageField.getText().trim());
            config.setDomainBasePackage(recordPackageField.getText().trim());
            config.setDefaultSavePath(savePathField.getText().trim());
            config.setAutoLoadDatabaseOnStart(autoLoadDatabaseCheck.isSelected());
            config.setExpandTree(expandTreeCheck.isSelected());

            String selected = (String) languageComboBox.getSelectedItem();
            String lang = "en";
            if ("Korean".equals(selected)) {
                lang = "ko";
            }
            config.setLanguage(lang);
//            MessageUtil.loadBundle(lang); // ✅ 언어 변경 즉시 적용

            DefaultConfigFileHandler.save(config);

            MainFrame.log(MessageUtil.get("setting.save.success"), LogType.INFO);
            JOptionPane.showMessageDialog(this,
                    MessageUtil.get("setting.save.success") + ".",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            MainFrame.log(MessageUtil.get("setting.save.failure") + ": " + ex.getMessage(),
                    LogType.EXCEPTION);
            JOptionPane.showMessageDialog(this,
                    MessageUtil.get("setting.save.failure") + "\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
