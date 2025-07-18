package studio.geonlee.auto_creator.ui.panel;

import studio.geonlee.auto_creator.config.message.MessageUtil;
import studio.geonlee.auto_creator.config.setting.DictionaryFileHandler;
import studio.geonlee.auto_creator.config.setting.GlobalConfig;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DictionaryPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    public DictionaryPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        tableModel = new DefaultTableModel(new Object[]{
                MessageUtil.get("dictionary.column.name"),
                MessageUtil.get("dictionary.variable.name")},
                0);
        table = new JTable(tableModel);

        // 테이블 얼룩말 패턴 적용 (default가 눈에 잘 안보임)
        DefaultTableCellRenderer darkZebraRenderer = new DefaultTableCellRenderer() {
            private final Color darkGray = new Color(60, 63, 65);
            private final Color mediumGray = new Color(77, 77, 77);
            private final Color selectionColor = UIManager.getColor("Table.selectionBackground");

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(selectionColor);
                } else {
                    c.setBackground(row % 2 == 0 ? mediumGray : darkGray);
                }

                return c;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(darkZebraRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);

        add(createSection(
                MessageUtil.get("dictionary.setting"),
                MessageUtil.get("dictionary.setting.description"),
                scrollPane
        ));

        loadTableData();

        // 하단 행 추가 버튼
        JPanel bottomPanel = new JPanel();
        JButton addRowButton = new JButton(MessageUtil.get("button.dictionary.row.add"));
        addRowButton.addActionListener(e -> {
            tableModel.addRow(new Object[]{"", ""}); // 새 빈 행 추가
            int lastRow = tableModel.getRowCount() - 1;

            // 첫 번째 셀 선택 및 수정 모드로 전환
            table.changeSelection(lastRow, 0, false, false); // 행, 열, 확장선택, 토글
            table.editCellAt(lastRow, 0);
            Component editor = table.getEditorComponent();
            if (editor != null) {
                editor.requestFocusInWindow();
            }
        });

        JButton saveButton = new JButton(MessageUtil.get("button.save"));
        saveButton.addActionListener(e -> {
            saveDictionary();
        });

        bottomPanel.add(addRowButton);
        bottomPanel.add(saveButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
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

    private void loadTableData() {
        GlobalConfig.dictionary.forEach((abbr, full) -> tableModel.addRow(new Object[]{abbr, full}));
    }

    private void saveDictionary() {
        GlobalConfig.dictionary = new HashMap<>();
        Map<String, String> updatedDictionary = new LinkedHashMap<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String key = String.valueOf(tableModel.getValueAt(i, 0)).trim();
            String value = String.valueOf(tableModel.getValueAt(i, 1)).trim();
            if (!key.isEmpty() && !value.isEmpty()) {
                updatedDictionary.put(key, value.trim());
            }
        }
        GlobalConfig.dictionary = updatedDictionary;
        DictionaryFileHandler dictionaryFileHandler = new DictionaryFileHandler();
        dictionaryFileHandler.save();
        JOptionPane.showMessageDialog(this,
                MessageUtil.get("dictionary.save.success") + ".",
                "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}
