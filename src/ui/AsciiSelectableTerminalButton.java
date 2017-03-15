package ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * A button selectable.
 * 
 * @author julien MAITRE
 * 
 */
public class AsciiSelectableTerminalButton extends AsciiTerminalButton {
	private boolean selected = false;
	private boolean justSelected = false;
	private Color mouseSelectedColor;

	public AsciiSelectableTerminalButton(AsciiPanel asciiPanel, String label, int x, int y, Color mouseDefaultColor, Color mouseClickedColor, Color mouseSelectedColor) {
		super(asciiPanel, label, x, y, mouseDefaultColor, mouseClickedColor);
		this.mouseSelectedColor = mouseSelectedColor;
	}

	public AsciiSelectableTerminalButton(AsciiPanel asciiPanel, String label, int x, int y, Color mouseDefaultColor, Color mouseClickedColor, Color mouseSelectedColor, Color mouseBackgroundColor) {
		super(asciiPanel, label, x, y, mouseDefaultColor, mouseClickedColor, mouseBackgroundColor);
		this.mouseSelectedColor = mouseSelectedColor;
	}

	public AsciiSelectableTerminalButton(AsciiPanel asciiPanel, String label, int x, int y, Color mouseDefaultColor, Color mouseClickedColor, Color mouseEnteredColor, Color mouseSelectedColor, Color mouseBackgroundColor) {
		super(asciiPanel, label, x, y, mouseDefaultColor, mouseClickedColor, mouseEnteredColor, mouseBackgroundColor);
		this.mouseSelectedColor = mouseSelectedColor;
	}

	public AsciiSelectableTerminalButton(AsciiPanel asciiPanel, String label, int x, int y, Color mouseDefaultColor, Color mouseClickedColor, Color mouseEnteredColor, Color mouseDisabledColor, Color mouseSelectedColor, Color mouseBackgroundColor) {
		super(asciiPanel, label, x, y, mouseDefaultColor, mouseClickedColor, mouseEnteredColor, mouseDisabledColor, mouseBackgroundColor);
		this.mouseSelectedColor = mouseSelectedColor;
	}

	@Override
	protected void initialize () {
		setTouchable(Touchable.enabled);
		addListener(clickListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(isDisabled()) return;
				selected = !selected;
				justSelected = selected;
			}
		});
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		boolean isDisabled = isDisabled();
		boolean isPressed = isPressed();
		boolean isSelected = isSelected();
		boolean isJustSelected = isJustSelected();
		boolean isOver = isOver();

		Color current = mouseDefaultColor;
		if(isDisabled) {
			current = mouseDisabledColor;
		}
		else if(isPressed) {
			current = mouseClickedColor;
		}
		else if(isJustSelected) {
			current = mouseSelectedColor;
		}
		else if(isOver) {
			current = mouseEnteredColor;
		}
		else if(isSelected) {
			current = mouseSelectedColor;
		}

		if(!isOver && justSelected) {
			justSelected = false;
		}

		asciiPanel.writeString(x, y, this.label, current, this.mouseBackgroundColor);
	}

	public boolean isSelected() {
		return selected;
	}

	public boolean isJustSelected() {
		return justSelected;
	}
}
