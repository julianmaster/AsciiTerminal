package com.examples;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import ui.AsciiPanel;

public class AsciiTetris extends Game {

	public static final int WINDOW_WIDTH = 16;
	public static final int WINDOW_HEIGHT = 16;
	public static final int CHARACTER_WIDTH = 8;
	public static final int CHARACTER_HEIGHT = 8;
	public static final int SCALE = 3;

	AsciiPanel asciiPanel;
	
	@Override
	public void create () {
		asciiPanel = new AsciiPanel(WINDOW_WIDTH, WINDOW_HEIGHT, "Anikki_square_8x8.png", CHARACTER_WIDTH, CHARACTER_HEIGHT, SCALE);
//		asciiPanel = new AsciiPanel(WINDOW_WIDTH, WINDOW_HEIGHT, "Yoshis_island_9x12.png", CHARACTER_WIDTH, CHARACTER_HEIGHT);
//		asciiPanel = new AsciiPanel(WINDOW_WIDTH, WINDOW_HEIGHT, "Vidumec_15x15.png", CHARACTER_WIDTH, CHARACTER_HEIGHT);
		super.setScreen(asciiPanel);


		asciiPanel.write(0,0,'a', Color.CYAN, Color.RED);
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		super.dispose();
		asciiPanel.dispose();
	}
}
