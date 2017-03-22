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
	protected final AsciiTerminal asciiTerminal;
	protected String label;
	protected int x;
	protected int y;
	protected ClickListener clickListener;
	protected boolean isDisabled = false;
	protected Color mouseDefaultColor;
	protected Color mouseClickedColor;
	protected Color mouseEnteredColor;
	protected Color mouseDisabledColor;
	protected Color mouseBackgroundColor;

	public AsciiTerminalButton(AsciiTerminal asciiTerminal, String label, int x, int y, Color mouseDefaultColor, Color mouseClickedColor) {
		this(asciiTerminal, label, x, y, mouseDefaultColor, mouseClickedColor, asciiTerminal.getDefaultCharacterBackgroundColor());
	}

	public AsciiTerminalButton(AsciiTerminal asciiTerminal, String label, int x, int y, Color mouseDefaultColor, Color mouseClickedColor, Color mouseBackgroundColor) {
		this(asciiTerminal, label, x, y, mouseDefaultColor, mouseClickedColor, mouseDefaultColor, mouseBackgroundColor);
	}

	public AsciiTerminalButton(AsciiTerminal asciiTerminal, String label, int x, int y, Color mouseDefaultColor, Color mouseClickedColor, Color mouseEnteredColor, Color mouseBackgroundColor) {
		this(asciiTerminal, label, x, y, mouseDefaultColor, mouseClickedColor, mouseEnteredColor, mouseDefaultColor, mouseBackgroundColor);
	}

	public AsciiTerminalButton(AsciiTerminal asciiTerminal, String label, int x, int y, Color mouseDefaultColor, Color mouseClickedColor, Color mouseEnteredColor, Color mouseDisabledColor, Color mouseBackgroundColor) {
		this.asciiTerminal = asciiTerminal;
		this.label = label;
		this.x = x;
		this.y = y;
		this.mouseDefaultColor = mouseDefaultColor;
		this.mouseClickedColor = mouseClickedColor;
		this.mouseEnteredColor = mouseEnteredColor;
		this.mouseDisabledColor = mouseDisabledColor;
		this.mouseBackgroundColor = mouseBackgroundColor;

		sizeChanged();
		initialize();
	}

	protected void initialize () {
		setTouchable(Touchable.enabled);
		addListener(clickListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(isDisabled()) return;
			}
		});
	}

	public void hasSizeChanged() {
		sizeChanged();
	}

	@Override
	protected void sizeChanged() {
		this.setX(x * asciiTerminal.getCharacterWidth() * asciiTerminal.getScale());
		this.setY(y * asciiTerminal.getCharacterHeight() * asciiTerminal.getScale());
		this.setWidth(label.length() * asciiTerminal.getCharacterWidth() * asciiTerminal.getScale());
		this.setHeight(asciiTerminal.getCharacterHeight() * asciiTerminal.getScale());
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

		asciiTerminal.writeString(x, y, this.label, current, this.mouseBackgroundColor);
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
