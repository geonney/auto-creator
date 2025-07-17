package studio.geonlee.auto_creator.ui.frame;

import lombok.extern.slf4j.Slf4j;
import studio.geonlee.auto_creator.common.enumeration.DatabaseType;
import studio.geonlee.auto_creator.common.enumeration.LogType;
import studio.geonlee.auto_creator.config.message.MessageUtil;
import studio.geonlee.auto_creator.config.setting.DatabaseConfigFileHandler;
import studio.geonlee.auto_creator.config.setting.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.setting.GlobalConfig;
import studio.geonlee.auto_creator.context.DatabaseContext;
import studio.geonlee.auto_creator.ui.dialog.AboutDialog;
import studio.geonlee.auto_creator.ui.dialog.DatabaseConnectionDialog;
import studio.geonlee.auto_creator.ui.dialog.SettingsDialog;
import studio.geonlee.auto_creator.ui.panel.CodeGeneratorPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

/**
 * @author GEON
 * @since 2025-04-25
 **/
@Slf4j
public class MainFrame extends JFrame {
    private static MainFrame instance;
    private final JTextArea logArea = new JTextArea();
    private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("No database");
    private final JTree tableTree = new JTree(rootNode);
    private final CodeGeneratorPanel codeGeneratorPanel;
    private final DatabaseConnectionDialog databaseConnectionDialog;
    private String selectedTableName;
    private String selectedSchemaName;

