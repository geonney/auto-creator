package studio.geonlee.auto_creator.ui.panel;

import com.sun.tools.javac.Main;
import studio.geonlee.auto_creator.common.util.CaseUtils;
import studio.geonlee.auto_creator.config.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;
import studio.geonlee.auto_creator.config.message.MessageUtil;
import studio.geonlee.auto_creator.ui.frame.MainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * @author GEON
 * @since 2025-04-28
 **/
public class GeneratorSettingPanel extends JPanel {

    private final JComboBox<String> architectureComboBox;
    private final JComboBox<String> ormComboBox;
    private final JCheckBox useSwaggerCheck = new JCheckBox(MessageUtil.get("checkbox.swagger.annotation"));
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
        add(useSwaggerCheck); // ✅ 여기에 추가
        add(Box.createVerticalStrut(20));

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
        DefaultConfig config = DefaultConfigFileHandler.load();
        if (config != null) {
            architectureComboBox.setSelectedItem(config.getArchitecture().toUpperCase());
            ormComboBox.setSelectedItem(("jpa".equals(config.getOrm())
                    ? config.getOrm().toUpperCase()
                    : CaseUtils.toUppercaseFirstLetter(config.getOrm())));
            useSwaggerCheck.setSelected(config.isUseSwagger());
        }
    }

    private void saveSettings() {
        try {
            DefaultConfig config = DefaultConfigFileHandler.load();
            if (config == null) config = new DefaultConfig();

            config.setArchitecture(String.valueOf(architectureComboBox.getSelectedItem()).toLowerCase());
            config.setOrm(String.valueOf(ormComboBox.getSelectedItem()).toLowerCase());
            config.setUseSwagger(useSwaggerCheck.isSelected());

            DefaultConfigFileHandler.save(config);

            MainFrame.log(MessageUtil.get("setting.save.success"));
            JOptionPane.showMessageDialog(this,
                    MessageUtil.get("setting.save.success") + ".",
                    "Information", JOptionPane.INFORMATION_MESSAGE);

            mainFrame.refreshCodeGeneratorPanel();
        } catch (Exception ex) {
            MainFrame.log(MessageUtil.get("setting.save.failure") + ": " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    MessageUtil.get("setting.save.failure") + "\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
