package studio.geonlee.auto_creator.ui.dialog;

import studio.geonlee.auto_creator.common.enumeration.DatabaseType;
import studio.geonlee.auto_creator.common.enumeration.LogType;
import studio.geonlee.auto_creator.config.DatabaseConfigFileHandler;
import studio.geonlee.auto_creator.config.DefaultConfigFileHandler;
import studio.geonlee.auto_creator.config.dto.DatabaseConfig;
import studio.geonlee.auto_creator.config.dto.DefaultConfig;
import studio.geonlee.auto_creator.config.message.MessageUtil;
import studio.geonlee.auto_creator.context.DatabaseContext;
import studio.geonlee.auto_creator.ui.frame.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Objects;

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
        super(mainFrame, MessageUtil.get("title.database.connection"), true);
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
        DefaultConfig config = DefaultConfigFileHandler.load();
        if (!config.isAutoLoadDatabaseOnStart()) {
            DatabaseConfig loaded = DatabaseConfigFileHandler.load();
            if (loaded != null) {
                databaseTypeCombo.setSelectedItem(DatabaseType.valueOf(loaded.getDatabaseType()));
                hostField.setText(loaded.getHost());
                portField.setText(String.valueOf(loaded.getPort()));
                userField.setText(loaded.getUser());
                passwordField.setText(loaded.getPassword());
            }
        }
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // ✅ 여기에 padding 추가!

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

        panel.add(new JLabel("Database List:"));
        panel.add(databaseCombo);

        return panel;
    }

    private JPanel createButtonPanel() {
        JButton loadBtn = new JButton(MessageUtil.get("button.load.database.list"));
        loadBtn.addActionListener(e -> loadDatabaseList());

        JButton connectBtn = new JButton(MessageUtil.get("button.connection"));
        connectBtn.addActionListener(e -> connectToDatabase());

        JPanel panel = new JPanel();
        panel.add(loadBtn);
        panel.add(connectBtn);
        return panel;
    }

    private void loadDatabaseList() {
        try {
            DatabaseType databaseType = (DatabaseType) databaseTypeCombo.getSelectedItem();
            String url = Objects.requireNonNull(databaseType).formatUrl(hostField.getText(), Integer.parseInt(portField.getText()));

            // TODO 타 DB 관련 처리 필요. 현재 postgresql 만 가능.
            if (databaseType == DatabaseType.POSTGRESQL) {
                url += "postgres";
            } else {
                JOptionPane.showMessageDialog(this,
                        "⚠️ " + databaseType + MessageUtil.get("not.supported.database"),
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            connection = DriverManager.getConnection(url, userField.getText(), new String(passwordField.getPassword()));
            ResultSet rs = connection.createStatement().executeQuery("SELECT datname FROM pg_database WHERE datistemplate = false");

            databaseCombo.removeAllItems();
            while (rs.next()) {
                databaseCombo.addItem(rs.getString("datname"));
            }

            MainFrame.log(MessageUtil.get("database.list.load.success"), LogType.INFO);
        } catch (Exception ex) {
            MainFrame.log(MessageUtil.get("database.list.load.failure") + ": " + ex.getMessage(),
                    LogType.EXCEPTION);
            JOptionPane.showMessageDialog(this,
                    MessageUtil.get("database.list.load.failure") + ".\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void connectToDatabase() {
        String selectedDatabase = (String) databaseCombo.getSelectedItem();

        try {
            DatabaseType databaseType = (DatabaseType) databaseTypeCombo.getSelectedItem();
            DatabaseContext.setDatabaseType(databaseType);

            String url = Objects.requireNonNull(databaseType).formatUrl(hostField.getText(), Integer.parseInt(portField.getText())) + selectedDatabase;

            connection = DriverManager.getConnection(url, userField.getText(), new String(passwordField.getPassword()));
            mainFrame.setDatabaseConnection(connection, selectedDatabase);
            MainFrame.log(MessageUtil.get("database.connection.success") + ": " + selectedDatabase,
                    LogType.INFO);
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
            MainFrame.log(MessageUtil.get("database.connection.failure") + ": " + ex.getMessage()
                    , LogType.EXCEPTION);
            JOptionPane.showMessageDialog(this,
                    MessageUtil.get("database.connection.failure") + ".\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadDatabaseListAndSelect(String databaseName) {
        try {
            DatabaseType databaseType = (DatabaseType) databaseTypeCombo.getSelectedItem();
            String url = Objects.requireNonNull(databaseType).formatUrl(hostField.getText(), Integer.parseInt(portField.getText()));

            // 기본 접속 DB
            if (databaseType == DatabaseType.POSTGRESQL) {
                url += "postgres";
            } else {
                MainFrame.log("⚠️ " + databaseType + MessageUtil.get("not.supported.database"),
                        LogType.INFO);
                return;
            }

            Connection tempConnection = DriverManager.getConnection(url, userField.getText(), new String(passwordField.getPassword()));
            ResultSet rs = tempConnection.createStatement().executeQuery("SELECT datname FROM pg_database WHERE datistemplate = false");

            databaseCombo.removeAllItems();
            while (rs.next()) {
                databaseCombo.addItem(rs.getString("datname"));
            }

            databaseCombo.setSelectedItem(databaseName); // ✅ 여기서 복원
            MainFrame.log(MessageUtil.get("database.list.load.success"), LogType.INFO);

            rs.close();
            tempConnection.close(); // 🔥 접속 끝나면 닫아야 한다

        } catch (Exception ex) {
            MainFrame.log(MessageUtil.get("database.list.load.failure") + ": " + ex.getMessage(),
                    LogType.EXCEPTION);
        }
    }

    public void setDatabaseConfig(DatabaseConfig config) {
        if (config == null) return;

        databaseTypeCombo.setSelectedItem(DatabaseType.valueOf(config.getDatabaseType()));
        hostField.setText(config.getHost());
        portField.setText(String.valueOf(config.getPort()));
        userField.setText(config.getUser());
        passwordField.setText(config.getPassword());
        databaseCombo.removeAllItems();
        databaseCombo.addItem(config.getDatabaseName());
        databaseCombo.setSelectedItem(config.getDatabaseName());
    }
}
