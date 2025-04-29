package studio.geonlee.auto_creator.ui.panel;

import studio.geonlee.auto_creator.common.enumeration.CodeType;
import studio.geonlee.auto_creator.common.enumeration.DatabaseType;
import studio.geonlee.auto_creator.common.record.EntityMetadata;
import studio.geonlee.auto_creator.common.record.FieldMetadata;
import studio.geonlee.auto_creator.common.util.CaseUtils;
import studio.geonlee.auto_creator.common.util.DatabaseMetaReader;
import studio.geonlee.auto_creator.config.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;
import studio.geonlee.auto_creator.config.message.MessageUtil;
import studio.geonlee.auto_creator.context.DatabaseContext;
import studio.geonlee.auto_creator.generator.*;
import studio.geonlee.auto_creator.generator.layered.ControllerGenerator;
import studio.geonlee.auto_creator.generator.layered.RepositoryGenerator;
import studio.geonlee.auto_creator.generator.layered.ServiceImplGenerator;
import studio.geonlee.auto_creator.generator.layered.ServiceInterfaceGenerator;
import studio.geonlee.auto_creator.ui.frame.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author GEON
 * @since 2025-04-25
 **/
public class CodeGeneratorPanel extends JPanel {

    private final JTextField classNameField = new JTextField(20);
    private final JTextArea previewArea = new JTextArea();
    private CodeType currentCodeType;

    private final JButton entityButton;
    private final JButton recordButton;
    private final JButton serviceLogicButton;
    private final JButton queryButton;

    private final JPopupMenu serviceMenu = new JPopupMenu();  // ✅ serviceMenu를 필드로 변경
    private final JPopupMenu queryMenu = new JPopupMenu();  // ✅ serviceMenu를 필드로 변경
    private final MainFrame mainFrame; // ✅ mainFrame 필드로 저장

