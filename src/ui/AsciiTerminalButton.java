package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

/**
 * A simple button
 * 
 * @author julien MAITRE
 *
 */
public class AsciiTerminalButton extends JComponent implements MouseListener {
	protected final AsciiPanel asciiPanel;
	private String name;
	private int x;
	private int y;
	protected boolean entered = false;
	protected Color mouseCurrentColor;
	protected Color mouseDefaultColor;
	protected Color mouseEnteredColor;
	protected Color mouseBackgroundColor;
	
	public AsciiTerminalButton(AsciiPanel asciiPanel, String label, int x, int y, Color mouseDefaultColor, Color mouseEnteredColor) {
		this.asciiPanel = asciiPanel;
		this.name = label;
		this.x = x;
		this.y = y;
		this.mouseCurrentColor = mouseDefaultColor;
		this.mouseDefaultColor = mouseDefaultColor;
		this.mouseEnteredColor = mouseEnteredColor;
		setBounds(x*asciiPanel.getCharacterSize().width, y*asciiPanel.getCharacterSize().height, label.length()*asciiPanel.getCharacterSize().width, asciiPanel.getCharacterSize().height);
		this.addMouseListener(this);
	}
	
	public AsciiTerminalButton(AsciiPanel asciiPanel, String label, int x, int y, Color mouseDefaultColor, Color mouseEnteredColor, Color mouseBackgroundColor) {
		this.asciiPanel = asciiPanel;
		this.name = label;
		this.x = x;
		this.y = y;
		this.mouseCurrentColor = mouseDefaultColor;
		this.mouseDefaultColor = mouseDefaultColor;
		this.mouseEnteredColor = mouseEnteredColor;
		this.mouseBackgroundColor = mouseBackgroundColor;
		setBounds(x*asciiPanel.getCharacterSize().width, y*asciiPanel.getCharacterSize().height, label.length()*asciiPanel.getCharacterSize().width, asciiPanel.getCharacterSize().height);
		this.addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// nothing
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// nothing
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// nothing
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		entered = true;
		mouseCurrentColor = mouseEnteredColor;
		asciiPanel.repaint(getBounds());
	}

	@Override
	public void mouseExited(MouseEvent e) {
		entered = false;
		mouseCurrentColor = mouseDefaultColor;
		asciiPanel.repaint(getBounds());
	}
	
	@Override
	public void paint(Graphics g) {
		asciiPanel.writeString(x, y, name, mouseCurrentColor, mouseBackgroundColor != null ? mouseBackgroundColor : asciiPanel.getDefaultCharacterBackgroundColor());
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	public void setMouseDefaultColor(Color mouseDefaultColor) {
		this.mouseDefaultColor = mouseDefaultColor;
		if(!entered) {
			mouseCurrentColor = mouseDefaultColor;
			asciiPanel.repaint(getBounds());
		}
	}
	
	public void setMouseEnteredColor(Color mouseEnteredColor) {
		this.mouseEnteredColor = mouseEnteredColor;
		if(entered) {
			mouseCurrentColor = mouseEnteredColor;
			asciiPanel.repaint(getBounds());
		}
	}
}
