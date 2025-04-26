package studio.geonlee.auto_creator.dialog;

import studio.geonlee.auto_creator.panel.GeneralSettingPanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;

/**
 * @author GEON
 * @since 2025-04-26
 **/
public class SettingsDialog extends JDialog {

    private final JTree menuTree;
    private final JPanel detailPanel;
    private final CardLayout cardLayout;

    public SettingsDialog(JFrame owner) {
        super(owner, "Settings", true);
        setSize(800, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // 1. 트리 만들기
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Settings");
        DefaultMutableTreeNode generalNode = new DefaultMutableTreeNode("General");
        DefaultMutableTreeNode databaseNode = new DefaultMutableTreeNode("Database");
        DefaultMutableTreeNode generatorNode = new DefaultMutableTreeNode("Generator");
        root.add(generalNode);
        root.add(databaseNode);
        root.add(generatorNode);

        menuTree = new JTree(root);
        menuTree.setRootVisible(false);
        JScrollPane treeScroll = new JScrollPane(menuTree);
        treeScroll.setPreferredSize(new Dimension(200, 600));
        add(treeScroll, BorderLayout.WEST);

        // 2. 디테일 패널 만들기
        cardLayout = new CardLayout();
        detailPanel = new JPanel(cardLayout);

        detailPanel.add(new GeneralSettingPanel(), "General");
        detailPanel.add(new JPanel(), "Database");   // Database는 아직 구현 안됨 (Placeholder)
        detailPanel.add(new JPanel(), "Generator");  // Generator도 Placeholder

        add(detailPanel, BorderLayout.CENTER);

        // 3. 트리 이벤트 연결
        menuTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) menuTree.getLastSelectedPathComponent();
            if (node == null) return;

            String selected = node.getUserObject().toString();
            cardLayout.show(detailPanel, selected);
        });

        // 4. 초기 선택 General
        menuTree.setSelectionPath(new TreePath(generalNode.getPath()));
    }
}