    public MainFrame() {
        instance = this;
        setTitle("🔧 Auto Code");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveWindowSettings();
            }
        });

        try {
            URL iconUrl = getClass().getClassLoader().getResource("icon/ci.png");
            if (iconUrl != null) {
                Image icon = ImageIO.read(iconUrl);
                setIconImage(icon);
            }
        } catch (IOException e) {
            log.error("Icon setting Failure: {}", e.getMessage());
        }

        //설정 파일 로드
        DatabaseConfigFileHandler databaseConfigFileHandler = new DatabaseConfigFileHandler();
        DefaultConfigFileHandler defaultConfigFileHandler = new DefaultConfigFileHandler();
        GlobalConfig.defaultConfig = defaultConfigFileHandler.load();
        GlobalConfig.databaseConfig = databaseConfigFileHandler.load();
        setMinimumSize(new Dimension(1070, 800));
        if (GlobalConfig.defaultConfig != null && GlobalConfig.defaultConfig.getWindowWidth() > 0
                && GlobalConfig.defaultConfig.getWindowHeight() > 0) {
            setBounds(GlobalConfig.defaultConfig.getWindowX(), GlobalConfig.defaultConfig.getWindowY(),
                    GlobalConfig.defaultConfig.getWindowWidth(), GlobalConfig.defaultConfig.getWindowHeight());
        } else {
            setSize(1070, 800); // 기본 사이즈
            setLocationRelativeTo(null); // 화면 중앙
        }
        setLayout(new BorderLayout());

        setupMenu();
        setupLogArea();

        codeGeneratorPanel = new CodeGeneratorPanel(this);
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(tableTree),
                codeGeneratorPanel
        );
        //테이블 트리 이벤트
        tableTree.addTreeSelectionListener(e -> {
            Object node = tableTree.getLastSelectedPathComponent();
            if (node == null) return;

            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) node;
            String name = selectedNode.getUserObject().toString();

            if (name.startsWith("📄 ")) {
                selectedTableName = name.substring(2).trim();

                // ✅ 부모 노드에서 스키마 추출
                DefaultMutableTreeNode schemaNode = (DefaultMutableTreeNode) selectedNode.getParent();
                selectedSchemaName = schemaNode.getUserObject().toString().replace("📁 ", "").trim();

                codeGeneratorPanel.setClassNameFromTable(selectedTableName);
                log(MessageUtil.get("main.chosen.table") + ": " + selectedSchemaName + "." + selectedTableName,
                        LogType.INFO);
            } else {
                selectedTableName = null;
                selectedSchemaName = null;
            }
        });

        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);
        add(new JScrollPane(logArea), BorderLayout.SOUTH);

        log(MessageUtil.get("main.initial.success"), LogType.INFO);
        setVisible(true);

        // TODO 이전에 저장한 DB 정보를 자동 매핑하기 위해 설정
        databaseConnectionDialog = new DatabaseConnectionDialog(this);
        tryAutoConnectDatabase();
    }

    private void setupMenu() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu(MessageUtil.get("menu.file"));

        JMenuItem databaseConnectionItem = new JMenuItem(MessageUtil.get("menu.file.database.connection"));
        JMenuItem settingsItem = new JMenuItem(MessageUtil.get("menu.settings"));

        fileMenu.add(databaseConnectionItem);
        fileMenu.add(settingsItem);

        // Help Menu
        JMenu helpMenu = new JMenu(MessageUtil.get("menu.help"));
        JMenuItem aboutItem = new JMenuItem(MessageUtil.get("menu.help.about"));

        helpMenu.add(aboutItem);

        // Menu registration
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        // TODO 단축키 설정
        databaseConnectionItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
        settingsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));

        // Menu event registration
        databaseConnectionItem.addActionListener(e -> databaseConnectionDialog.setVisible(true));
        settingsItem.addActionListener(e -> new SettingsDialog(this).setVisible(true));
        aboutItem.addActionListener(e -> new AboutDialog(this).setVisible(true));
    }

    private void setupLogArea() {
        logArea.setEditable(false);
        logArea.setRows(8);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
    }

    public void setDatabaseConnection(Connection conn, String dbName) {
        // 이후 테이블 트리 구성 등에 사용할 수 있도록 저장
        DatabaseContext.setConnection(conn);
        DatabaseContext.setDatabaseName(dbName);

        // TODO: 트리 구성할 수 있도록 호출
        refreshTableTree();
    }

    public void refreshTableTree() {
        try {
            rootNode.removeAllChildren();
            String dbName = DatabaseContext.getDatabaseName();
            rootNode.setUserObject("📦 " + dbName);

            Connection conn = DatabaseContext.getConnection();
            DatabaseMetaData meta = conn.getMetaData();

            ResultSet schemas = meta.getSchemas();
            while (schemas.next()) {
                String schema = schemas.getString("TABLE_SCHEM");
                DefaultMutableTreeNode schemaNode = new DefaultMutableTreeNode("📁 " + schema);

                ResultSet tables = meta.getTables(null, schema, "%", new String[]{"TABLE"});
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    schemaNode.add(new DefaultMutableTreeNode("📄 " + tableName));
                }

                if (schemaNode.getChildCount() > 0) {
                    rootNode.add(schemaNode);
                }
            }

            ((DefaultTreeModel) tableTree.getModel()).reload();

            if (GlobalConfig.defaultConfig != null && GlobalConfig.defaultConfig.isExpandTree()) {
                expandAllNodes(tableTree); // ✅ 트리 확장 여기 추가
            } else {
                tableTree.expandRow(0);
            }

            MainFrame.log(MessageUtil.get("table.load.success"), LogType.INFO);
        } catch (Exception ex) {
            MainFrame.log(MessageUtil.get("table.load.failure") + ": " + ex.getMessage(), LogType.EXCEPTION);
        }
    }

    public String getSelectedTable() {
        return selectedTableName;
    }

    public String getSelectedSchema() {
        return selectedSchemaName;
    }

    private void tryAutoConnectDatabase() {
        try {
            if (GlobalConfig.defaultConfig.isAutoLoadDatabaseOnStart()) {
                if (GlobalConfig.databaseConfig != null) {
                    DatabaseType dbType = DatabaseType.valueOf(GlobalConfig.databaseConfig.getDatabaseType());
                    String url = dbType.formatUrl(GlobalConfig.databaseConfig.getHost(),
                            GlobalConfig.databaseConfig.getPort()) + GlobalConfig.databaseConfig.getDatabaseName();

                    Connection conn = DriverManager.getConnection(url, GlobalConfig.databaseConfig.getUser(),
                            GlobalConfig.databaseConfig.getPassword());
                    DatabaseContext.setConnection(conn);
                    DatabaseContext.setDatabaseType(dbType);
                    DatabaseContext.setDatabaseName(GlobalConfig.databaseConfig.getDatabaseName());

                    // ✅ 연결 성공했으면
                    if (databaseConnectionDialog != null) {
                        databaseConnectionDialog.setDatabaseConfig(GlobalConfig.databaseConfig);
                        databaseConnectionDialog.loadDatabaseListAndSelect(GlobalConfig.databaseConfig.getDatabaseName());
                    }

                    refreshTableTree(); // ✅ 트리 다시 그리기
                    MainFrame.log(MessageUtil.get("last.setting.load.success") + ": " +
                            GlobalConfig.databaseConfig.getDatabaseName(), LogType.INFO);
                } else {
                    MainFrame.log(MessageUtil.get("no.database.setting"), LogType.INFO);
                }
            }
        } catch (Exception e) {
            MainFrame.log(MessageUtil.get("last.setting.load.failure") + ": " + e.getMessage(), LogType.EXCEPTION);
        }
    }

    public void refreshCodeGeneratorPanel() {
        if (codeGeneratorPanel != null) {
            codeGeneratorPanel.refreshOrmSettings();
        }
    }

    private void saveWindowSettings() {
        try {
            GlobalConfig.defaultConfig.setWindowX(getX());
            GlobalConfig.defaultConfig.setWindowY(getY());
            GlobalConfig.defaultConfig.setWindowWidth(getWidth());
            GlobalConfig.defaultConfig.setWindowHeight(getHeight());

            DefaultConfigFileHandler defaultConfigFileHandler = new DefaultConfigFileHandler();
            defaultConfigFileHandler.save(GlobalConfig.defaultConfig);
        } catch (Exception ex) {
            MainFrame.log("❌ 창 위치/크기 저장 실패: " + ex.getMessage(), LogType.EXCEPTION);
        }
    }

    private void expandAllNodes(JTree tree) {
        int row = 0;
        while (row < tree.getRowCount()) {
            tree.expandRow(row);
            row++;
        }
    }

    public static void log(String message, LogType logType) {
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (logType == LogType.EXCEPTION) {
            log.error(message);
        } else {
            log.info(message);
        }
        instance.logArea.append("[" + timestamp + "] " + message + "\n");
        instance.logArea.setCaretPosition(instance.logArea.getDocument().getLength()); // 항상 스크롤 맨 밑으로
    }
}