package ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * A simple button
 * 
 * @author julien MAITRE
 *
 */
public class AsciiTerminalButton extends Actor {
	protected final AsciiPanel asciiPanel;
	private String label;
	private int x;
	private int y;
	private ClickListener clickListener;
	private boolean isDisabled = false;
	protected Color mouseDefaultColor;
	protected Color mouseClickedColor;
	protected Color mouseEnteredColor;
	protected Color mouseDisabledColor;
	protected Color mouseBackgroundColor;

	public AsciiTerminalButton(AsciiPanel asciiPanel, String label, int x, int y, Color mouseDefaultColor, Color mouseClickedColor) {
		this(asciiPanel, label, x, y, mouseDefaultColor, mouseClickedColor, asciiPanel.getDefaultCharacterBackgroundColor());
	}

	public AsciiTerminalButton(AsciiPanel asciiPanel, String label, int x, int y, Color mouseDefaultColor, Color mouseClickedColor, Color mouseBackgroundColor) {
		this(asciiPanel, label, x, y, mouseDefaultColor, mouseClickedColor, mouseDefaultColor, mouseBackgroundColor);
	}

	public AsciiTerminalButton(AsciiPanel asciiPanel, String label, int x, int y, Color mouseDefaultColor, Color mouseClickedColor, Color mouseEnteredColor, Color mouseBackgroundColor) {
		this(asciiPanel, label, x, y, mouseDefaultColor, mouseClickedColor, mouseEnteredColor, mouseDefaultColor, mouseBackgroundColor);
	}

	public AsciiTerminalButton(AsciiPanel asciiPanel, String label, int x, int y, Color mouseDefaultColor, Color mouseClickedColor, Color mouseEnteredColor, Color mouseDisabledColor, Color mouseBackgroundColor) {
		this.asciiPanel = asciiPanel;
		this.label = label;
		this.x = x;
		this.y = y;
		this.mouseDefaultColor = mouseDefaultColor;
		this.mouseClickedColor = mouseClickedColor;
		this.mouseEnteredColor = mouseEnteredColor;
		this.mouseDisabledColor = mouseDisabledColor;
		this.mouseBackgroundColor = mouseBackgroundColor;

		this.setX(x * asciiPanel.getCharacterWidth() * asciiPanel.getScale());
		this.setY(y * asciiPanel.getCharacterHeight() * asciiPanel.getScale());
		this.setWidth(label.length() * asciiPanel.getCharacterWidth() * asciiPanel.getScale());
		this.setHeight(asciiPanel.getCharacterHeight() * asciiPanel.getScale());

		initialize();
	}

	private void initialize () {
		setTouchable(Touchable.enabled);
		addListener(clickListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(isDisabled()) return;
			}
		});
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		boolean isDisabled = isDisabled();
		boolean isPressed = isPressed();
		boolean isOver = isOver();

		Color current = mouseDefaultColor;
		if(isDisabled) {
			current = mouseDisabledColor;
		}
		else if(isPressed) {
			current = mouseClickedColor;
		}
		else if(isOver) {
			current = mouseEnteredColor;
		}

		asciiPanel.writeString(x, y, this.label, current, this.mouseBackgroundColor);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isDisabled() {
		return isDisabled;
	}

	public void setDisabled(boolean disabled) {
		isDisabled = disabled;
	}

	public boolean isPressed() {
		return clickListener.isVisualPressed();
	}

	public boolean isOver() {
		return clickListener.isOver();
	}

	public ClickListener getClickListener() {
		return clickListener;
	}
}
