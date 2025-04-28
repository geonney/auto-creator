package studio.geonlee.auto_creator.ui.provider;

import studio.geonlee.auto_creator.common.record.EntityMetadata;

import javax.swing.*;
import java.util.List;

/**
 * @author GEON
 * @since 2025-04-28
 **/
public interface ArchitectureButtonProvider {
    List<JButton> getButtons(EntityMetadata entityMetadata);
}
