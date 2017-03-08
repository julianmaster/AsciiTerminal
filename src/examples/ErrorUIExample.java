package examples;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Random;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import ui.AsciiPanel;
import ui.AsciiTerminal;
import ui.AsciiTerminalDataCell;

public class ErrorUIExample {
	
	public static final String TITLE = "ASCII ";
	public static final int WINDOW_WIDTH = 36;
	public static final int WINDOW_HEIGHT = 36;
	public static final int TARGET_FPS = 60;
	public static final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
	
	private final AsciiTerminal asciiTerminal;
	private final AsciiPanel asciiPanel;
	
	private Random rand = new Random();
	
	private AsciiTerminalDataCell[][] view = new AsciiTerminalDataCell[WINDOW_WIDTH][WINDOW_HEIGHT];
	
	public ErrorUIExample(String tilesetFile, int characterWidth, int characterHeight, int scale, boolean customWindow) {
		asciiTerminal = new AsciiTerminal(TITLE, new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT), tilesetFile, characterWidth, characterHeight, scale, customWindow);
		asciiPanel = asciiTerminal.getAsciiPanel();
		
		for(int i = 0; i < WINDOW_WIDTH; i++) {
			for(int j = 0; j < WINDOW_HEIGHT; j++) {
				view[i][j] = new AsciiTerminalDataCell((char)rand.nextInt(256), new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)), Color.BLACK);
			}
		}
	}
	
	public void run() {
		long lastLoopTime = System.nanoTime();
		
		while(true) {
			long now = System.nanoTime();
			double updateLength = now - lastLoopTime;
			lastLoopTime = now;
			double delta = updateLength / OPTIMAL_TIME;
			
			asciiPanel.clear();
			
			for(int i = 0; i < WINDOW_WIDTH; i++) {
				for(int j = 0; j < WINDOW_HEIGHT; j++) {
					asciiPanel.write(i, j, view[i][j]);
				}
			}
			
			asciiTerminal.repaint();
			
			try {
				long value = (lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000;
				if(value > 0) {
					Thread.sleep(value);					
				}
				else {
					Thread.sleep(0, 1);
				}
			} catch (InterruptedException e) {
			}
		}
	}
	
	public static void main(String[] args) {
		String[] choiceTileset = { "Anikki [8x8]", "Yoshis island [9x12]", "Vidumec [15x15]", "Wanderlust [16x16]", "Curses square [24x24]" };
		JComboBox<String> comboChoiceTileset = new JComboBox<>(choiceTileset);
//		comboChoiceTileset.setSelectedItem("Curses square [24x24]");
		
		String[] choiceScale = { "Small", "Medium", "Large"};
		JComboBox<String> comboChoiceScale = new JComboBox<>(choiceScale);
		comboChoiceScale.setSelectedItem("Medium");
		Object[] choices = {
				"Tileset:", comboChoiceTileset,
				"Scale:", comboChoiceScale
		};
	    
		int option = JOptionPane.showConfirmDialog(null, choices, "Configurations", JOptionPane.OK_CANCEL_OPTION);
	    if(option == JOptionPane.OK_OPTION) {
	    	int scale = comboChoiceScale.getSelectedIndex()+1;
	    	if(comboChoiceTileset.getSelectedItem().equals("Anikki [8x8]")) {
				new ErrorUIExample("/assets/Anikki_square_8x8.png", 8, 8, scale, true).run();
			}
			else if(comboChoiceTileset.getSelectedItem().equals("Yoshis island [9x12]")) {
				new ErrorUIExample("/assets/Yoshis_island_9x12.png", 9, 12, scale, true).run();
			}
			else if(comboChoiceTileset.getSelectedItem().equals("Vidumec [15x15]")) {
				new ErrorUIExample("/assets/Vidumec_15x15.png", 15, 15, scale, true).run();
			}
			else if(comboChoiceTileset.getSelectedItem().equals("Wanderlust [16x16]")) {
				new ErrorUIExample("/assets/wanderlust_16x16.png", 16, 16, scale, true).run();
			}
			else {
				new ErrorUIExample("/assets/Curses_square_24x24.png", 24, 24, scale, true).run();
			}
	    }
	}
}
