package studio.geonlee.auto_creator.ui.panel;

import studio.geonlee.auto_creator.common.enumeration.LogType;
import studio.geonlee.auto_creator.common.util.CaseUtils;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;
import studio.geonlee.auto_creator.config.message.MessageUtil;
import studio.geonlee.auto_creator.config.setting.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.setting.GlobalConfig;
import studio.geonlee.auto_creator.ui.frame.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

/**
 * @author GEON
 * @since 2025-04-28
 **/
public class GeneratorSettingPanel extends JPanel {

    private final JComboBox<String> architectureComboBox;
    private final JComboBox<String> ormComboBox;
    private final JCheckBox useSwaggerCheck = new JCheckBox(MessageUtil.get("checkbox.swagger.annotation"));
    private final JCheckBox useMapStructCheck = new JCheckBox(MessageUtil.get("checkbox.mapStruct"));
    private final JCheckBox useQueryDslCheck = new JCheckBox(MessageUtil.get("checkbox.querydsl"));
    private final JCheckBox useBaseEntityCheck = new JCheckBox(MessageUtil.get("checkbox.baseEntity"));
    private final JTextField baseEntityColumnField = new JTextField(30);
    private final JPanel baseEntityFieldsPanel;
    private final MainFrame mainFrame;

    public GeneratorSettingPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] languages = {"Layered", "Hexagonal"};
        architectureComboBox = new JComboBox<>(languages);
        architectureComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        String[] orm = {"JPA", "Mybatis"};
        ormComboBox = new JComboBox<>(orm);
        ormComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        // 항목 추가
        add(createSection(
                MessageUtil.get("architecture.setting"),
                MessageUtil.get("architecture.setting.description"),
                architectureComboBox
        ));

        add(createSection(
                MessageUtil.get("orm.setting"),
                MessageUtil.get("orm.setting.description"),
                ormComboBox
        ));

        // swagger 사용 여부 체크박스
        useSwaggerCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(useSwaggerCheck);

        // mapstruct 사용 여부 체크박스
        useMapStructCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(useMapStructCheck);

        // querydsl 사용 여부 체크박스
        useQueryDslCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(useQueryDslCheck);

        // JPA BaseEntity 사용 여부 체크박스
        // 항목 추가
        useBaseEntityCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        useBaseEntityCheck.setVisible(false);
        add(useBaseEntityCheck);
        baseEntityColumnField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        baseEntityFieldsPanel = createSection(
                MessageUtil.get("baseEntity.setting"),
                MessageUtil.get("baseEntity.setting.description"),
                baseEntityColumnField
        );
        add(Box.createVerticalStrut(5));
        add(baseEntityFieldsPanel);

        // ORM 콤보박스 변경 시 BaseEntity 설정 show
        ormComboBox.addActionListener(e -> {
            String selected = (String) ormComboBox.getSelectedItem();
            useBaseEntityCheck.setVisible("JPA".equals(selected));
            baseEntityFieldsPanel.setVisible("JPA".equals(selected));
        });
        useBaseEntityCheck.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                baseEntityColumnField.setEnabled(true);
            } else {
                baseEntityColumnField.setText("");
                baseEntityColumnField.setEnabled(false);
            }
        });

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

    private void loadSettings() {
        DefaultConfig config = GlobalConfig.defaultConfig;
        if (config != null) {
            architectureComboBox.setSelectedItem(CaseUtils.toUppercaseFirstLetter(config.getArchitecture().toLowerCase()));
            ormComboBox.setSelectedItem(("jpa".equals(config.getOrm())
                    ? config.getOrm().toUpperCase()
                    : CaseUtils.toUppercaseFirstLetter(config.getOrm())));
            useSwaggerCheck.setSelected(config.isUseSwagger());
            useMapStructCheck.setSelected(config.isUseMapStruct());
            useQueryDslCheck.setSelected(config.isUseQueryDsl());
            useBaseEntityCheck.setSelected(config.isUseBaseEntity());
            baseEntityColumnField.setText(config.getBaseEntityColumnField());
            if ("JPA".equals(ormComboBox.getSelectedItem())) {
                useBaseEntityCheck.setVisible(true);
                baseEntityFieldsPanel.setVisible(true);
            }
        }
    }

    private void saveSettings() {
        try {
            DefaultConfig config = GlobalConfig.defaultConfig;
            if (config == null) config = new DefaultConfig();

            config.setArchitecture(String.valueOf(architectureComboBox.getSelectedItem()).toLowerCase());
            config.setOrm(String.valueOf(ormComboBox.getSelectedItem()).toLowerCase());
            config.setUseSwagger(useSwaggerCheck.isSelected());
            config.setUseMapStruct(useMapStructCheck.isSelected());
            config.setUseQueryDsl(useQueryDslCheck.isSelected());
            config.setUseBaseEntity(useBaseEntityCheck.isSelected());
            config.setBaseEntityColumnField(baseEntityColumnField.getText());
            DefaultConfigFileHandler defaultConfigFileHandler = new DefaultConfigFileHandler();
            defaultConfigFileHandler.save(config);

            MainFrame.log(MessageUtil.get("setting.save.success"), LogType.INFO);
            JOptionPane.showMessageDialog(this,
                    MessageUtil.get("setting.save.success") + ".",
                    "Information", JOptionPane.INFORMATION_MESSAGE);

            mainFrame.refreshCodeGeneratorPanel();
        } catch (Exception ex) {
            MainFrame.log(MessageUtil.get("setting.save.failure") + ": " + ex.getMessage(),
                    LogType.EXCEPTION);
            JOptionPane.showMessageDialog(this,
                    MessageUtil.get("setting.save.failure") + "\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
