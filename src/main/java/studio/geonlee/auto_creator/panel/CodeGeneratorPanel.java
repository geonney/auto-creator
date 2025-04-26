package studio.geonlee.auto_creator.panel;

import studio.geonlee.auto_creator.common.enumeration.CodeType;
import studio.geonlee.auto_creator.common.enumeration.DatabaseType;
import studio.geonlee.auto_creator.context.DatabaseContext;
import studio.geonlee.auto_creator.frame.MainFrame;
import studio.geonlee.auto_creator.generator.EntityCodeGenerator;
import studio.geonlee.auto_creator.generator.RecordGenerator;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author GEON
 * @since 2025-04-25
 **/
public class CodeGeneratorPanel extends JPanel {

    private final JTextField classNameField = new JTextField(20);
    private final JTextArea previewArea = new JTextArea();
    private CodeType currentCodeType;

    public CodeGeneratorPanel(MainFrame mainFrame) {
        setLayout(new BorderLayout());

        // ✅ 상단 버튼 패널
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Entity 이름:"));
        topPanel.add(classNameField);
        JButton entityCreateButton = new JButton("Entity 생성");
        topPanel.add(entityCreateButton);
        JButton recordBtn = new JButton("Record 생성");
        topPanel.add(recordBtn);
        add(topPanel, BorderLayout.NORTH);

        // ✅ 코드 미리보기 영역
        previewArea.setFont(new Font("monospaced", Font.PLAIN, 13));
        previewArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(previewArea);
        add(scrollPane, BorderLayout.CENTER);

        // ✅ 하단 저장 버튼 패널
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton copyButton = new JButton("📋 복사");
        JButton downloadButton = new JButton("💾 저장");
        bottomPanel.add(copyButton);
        bottomPanel.add(downloadButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // ✅ Entity 생성 버튼 로직
        entityCreateButton.addActionListener(e -> {
            String className = classNameField.getText().trim();
            if (className.isEmpty()) {
                JOptionPane.showMessageDialog(this, "클래스 이름을 입력하세요.", "입력 필요", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String tableName = mainFrame.getSelectedTable();
            String schema = mainFrame.getSelectedSchema();
            DatabaseType dbType = DatabaseContext.getDatabaseType();

            if (tableName == null || schema == null) {
                JOptionPane.showMessageDialog(this, "테이블을 먼저 선택하세요.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String code = EntityCodeGenerator.generate(className, tableName, schema, dbType);
            previewArea.setText(code);

            currentCodeType = CodeType.ENTITY;
            mainFrame.log("✅ Entity 생성 완료: " + className + " (테이블: " + schema + "." + tableName + ")");
        });


        JPopupMenu recordMenu = new JPopupMenu();

        for (String type : java.util.List.of("Create", "Update", "Delete", "Search")) {
            JMenuItem item = new JMenuItem(type);
            item.addActionListener(e -> {
                String className = classNameField.getText().trim();
                if (className.isEmpty()) return;

                String code = RecordGenerator.generate(
                        type,
                        className,
                        mainFrame.getSelectedTable(),
                        mainFrame.getSelectedSchema(),
                        DatabaseContext.getDatabaseType()
                );

                previewArea.setText(code);
                currentCodeType = switch (type.toLowerCase()) {
                    case "create" -> CodeType.RECORD_CREATE;
                    case "update" -> CodeType.RECORD_UPDATE;
                    case "delete" -> CodeType.RECORD_DELETE;
                    case "search" -> CodeType.RECORD_SEARCH;
                    default -> null;
                };
                mainFrame.log("✅ Record (" + type + ") 생성 완료");
            });
            recordMenu.add(item);
        }

        recordBtn.addActionListener(e -> {
            recordMenu.show(recordBtn, 0, recordBtn.getHeight());
        });

        // ✅ 저장(download) 버튼 로직
        downloadButton.addActionListener(e -> {
            String defaultName = switch (currentCodeType) {
                case RECORD_CREATE -> classNameField.getText().trim() + "CreateRecord.java";
                case RECORD_UPDATE -> classNameField.getText().trim() + "UpdateRecord.java";
                case RECORD_DELETE -> classNameField.getText().trim() + "DeleteRecord.java";
                case RECORD_SEARCH -> classNameField.getText().trim() + "SearchRecord.java";
                default -> classNameField.getText().trim() + ".java";
            };
            String fileName = JOptionPane.showInputDialog(this, "파일명을 입력하세요:", defaultName);

            if (fileName == null || fileName.isBlank()) return;

            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new java.io.File(fileName));

            int result = chooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try (FileWriter writer = new FileWriter(chooser.getSelectedFile())) {
                    writer.write(defaultName);
                    mainFrame.log("📁 저장 완료: " + chooser.getSelectedFile().getAbsolutePath());
                } catch (IOException ex) {
                    mainFrame.log("❌ 파일 저장 실패: " + ex.getMessage());
                    JOptionPane.showMessageDialog(this, "파일 저장 중 오류 발생\n" + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // ✅ 복사 버튼 로직
        copyButton.addActionListener(e -> {
            String code = previewArea.getText().trim();
            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(this, "복사할 코드가 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(new java.awt.datatransfer.StringSelection(code), null);

            mainFrame.log("📋 코드 전체 복사됨");
        });
    }

    public void setClassNameFromTable(String tableName) {
        String entityName = toCamelCase(tableName);
        classNameField.setText(entityName);
    }

    private String toCamelCase(String name) {
        if (name == null || name.isEmpty()) return "";
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