    public CodeGeneratorPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        // ✅ 상단 버튼 패널
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel(MessageUtil.get("title.entity.name") + ":"));
        topPanel.add(classNameField);

        entityButton = new JButton(MessageUtil.get("button.create.entity"));
        topPanel.add(entityButton);

        recordButton = new JButton(MessageUtil.get("button.create.record"));
        topPanel.add(recordButton);

        serviceLogicButton = new JButton(MessageUtil.get("button.create.service"));
        topPanel.add(serviceLogicButton);

        queryButton = new JButton(MessageUtil.get("button.create.query"));
        topPanel.add((queryButton));

        add(topPanel, BorderLayout.NORTH);

        // ✅ 코드 미리보기 영역
        previewArea.setFont(new Font("monospaced", Font.PLAIN, 13));
        previewArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(previewArea);
        add(scrollPane, BorderLayout.CENTER);

        // ✅ 하단 저장 버튼 패널
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton copyButton = new JButton(MessageUtil.get("button.copy"));
        JButton downloadButton = new JButton(MessageUtil.get("button.download"));
        bottomPanel.add(copyButton);
        bottomPanel.add(downloadButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // ✅ Entity 생성 버튼
        entityButton.addActionListener(e -> generateEntity());

        // ✅ Record 생성 버튼
        setupRecordButton();

        // ✅ Query 생성 버튼
        setupQueryButton();

        // ✅ Service Logic 생성 버튼
        serviceLogicButton.addActionListener(e -> {
            String tableName = mainFrame.getSelectedTable();
            if (tableName == null) {
                JOptionPane.showMessageDialog(this,
                        MessageUtil.get("choose.table.first"),
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            serviceMenu.show(serviceLogicButton, 0, serviceLogicButton.getHeight());
        });

        // ✅ Service Logic 생성 버튼
        queryButton.addActionListener(e -> {
            String tableName = mainFrame.getSelectedTable();
            if (tableName == null) {
                JOptionPane.showMessageDialog(this,
                        MessageUtil.get("choose.table.first"),
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            queryMenu.show(queryButton, 0, queryButton.getHeight());
        });

        // ✅ 다운로드 버튼
        downloadButton.addActionListener(e -> downloadCode());

        // ✅ 복사 버튼
        copyButton.addActionListener(e -> copyCode());

        // ✅ ORM에 따라 버튼/메뉴 가시성 초기 세팅
        refreshOrmSettings();
    }

    // ✅ Entity 코드 생성
    private void generateEntity() {
        String tableName = mainFrame.getSelectedTable();
        String schema = mainFrame.getSelectedSchema();
        DatabaseType dbType = DatabaseContext.getDatabaseType();

        if (tableName == null || schema == null) {
            JOptionPane.showMessageDialog(this, MessageUtil.get("choose.table.first"), "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String className = classNameField.getText().trim();
        if (className.isEmpty()) {
            JOptionPane.showMessageDialog(this, MessageUtil.get("enter.entity.name"), "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String code = EntityCodeGenerator.generate(className, tableName, schema, dbType);
        previewArea.setText(code);
        currentCodeType = CodeType.ENTITY;
        MainFrame.log(MessageUtil.get("entity.create.success") + ": " + className + " (Table: " + schema + "." + tableName + ")");
    }

    // ✅ Record 버튼 설정
    private void setupRecordButton() {
        JPopupMenu recordMenu = new JPopupMenu();

        List<String> recordTypes = List.of(
                "Create Request", "Create Response",
                "Update Request", "Update Response",
                "Delete Request", "Delete Response",
                "Search Request", "Search Response"
        );

        for (String type : recordTypes) {
            JMenuItem item = new JMenuItem(type);
            item.addActionListener(e -> {
                String className = classNameField.getText().trim();
                if (className.isEmpty()) return;

                String tableName = mainFrame.getSelectedTable();
                if (tableName == null) {
                    JOptionPane.showMessageDialog(this,
                            MessageUtil.get("choose.table.first"),
                            "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String schema = mainFrame.getSelectedSchema();
                DatabaseType dbType = DatabaseContext.getDatabaseType();

                // ✅ "Create Request" → "create-request" 형태로 변환
                String mode = type.toLowerCase().replace(" ", "-");

                String code = RecordGenerator.generate(
                        mode,
                        className,
                        tableName,
                        schema,
                        dbType
                );

                previewArea.setText(code);

                currentCodeType = switch (mode) {
                    case "create-request" -> CodeType.RECORD_CREATE_REQUEST;
                    case "create-response" -> CodeType.RECORD_CREATE_RESPONSE;
                    case "update-request" -> CodeType.RECORD_UPDATE_REQUEST;
                    case "update-response" -> CodeType.RECORD_UPDATE_RESPONSE;
                    case "delete-request" -> CodeType.RECORD_DELETE_REQUEST;
                    case "delete-response" -> CodeType.RECORD_DELETE_RESPONSE;
                    case "search-request" -> CodeType.RECORD_SEARCH_REQUEST;
                    case "search-response" -> CodeType.RECORD_SEARCH_RESPONSE;
                    default -> null;
                };

                MainFrame.log(MessageUtil.get("record.create.success") + ": " + className + " (" + type + ")");
            });
            recordMenu.add(item);
        }

        recordButton.addActionListener(e -> {
            String tableName = mainFrame.getSelectedTable();
            if (tableName == null) {
                JOptionPane.showMessageDialog(this,
                        MessageUtil.get("choose.table.first"),
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            recordMenu.show(recordButton, 0, recordButton.getHeight());
        });
    }

    private void setupQueryButton() {
        for (String type : List.of("Select", "Insert", "Update", "Delete")) {
            JMenuItem item = new JMenuItem(type);
            item.addActionListener(e -> {
                String className = classNameField.getText().trim();
                if (className.isEmpty()) return;

                String tableName = mainFrame.getSelectedTable();
                String schema = mainFrame.getSelectedSchema();
                DatabaseType databaseType = DatabaseContext.getDatabaseType();

                if (tableName == null || schema == null) {
                    JOptionPane.showMessageDialog(this, MessageUtil.get("choose.table.first"), "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                List<FieldMetadata> fields = DatabaseMetaReader.readTableFields(schema, tableName, databaseType);
                EntityMetadata entityMeta = EntityMetadata.of(schema, tableName, databaseType, fields);

                // ✅ Query 생성
                String code = QueryGenerator.generate(type, entityMeta);

                previewArea.setText(code);

                // 필요하면 CodeType에 QUERY_SELECT, QUERY_INSERT 등 추가
                MainFrame.log(MessageUtil.get("query.create.success") + ": " + className + " (" + type + ")");
            });
            queryMenu.add(item);
        }

        queryButton.addActionListener(e -> {
            String tableName = mainFrame.getSelectedTable();
            if (tableName == null) {
                JOptionPane.showMessageDialog(this,
                        MessageUtil.get("choose.table.first"),
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            queryMenu.show(queryButton, 0, queryButton.getHeight());
        });
    }

    // ✅ Service 코드 생성
    private void generateServiceCode(String type) {
        String className = classNameField.getText().trim();
        if (className.isEmpty()) return;

        String tableName = mainFrame.getSelectedTable();
        if (tableName == null || tableName.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    MessageUtil.get("choose.table.first"),
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String schema = mainFrame.getSelectedSchema();
        DatabaseType databaseType = DatabaseContext.getDatabaseType();
        List<FieldMetadata> fields = DatabaseMetaReader.readTableFields(schema, tableName, databaseType);

        EntityMetadata entityMeta = EntityMetadata.of(schema, tableName, databaseType, fields);

        String code = switch (type) {
            case "Controller" -> ControllerGenerator.generate(entityMeta);
            case "Service Interface" -> ServiceInterfaceGenerator.generate(entityMeta);
            case "Service Impl" -> ServiceImplGenerator.generate(entityMeta);
            case "Repository" -> RepositoryGenerator.generate(entityMeta);
            case "Mapper" -> MapperGenerator.generate(entityMeta);
            case "Query xml" -> MybatisXmlGenerator.generate(entityMeta);
            default -> "";
        };

        previewArea.setText(code);

        currentCodeType = switch (type) {
            case "Controller" -> CodeType.CONTROLLER;
            case "Service Interface" -> CodeType.SERVICE_INTERFACE;
            case "Service Impl" -> CodeType.SERVICE_IMPL;
            case "Repository" -> CodeType.REPOSITORY;
            case "Mapper" -> CodeType.MAPPER;
            case "Query xml" -> CodeType.XML;
            default -> null;
        };

        MainFrame.log(MessageUtil.get("service.create.success") + ": " + className + " (" + type + ")");
    }

    // ✅ ORM 변경 시 버튼/메뉴 업데이트
    public void refreshOrmSettings() {
        DefaultConfig config = DefaultConfigFileHandler.load();
        String orm = config.getOrm();

        boolean isJpa = "jpa".equalsIgnoreCase(orm);
        boolean isMybatis = "mybatis".equalsIgnoreCase(orm);

        entityButton.setVisible(isJpa);

        serviceMenu.removeAll();
        List<String> serviceTypes = new ArrayList<>(List.of("Controller", "Service Interface", "Service Impl"));
        if (isJpa) {
            serviceTypes.add("Repository");
        } else if (isMybatis) {
            serviceTypes.add("Mapper");
            serviceTypes.add("Query xml");
        }

        for (String type : serviceTypes) {
            JMenuItem item = new JMenuItem(type);
            item.addActionListener(e -> generateServiceCode(type));
            serviceMenu.add(item);
        }
    }

    // ✅ 코드 저장
    private void downloadCode() {
        String code = previewArea.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    MessageUtil.get("no.code.download"),
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String defaultName = switch (currentCodeType) {
            case RECORD_CREATE_REQUEST -> classNameField.getText().trim() + "CreateRequestRecord.java";
            case RECORD_CREATE_RESPONSE -> classNameField.getText().trim() + "CreateResponseRecord.java";
            case RECORD_UPDATE_REQUEST -> classNameField.getText().trim() + "UpdateRequestRecord.java";
            case RECORD_UPDATE_RESPONSE -> classNameField.getText().trim() + "UpdateResponseRecord.java";
            case RECORD_DELETE_REQUEST -> classNameField.getText().trim() + "DeleteRequestRecord.java";
            case RECORD_DELETE_RESPONSE -> classNameField.getText().trim() + "DeleteResponseRecord.java";
            case RECORD_SEARCH_REQUEST -> classNameField.getText().trim() + "SearchRequestRecord.java";
            case RECORD_SEARCH_RESPONSE -> classNameField.getText().trim() + "SearchResponseRecord.java";
            case CONTROLLER -> classNameField.getText().trim() + "Controller.java";
            case SERVICE_INTERFACE -> classNameField.getText().trim() + "Service.java";
            case SERVICE_IMPL -> classNameField.getText().trim() + "ServiceImpl.java";
            case REPOSITORY -> classNameField.getText().trim() + "Repository.java";
            case MAPPER -> classNameField.getText().trim() + "Mapper.java";
            case XML -> classNameField.getText().trim() + "Mapper.xml";
            default -> classNameField.getText().trim() + ".java";
        };

        String fileName = JOptionPane.showInputDialog(this, MessageUtil.get("save.file.name"), defaultName);
        if (fileName == null || fileName.isBlank()) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(fileName));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(chooser.getSelectedFile())) {
                writer.write(code);
                MainFrame.log(MessageUtil.get("file.save.success") + ": " + chooser.getSelectedFile().getAbsolutePath());
            } catch (IOException ex) {
                MainFrame.log(MessageUtil.get("file.save.failure") + ": " + ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        MessageUtil.get("file.save.failure") + ".\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ✅ 코드 복사
    private void copyCode() {
        String code = previewArea.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    MessageUtil.get("no.code.copy"),
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new java.awt.datatransfer.StringSelection(code), null);

        MainFrame.log(MessageUtil.get("code.copy.success"));
    }

    public void setClassNameFromTable(String tableName) {
        String entityName = CaseUtils.toUppercaseFirstLetter(tableName);
        classNameField.setText(entityName);
    }
}
