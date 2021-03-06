package com.asciiterminal.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * A simple button
 * 
 * @author julien MAITRE
 *
 */
public class AsciiTerminalButton extends Actor {
	protected final AsciiTerminal asciiTerminal;
	protected int x;
	protected int y;
	protected ClickListener clickListener;
	protected boolean isDisabled = false;
	protected Color mouseDefaultColor;
	protected Color mouseClickedColor;
	protected Color mouseEnteredColor;
	protected Color mouseDisabledColor;
	protected Color mouseBackgroundColor;

	public AsciiTerminalButton(AsciiTerminal asciiTerminal, String name, int x, int y, Color mouseDefaultColor, Color mouseClickedColor) {
		this(asciiTerminal, name, x, y, mouseDefaultColor, mouseClickedColor, asciiTerminal.getDefaultCharacterBackgroundColor());
	}

	public AsciiTerminalButton(AsciiTerminal asciiTerminal, String name, int x, int y, Color mouseDefaultColor, Color mouseClickedColor, Color mouseBackgroundColor) {
		this(asciiTerminal, name, x, y, mouseDefaultColor, mouseClickedColor, mouseDefaultColor, mouseBackgroundColor);
	}

	public AsciiTerminalButton(AsciiTerminal asciiTerminal, String name, int x, int y, Color mouseDefaultColor, Color mouseClickedColor, Color mouseEnteredColor, Color mouseBackgroundColor) {
		this(asciiTerminal, name, x, y, mouseDefaultColor, mouseClickedColor, mouseEnteredColor, mouseDefaultColor, mouseBackgroundColor);
	}

	public AsciiTerminalButton(AsciiTerminal asciiTerminal, String name, int x, int y, Color mouseDefaultColor, Color mouseClickedColor, Color mouseEnteredColor, Color mouseDisabledColor, Color mouseBackgroundColor) {
		this.asciiTerminal = asciiTerminal;
		this.x = x;
		this.y = y;
		this.mouseDefaultColor = mouseDefaultColor;
		this.mouseClickedColor = mouseClickedColor;
		this.mouseEnteredColor = mouseEnteredColor;
		this.mouseDisabledColor = mouseDisabledColor;
		this.mouseBackgroundColor = mouseBackgroundColor;

		this.setName(name);

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
		this.setY((asciiTerminal.getHeight() - y - 1) * asciiTerminal.getCharacterHeight() * asciiTerminal.getScale());
		this.setWidth(this.getName().length() * asciiTerminal.getCharacterWidth() * asciiTerminal.getScale());
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

		asciiTerminal.writeString(x, y, this.getName(), current, this.mouseBackgroundColor);
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

	public Color getMouseDefaultColor() {
		return mouseDefaultColor;
	}

	public void setMouseDefaultColor(Color mouseDefaultColor) {
		this.mouseDefaultColor = mouseDefaultColor;
	}

	public Color getMouseClickedColor() {
		return mouseClickedColor;
	}

	public void setMouseClickedColor(Color mouseClickedColor) {
		this.mouseClickedColor = mouseClickedColor;
	}

	public Color getMouseEnteredColor() {
		return mouseEnteredColor;
	}

	public void setMouseEnteredColor(Color mouseEnteredColor) {
		this.mouseEnteredColor = mouseEnteredColor;
	}

	public Color getMouseDisabledColor() {
		return mouseDisabledColor;
	}

	public void setMouseDisabledColor(Color mouseDisabledColor) {
		this.mouseDisabledColor = mouseDisabledColor;
	}

	public Color getMouseBackgroundColor() {
		return mouseBackgroundColor;
	}

	public void setMouseBackgroundColor(Color mouseBackgroundColor) {
		this.mouseBackgroundColor = mouseBackgroundColor;
	}
}
