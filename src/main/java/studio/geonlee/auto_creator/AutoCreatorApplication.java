package studio.geonlee.auto_creator;

import com.formdev.flatlaf.FlatDarculaLaf;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import studio.geonlee.auto_creator.ui.frame.MainFrame;

import javax.swing.*;

@Slf4j
public class AutoCreatorApplication {

	public static void main(String[] args) {
		
		// 테마 적용
		try {
			UIManager.setLookAndFeel(new FlatDarculaLaf());
			log.info("The application has been successfully run.");
		} catch (Exception e) {
			log.error("FlatLaf 테마 적용 실패");
		}

		SwingUtilities.invokeLater(MainFrame::new); //MainFrame 실행
	}

}
