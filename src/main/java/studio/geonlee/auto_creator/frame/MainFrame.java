package studio.geonlee.auto_creator.frame;

import studio.geonlee.auto_creator.context.DatabaseContext;
import studio.geonlee.auto_creator.dialog.DatabaseConnectionDialog;
import studio.geonlee.auto_creator.panel.CodeGeneratorPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author GEON
 * @since 2025-04-25
 **/
public class MainFrame extends JFrame {
    private final JPanel mainPanel = new JPanel(new CardLayout());
    private final JTextArea logArea = new JTextArea();
    private final Map<String, JPanel> panelMap = new HashMap<>();
    private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("No database");
    private final JTree tableTree = new JTree(rootNode);
    private final CodeGeneratorPanel codeGeneratorPanel = new CodeGeneratorPanel(this);
    private String selectedTableName;
    private String selectedSchemaName;

    public MainFrame() {
        setTitle("🔧 코드 자동 생성기");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            URL iconUrl = getClass().getClassLoader().getResource("icon/ci.png");
            if (iconUrl != null) {
                Image icon = ImageIO.read(iconUrl);
                setIconImage(icon);
            }
        } catch (IOException e) {
            System.err.println("아이콘 설정 실패: " + e.getMessage());
        }

        setSize(1000, 700);
        setLocationRelativeTo(null);
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
                log("📌 테이블 선택: " + selectedSchemaName + "." + selectedTableName);
            } else {
                selectedTableName = null;
                selectedSchemaName = null;
            }
        });

        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);
        add(new JScrollPane(logArea), BorderLayout.SOUTH);

        log("✅ 프로그램 시작됨");
        setVisible(true);
    }

    private void setupMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu dbMenu = new JMenu("데이터베이스 설정");

        JMenuItem dbConnectItem = new JMenuItem("연결 설정...");
        dbConnectItem.addActionListener(e -> {
            new DatabaseConnectionDialog(this);
        });
        dbMenu.add(dbConnectItem);

        JMenu generateMenu = new JMenu("코드 생성");
        JMenuItem entityItem = new JMenuItem("Entity 생성");
        entityItem.addActionListener(e -> switchPanel("entityGenerator"));
        generateMenu.add(entityItem);

        // 메뉴 등록
        menuBar.add(dbMenu);
        menuBar.add(generateMenu);
        setJMenuBar(menuBar);
    }

    private void setupLogArea() {
        logArea.setEditable(false);
        logArea.setRows(8);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
    }

    private void switchPanel(String name) {
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        if (!panelMap.containsKey(name)) {
            JPanel newPanel = createPanel(name);
            panelMap.put(name, newPanel);
            mainPanel.add(newPanel, name);
        }
        cl.show(mainPanel, name);
        log("🔁 화면 전환: " + name);
    }

    private JPanel createPanel(String name) {
        System.out.println(name);
        return switch (name) {
//            case "databaseConfig" -> new DatabaseConfigPanel(this);
//            case "entityGenerator" -> new EntityGeneratorPanel(this);
            default -> new JPanel(); // fallback
        };
    }

    public void log(String message) {
        logArea.append(message + "\n");
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
            tableTree.expandRow(0);
            log("✅ 테이블 트리 로딩 완료");
        } catch (Exception ex) {
            log("❌ 테이블 트리 로딩 실패: " + ex.getMessage());
        }
    }

    public String getSelectedTable() {
        return selectedTableName;
    }

    public String getSelectedSchema() {
        return selectedSchemaName;
    }
}