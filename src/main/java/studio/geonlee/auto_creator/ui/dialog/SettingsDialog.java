package studio.geonlee.auto_creator.ui.dialog;

import studio.geonlee.auto_creator.config.message.MessageUtil;
import studio.geonlee.auto_creator.ui.frame.MainFrame;
import studio.geonlee.auto_creator.ui.panel.GeneralSettingPanel;
import studio.geonlee.auto_creator.ui.panel.GeneratorSettingPanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
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

    public SettingsDialog(MainFrame mainFrame) {
        super(mainFrame, MessageUtil.get("title.settings"), true);
        setSize(800, 600);
        setLocationRelativeTo(mainFrame);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // 1. 트리 만들기
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(MessageUtil.get("title.settings"));
        // 다국어 처리는 setCellRenderer 이벤트에서 처리
        DefaultMutableTreeNode generalNode = new DefaultMutableTreeNode("General");
        DefaultMutableTreeNode generatorNode = new DefaultMutableTreeNode("Generator");
//        DefaultMutableTreeNode databaseNode = new DefaultMutableTreeNode(MessageUtil.get("tree.setting.database"));
        root.add(generalNode);
        root.add(generatorNode);
//        root.add(databaseNode);

        menuTree = new JTree(root);
        menuTree.setRootVisible(false);

        JScrollPane treeScroll = new JScrollPane(menuTree);
        treeScroll.setPreferredSize(new Dimension(200, 600));
        add(treeScroll, BorderLayout.WEST);

        // 2. 디테일 패널 만들기
        cardLayout = new CardLayout();
        detailPanel = new JPanel(cardLayout);

        detailPanel.add(new GeneralSettingPanel(), "General");
        detailPanel.add(new GeneratorSettingPanel(mainFrame), "Generator");
//        detailPanel.add(new JPanel(), MessageUtil.get("tree.setting.database"));

        add(detailPanel, BorderLayout.CENTER);

        // 3. 트리 이벤트 연결
        menuTree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                          boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                if (value instanceof DefaultMutableTreeNode node) {
                    String key = node.getUserObject().toString();
                    String label = switch (key) {
                        case "General" -> MessageUtil.get("tree.setting.general");
                        case "Generator" -> MessageUtil.get("tree.setting.generator");
                        default -> key;
                    };
                    setText(label);
                }

                return c;
            }
        });
        menuTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) menuTree.getLastSelectedPathComponent();
            if (selectedNode == null) return;
            String selected = selectedNode.getUserObject().toString();

            switch (selected) {
                case "General" -> cardLayout.show(detailPanel, "General");
                case "Generator" -> cardLayout.show(detailPanel, "Generator");
            }
        });

        // 4. 초기 선택 General
        menuTree.setSelectionPath(new TreePath(generalNode.getPath()));
    }
}