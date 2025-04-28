package studio.geonlee.auto_creator;

import com.formdev.flatlaf.FlatDarculaLaf;
import studio.geonlee.auto_creator.ui.frame.MainFrame;

import javax.swing.*;

//@SpringBootApplication
public class AutoCreatorApplication {

	public static void main(String[] args) {
		
		// 테마 적용
		try {
			UIManager.setLookAndFeel(new FlatDarculaLaf());
		} catch (Exception e) {
			System.err.println("FlatLaf 테마 적용 실패");
		}

//		SpringApplication.run(AutoCreatorApplication.class, args);
		SwingUtilities.invokeLater(MainFrame::new); //MainFrame 실행
	}

}
