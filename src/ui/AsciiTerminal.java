package ui;

import java.awt.Dimension;

import javax.swing.JFrame;

/**
 *	Create a JFrame with an AsciiPanel.
 *
 * @author Julien MAITRE
 * 
 */
public class AsciiTerminal extends JFrame {
	private AsciiPanel asciiPanel;

    public AsciiTerminal(String title, Dimension dimension, String tilesetFile, int characterWidth, int characterHeight) {
        asciiPanel = new AsciiPanel(dimension, tilesetFile, characterWidth, characterHeight);
        
        this.setTitle(title);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().add(asciiPanel);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
	
	public AsciiPanel getAsciiPanel() {
		return asciiPanel;
	}
}
