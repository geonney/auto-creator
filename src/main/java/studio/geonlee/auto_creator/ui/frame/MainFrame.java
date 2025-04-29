package studio.geonlee.auto_creator.ui.frame;

import studio.geonlee.auto_creator.common.enumeration.DatabaseType;
import studio.geonlee.auto_creator.config.DatabaseConfigFileHandler;
import studio.geonlee.auto_creator.config.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.dto.DatabaseConfig;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;
import studio.geonlee.auto_creator.config.message.MessageUtil;
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
public class MainFrame extends JFrame {
    private static MainFrame instance;
    private final JTextArea logArea = new JTextArea();
    private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("No database");
    private final JTree tableTree = new JTree(rootNode);
    private final CodeGeneratorPanel codeGeneratorPanel = new CodeGeneratorPanel(this);
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
            System.err.println("Icon setting Failure: " + e.getMessage());
        }


        DefaultConfig config = DefaultConfigFileHandler.load();
        if (config != null && config.getWindowWidth() > 0 && config.getWindowHeight() > 0) {
            setBounds(config.getWindowX(), config.getWindowY(), config.getWindowWidth(), config.getWindowHeight());
        } else {
            setSize(1250, 800); // 기본 사이즈
            setLocationRelativeTo(null); // 화면 중앙
        }
        setLayout(new BorderLayout());

        setupMenu();
        setupLogArea();

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(tableTree),
                codeGeneratorPanel // ← 여기
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
                log(MessageUtil.get("main.chosen.table") + ": " + selectedSchemaName + "." + selectedTableName);
            } else {
                selectedTableName = null;
                selectedSchemaName = null;
            }
        });

        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);
        add(new JScrollPane(logArea), BorderLayout.SOUTH);

        log(MessageUtil.get("main.initial.success"));
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

    public static void log(String message) {
        if (instance != null) {
            instance.logArea.append(message + "\n");
            instance.logArea.setCaretPosition(instance.logArea.getDocument().getLength());
        }
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

            DefaultConfig config = DefaultConfigFileHandler.load();
            if (config != null && config.isExpandTree()) {
                expandAllNodes(tableTree); // ✅ 트리 확장 여기 추가
            } else {
                tableTree.expandRow(0);
            }

            MainFrame.log(MessageUtil.get("table.load.success"));
        } catch (Exception ex) {
            MainFrame.log(MessageUtil.get("table.load.failure") + ": " + ex.getMessage());
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
            DefaultConfig config = DefaultConfigFileHandler.load();
            if (config.isAutoLoadDatabaseOnStart()) {
                DatabaseConfig dbConfig = DatabaseConfigFileHandler.load();
                if (dbConfig != null) {
                    DatabaseType dbType = DatabaseType.valueOf(dbConfig.getDatabaseType());
                    String url = dbType.formatUrl(dbConfig.getHost(), dbConfig.getPort()) + dbConfig.getDatabaseName();

                    Connection conn = DriverManager.getConnection(url, dbConfig.getUser(), dbConfig.getPassword());
                    DatabaseContext.setConnection(conn);
                    DatabaseContext.setDatabaseType(dbType);
                    DatabaseContext.setDatabaseName(dbConfig.getDatabaseName());

                    // ✅ 연결 성공했으면
                    if (databaseConnectionDialog != null) {
                        databaseConnectionDialog.setDatabaseConfig(dbConfig);
                        databaseConnectionDialog.loadDatabaseListAndSelect(dbConfig.getDatabaseName());
                    }

                    refreshTableTree(); // ✅ 트리 다시 그리기
                    MainFrame.log(MessageUtil.get("last.setting.load.success") + ": " + dbConfig.getDatabaseName());
                } else {
                    MainFrame.log(MessageUtil.get("no.database.setting"));
                }
            }
        } catch (Exception e) {
            MainFrame.log(MessageUtil.get("last.setting.load.failure") + ": " + e.getMessage());
        }
    }

    public void refreshCodeGeneratorPanel() {
        if (codeGeneratorPanel != null) {
            codeGeneratorPanel.refreshOrmSettings();
        }
    }

    private void saveWindowSettings() {
        try {
            DefaultConfig config = DefaultConfigFileHandler.load();
            if (config == null) config = new DefaultConfig();

            config.setWindowX(getX());
            config.setWindowY(getY());
            config.setWindowWidth(getWidth());
            config.setWindowHeight(getHeight());

            DefaultConfigFileHandler.save(config);
        } catch (Exception ex) {
            MainFrame.log("❌ 창 위치/크기 저장 실패: " + ex.getMessage());
        }
    }

    private void expandAllNodes(JTree tree) {
        int row = 0;
        while (row < tree.getRowCount()) {
            tree.expandRow(row);
            row++;
        }
    }
}