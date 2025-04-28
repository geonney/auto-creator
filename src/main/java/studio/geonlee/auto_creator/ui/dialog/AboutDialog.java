package studio.geonlee.auto_creator.ui.dialog;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * @author GEON
 * @since 2025-04-26
 **/
public class AboutDialog extends JDialog {

    public AboutDialog(Frame owner) {
        super(owner, "About Auto-Code", true);
        setLayout(new BorderLayout(10, 10));

        // 왼쪽 아이콘
        JLabel iconLabel = new JLabel();
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icon/ac_icon_96x96_brighter.png"))); // 아이콘 경로 맞게
        iconLabel.setIcon(icon);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        iconLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        add(iconLabel, BorderLayout.WEST);

        // 오른쪽 텍스트 패널
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        // ✨ 살짝 아래로 내리는 여백
        textPanel.add(Box.createVerticalStrut(10));

        JLabel titleLabel = new JLabel("Auto Code (v0.0.1)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel javaVersionLabel = new JLabel("Java Version: " + System.getProperty("java.version"));
        javaVersionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel githubLabel = new JLabel("<html>GitHub: <a href='https://github.com/geonney'>https://github.com/geonney</a></html>");
        githubLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        githubLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        textPanel.add(javaVersionLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(githubLabel);

        textPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(textPanel, BorderLayout.CENTER);

        // 하단 Close 버튼
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(450, 220);
        setResizable(false);
        setLocationRelativeTo(owner);
    }
}
