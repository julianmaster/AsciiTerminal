package examples;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.JOptionPane;

import ui.AsciiPanel;
import ui.AsciiSelectableTerminalButton;
import ui.AsciiTerminal;
import ui.AsciiTerminalButton;

public class AsciiUISample {
	
	public static final String TITLE = "ASCII Demo";
	public static final int WINDOW_WIDTH = 16;
	public static final int WINDOW_HEIGHT = 16;
	
	private final AsciiTerminal terminal;
	
	public AsciiUISample(String tilesetFile, int characterWidth, int characterHeight) {
		terminal = new AsciiTerminal(TITLE, new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT), tilesetFile, characterWidth, characterHeight);
		
		AsciiPanel asciiPanel = terminal.getAsciiPanel();
		Random rand = new Random();
		
		for(int i = 0; i < 16; i++) {
			for(int j = 0; j < 10; j++) {
				asciiPanel.write(i, j, (char)rand.nextInt(256), new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
			}
		}
		
		AsciiTerminalButton button1 = new AsciiTerminalButton(asciiPanel, "Clik on me !", 0, 12, Color.GREEN, Color.ORANGE);
		button1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JOptionPane.showMessageDialog(terminal, "Thank !");
			}
		});
		asciiPanel.add(button1);
		
		AsciiSelectableTerminalButton button2 = new AsciiSelectableTerminalButton(asciiPanel, "Select me !", 0, 14, Color.GREEN, Color.ORANGE, Color.MAGENTA);
		button2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				AsciiSelectableTerminalButton astb = (AsciiSelectableTerminalButton)e.getComponent();
				astb.setSelect(!astb.isSelect());
			}
		});
		asciiPanel.add(button2);
		
		terminal.repaint();
	}
	
	public static void main(String[] args) {
		String[] choices = { "Anikki [8x8]", "Yoshis island [9x12]", "Vidumec [15x15]", "Wanderlust [16x16]", "Curses square [24x24]" };
	    
		String input = (String) JOptionPane.showInputDialog(null, "Choose tilset...",
	        "Choice of tileset", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
	    
		if(input != null) {
			if(input.equals("Anikki [8x8]")) {
				new AsciiUISample("/assets/Anikki_square_8x8.png", 8, 8);
			}
			else if(input.equals("Yoshis island [9x12]")) {
				new AsciiUISample("/assets/Yoshis_island_9x12.png", 9, 12);
			}
			else if(input.equals("Vidumec [15x15]")) {
				new AsciiUISample("/assets/Vidumec_15x15.png", 15, 15);
			}
			else if(input.equals("Wanderlust [16x16]")) {
				new AsciiUISample("/assets/wanderlust_16x16.png", 16, 16);
			}
			else {
				new AsciiUISample("/assets/Curses_square_24x24.png", 24, 24);
			}
		}
	}
}
