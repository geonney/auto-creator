package studio.geonlee.auto_creator.dialog;

import studio.geonlee.auto_creator.config.message.MessageUtil;
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
        super(owner, MessageUtil.get("title.settings"), true);
        setSize(800, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // 1. 트리 만들기
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(MessageUtil.get("title.settings"));
        DefaultMutableTreeNode generalNode = new DefaultMutableTreeNode(MessageUtil.get("tree.setting.general"));
//        DefaultMutableTreeNode databaseNode = new DefaultMutableTreeNode(MessageUtil.get("tree.setting.database"));
//        DefaultMutableTreeNode generatorNode = new DefaultMutableTreeNode(MessageUtil.get("tree.setting.generator"));
        root.add(generalNode);
//        root.add(databaseNode);
//        root.add(generatorNode);

        menuTree = new JTree(root);
        menuTree.setRootVisible(false);
        JScrollPane treeScroll = new JScrollPane(menuTree);
        treeScroll.setPreferredSize(new Dimension(200, 600));
        add(treeScroll, BorderLayout.WEST);

        // 2. 디테일 패널 만들기
        cardLayout = new CardLayout();
        detailPanel = new JPanel(cardLayout);

        detailPanel.add(new GeneralSettingPanel(), MessageUtil.get("tree.setting.general"));
//        detailPanel.add(new JPanel(), MessageUtil.get("tree.setting.database"));
//        detailPanel.add(new JPanel(), MessageUtil.get("tree.setting.generator"));

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