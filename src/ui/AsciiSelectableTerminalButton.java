package ui;

import java.awt.Color;
import java.awt.event.MouseEvent;

/**
 * A button selectable.
 * 
 * @author julien MAITRE
 * 
 */
public class AsciiSelectableTerminalButton extends AsciiTerminalButton {
	private boolean select = false;
	private Color mouseSelectColor;

	public AsciiSelectableTerminalButton(AsciiPanel asciiPanel, String label, int x, int y, Color mouseDefaultColor, Color mouseEnteredColor, Color mouseSelectColor) {
		super(asciiPanel, label, x, y, mouseDefaultColor, mouseEnteredColor);
		this.mouseSelectColor = mouseSelectColor;
	}
	
	public AsciiSelectableTerminalButton(AsciiPanel asciiPanel, String label, int x, int y, Color mouseDefaultColor, Color mouseEnteredColor, Color mouseSelectColor, Color mouseBackgroundColor) {
		super(asciiPanel, label, x, y, mouseDefaultColor, mouseEnteredColor, mouseBackgroundColor);
		this.mouseSelectColor = mouseSelectColor;
	}

	public void setSelect(boolean select) {
		this.select = select;
		changeColor();
	}
	
	public boolean isSelect() {
		return select;
	}

	@Override
	public void mouseExited(MouseEvent e) {
		super.mouseExited(e);
		changeColor();
	}
	
	private void changeColor() {
		if(select) {
			mouseCurrentColor = mouseSelectColor;
		}
		else {
			mouseCurrentColor = mouseDefaultColor;
		}
		asciiPanel.repaint(getBounds());
	}
}
