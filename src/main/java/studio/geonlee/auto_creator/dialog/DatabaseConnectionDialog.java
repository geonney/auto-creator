package studio.geonlee.auto_creator.dialog;

import studio.geonlee.auto_creator.common.enumeration.DatabaseType;
import studio.geonlee.auto_creator.config.DatabaseConfig;
import studio.geonlee.auto_creator.config.DatabaseConfigFileHandler;
import studio.geonlee.auto_creator.context.DatabaseContext;
import studio.geonlee.auto_creator.frame.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

/**
 * @author GEON
 * @since 2025-04-25
 **/
public class DatabaseConnectionDialog extends JDialog {
    private final JComboBox<DatabaseType> databaseTypeCombo = new JComboBox<>(DatabaseType.values());
    private final JTextField hostField = new JTextField("localhost");
    private final JTextField portField = new JTextField("5432");
    private final JTextField userField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JComboBox<String> databaseCombo = new JComboBox<>();

    private final MainFrame mainFrame;
    private Connection connection;

    public DatabaseConnectionDialog(MainFrame mainFrame) {
        super(mainFrame, "데이터베이스 연결", true);
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        setSize(450, 320);
        setLocationRelativeTo(mainFrame);

        // ✅ DBMS 선택 시 기본 포트 자동 설정
        databaseTypeCombo.addActionListener(e -> {
            DatabaseType selected = (DatabaseType) databaseTypeCombo.getSelectedItem();
            if (selected != null) {
                portField.setText(String.valueOf(selected.getDefaultPort()));
            }
        });

        DatabaseConfig loaded = DatabaseConfigFileHandler.load();
        if (loaded != null) {
            databaseTypeCombo.setSelectedItem(DatabaseType.valueOf(loaded.getDatabaseType()));
            hostField.setText(loaded.getHost());
            portField.setText(String.valueOf(loaded.getPort()));
            userField.setText(loaded.getUser());
            passwordField.setText(loaded.getPassword());
        }

        setVisible(true);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 8));

        panel.add(new JLabel("Database Type:"));
        panel.add(databaseTypeCombo);

        panel.add(new JLabel("Host:"));
        panel.add(hostField);

        panel.add(new JLabel("Port:"));
        panel.add(portField);

        panel.add(new JLabel("User:"));
        panel.add(userField);

        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        panel.add(new JLabel("Database Name:"));
        panel.add(databaseCombo);

        return panel;
    }

    private JPanel createButtonPanel() {
        JButton loadBtn = new JButton("데이터베이스 목록 불러오기");
        loadBtn.addActionListener(e -> loadDatabaseList());

        JButton connectBtn = new JButton("연결");
        connectBtn.addActionListener(e -> connectToDatabase());

        JPanel panel = new JPanel();
        panel.add(loadBtn);
        panel.add(connectBtn);
        return panel;
    }

    private void loadDatabaseList() {
        try {
            DatabaseType databaseType = (DatabaseType) databaseTypeCombo.getSelectedItem();
            String url = databaseType.formatUrl(hostField.getText(), Integer.parseInt(portField.getText()));

            // TODO 타 DB 관련 처리 필요. 현재 postgresql 만 가능.
            if (databaseType == DatabaseType.POSTGRESQL) {
                url += "postgres";
            } else {
                JOptionPane.showMessageDialog(this, databaseType + "의 DB 목록 조회는 아직 지원되지 않습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            connection = DriverManager.getConnection(url, userField.getText(), new String(passwordField.getPassword()));
            ResultSet rs = connection.createStatement().executeQuery("SELECT datname FROM pg_database WHERE datistemplate = false");

            databaseCombo.removeAllItems();
            while (rs.next()) {
                databaseCombo.addItem(rs.getString("datname"));
            }

            mainFrame.log("✅ 데이터베이스 목록 로드 완료");
        } catch (Exception ex) {
            mainFrame.log("❌ DB 목록 로드 실패: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "DB 목록 조회 실패\n" + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void connectToDatabase() {
        String selectedDatabase = (String) databaseCombo.getSelectedItem();
        if (selectedDatabase == null || selectedDatabase.isBlank()) {
            JOptionPane.showMessageDialog(this, "데이터베이스를 선택하세요.", "오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            DatabaseType databaseType = (DatabaseType) databaseTypeCombo.getSelectedItem();
            DatabaseContext.setDatabaseType(databaseType);

            String url = databaseType.formatUrl(hostField.getText(), Integer.parseInt(portField.getText())) + selectedDatabase;

            connection = DriverManager.getConnection(url, userField.getText(), new String(passwordField.getPassword()));
            mainFrame.setDatabaseConnection(connection, selectedDatabase);
            mainFrame.log("✅ 데이터베이스 연결 성공: " + selectedDatabase);
            dispose();
            DatabaseConfigFileHandler.save(new DatabaseConfig(
                    databaseType.name(),
                    hostField.getText(),
                    Integer.parseInt(portField.getText()),
                    userField.getText(),
                    new String(passwordField.getPassword()),
                    selectedDatabase
            ));
        } catch (Exception ex) {
            mainFrame.log("❌ 연결 실패: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "데이터베이스 연결 실패\n" + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}
