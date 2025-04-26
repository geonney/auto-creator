package studio.geonlee.auto_creator.dialog;

import studio.geonlee.auto_creator.common.enumeration.DatabaseType;
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
public class DatabaseConfigDialog extends JDialog {
    private final JComboBox<DatabaseType> databaseTypeCombo = new JComboBox<>(DatabaseType.values());
    private final JTextField hostField = new JTextField("localhost");
    private final JTextField portField = new JTextField("5432");
    private final JTextField userField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JComboBox<String> databaseCombo = new JComboBox<>();

    private final MainFrame mainFrame;
    private Connection connection;

    public DatabaseConfigDialog(MainFrame mainFrame) {
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
            DatabaseType dbType = (DatabaseType) databaseTypeCombo.getSelectedItem();
            String url = dbType.formatUrl(hostField.getText(), Integer.parseInt(portField.getText()));

            // PostgreSQL 한정: 'postgres' database에 먼저 접속하여 DB 목록 조회
            if (dbType == DatabaseType.POSTGRESQL) {
                url += "postgres";
            } else {
                JOptionPane.showMessageDialog(this, dbType + "의 DB 목록 조회는 아직 지원되지 않습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
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
            DatabaseType dbType = (DatabaseType) databaseTypeCombo.getSelectedItem();
            String url = dbType.formatUrl(hostField.getText(), Integer.parseInt(portField.getText())) + selectedDatabase;

            connection = DriverManager.getConnection(url, userField.getText(), new String(passwordField.getPassword()));
            mainFrame.setDatabaseConnection(connection, selectedDatabase);
            mainFrame.log("✅ 데이터베이스 연결 성공: " + selectedDatabase);
            dispose();
        } catch (Exception ex) {
            mainFrame.log("❌ 연결 실패: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "데이터베이스 연결 실패\n" + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}
