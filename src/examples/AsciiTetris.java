package examples;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import ui.AsciiTerminal;

import javax.swing.*;
import java.io.*;
import java.util.BitSet;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

class Leaderboards implements Serializable {
	private static final long serialVersionUID = 2274204318785895973L;
	public LinkedList<Integer> scores = new LinkedList<>();
}

public class AsciiTetris extends Game {
	private static final int WINDOW_WIDTH = 21;
	private static final int WINDOW_HEIGHT = 24;

	private static final String LEADERBOARD_SAVE_FILE = "saveAsciiTetris.bin";

	private static final char BLOC_TILE = 0;
	private static final int PLAYFIELD_WIDTH = 10;
	private static final int PLAYFIELD_HEIGHT = 22;
	private static final int DISPLAY_PLAYFIELD_HEIGHT = 20;

	private static final float SOFT_DROP_SPEED = 0.05f;
	private static final int SOFT_DROP_BONUS_GridPoint2 = 6;

	private static final float REPEAT_KEY_EVENT = 5f;

	private AsciiTerminal asciiTerminal;

	private Random rand = new Random();

	private Leaderboards leaderboards = new Leaderboards();
	private GameState gameState = GameState.MENU;

	private int score = 0;
	private int level = 0;
	private int scoreLevel = 0;
	private Color[][] cells = new Color[PLAYFIELD_WIDTH][PLAYFIELD_HEIGHT];

	private double timer = 0d;
	private GridPoint2 currentPosition = null;
	private int currentDirection = 0;

	private Tetrimino nextTetrimino = null;
	private Tetrimino currentTetrimino = null;
	private int countSameTetrimino = 0;

	private int event = 0;

	// Instant key event for 1 press event
	private BitSet instantKeyEvents = new BitSet();
	// Continue key event for long press event
	private BitSet continueKeyEvents = new BitSet();
	private double timerKeyEvent = 0d;
	private double waitEvent = REPEAT_KEY_EVENT;

	private int menuPosition = 0;

	enum Tetrimino {
		I(Color.CYAN, 4, new GridPoint2(3, 1), new GridPoint2[][]{
			new GridPoint2[]{new GridPoint2(0,1), new GridPoint2(1,1), new GridPoint2(2,1), new GridPoint2(3,1)},
			new GridPoint2[]{new GridPoint2(2,0), new GridPoint2(2,1), new GridPoint2(2,2), new GridPoint2(2,3)},
			new GridPoint2[]{new GridPoint2(0,2), new GridPoint2(1,2), new GridPoint2(2,2), new GridPoint2(3,2)},
			new GridPoint2[]{new GridPoint2(1,0), new GridPoint2(1,1), new GridPoint2(1,2), new GridPoint2(1,3)}
		}),
		O(Color.YELLOW, 4, new GridPoint2(3, 2), new GridPoint2[][]{
			new GridPoint2[]{new GridPoint2(1,0), new GridPoint2(2,0), new GridPoint2(1,1), new GridPoint2(2,1)}
		}),
		T(Color.MAGENTA, 3, new GridPoint2(3, 2), new GridPoint2[][]{
			new GridPoint2[]{new GridPoint2(1,0), new GridPoint2(0,1), new GridPoint2(1,1), new GridPoint2(2,1)},
			new GridPoint2[]{new GridPoint2(1,0), new GridPoint2(1,1), new GridPoint2(2,1), new GridPoint2(1,2)},
			new GridPoint2[]{new GridPoint2(0,1), new GridPoint2(1,1), new GridPoint2(2,1), new GridPoint2(1,2)},
			new GridPoint2[]{new GridPoint2(1,0), new GridPoint2(0,1), new GridPoint2(1,1), new GridPoint2(1,2)},
		}),
		L(Color.ORANGE, 3, new GridPoint2(3, 2), new GridPoint2[][]{
			new GridPoint2[]{new GridPoint2(2,0), new GridPoint2(0,1), new GridPoint2(1,1), new GridPoint2(2,1)},
			new GridPoint2[]{new GridPoint2(1,0), new GridPoint2(1,1), new GridPoint2(1,2), new GridPoint2(2,2)},
			new GridPoint2[]{new GridPoint2(0,1), new GridPoint2(1,1), new GridPoint2(2,1), new GridPoint2(0,2)},
			new GridPoint2[]{new GridPoint2(0,0), new GridPoint2(1,0), new GridPoint2(1,1), new GridPoint2(1,2)}
		}),
		J(Color.BLUE, 3, new GridPoint2(3, 2), new GridPoint2[][]{
			new GridPoint2[]{new GridPoint2(0,0), new GridPoint2(0,1), new GridPoint2(1,1), new GridPoint2(2,1)},
			new GridPoint2[]{new GridPoint2(1,0), new GridPoint2(2,0), new GridPoint2(1,1), new GridPoint2(1,2)},
			new GridPoint2[]{new GridPoint2(0,1), new GridPoint2(1,1), new GridPoint2(2,1), new GridPoint2(2,2)},
			new GridPoint2[]{new GridPoint2(1,0), new GridPoint2(1,1), new GridPoint2(0,2), new GridPoint2(1,2)}
		}),
		Z(Color.RED, 3, new GridPoint2(3, 2), new GridPoint2[][]{
			new GridPoint2[]{new GridPoint2(0,0), new GridPoint2(1,0), new GridPoint2(1,1), new GridPoint2(2,1)},
			new GridPoint2[]{new GridPoint2(2,0), new GridPoint2(1,1), new GridPoint2(2,1), new GridPoint2(1,2)},
			new GridPoint2[]{new GridPoint2(0,1), new GridPoint2(1,1), new GridPoint2(1,2), new GridPoint2(2,2)},
			new GridPoint2[]{new GridPoint2(1,0), new GridPoint2(0,1), new GridPoint2(1,1), new GridPoint2(0,2)}
		}),
		S(Color.GREEN, 3, new GridPoint2(3, 2), new GridPoint2[][]{
			new GridPoint2[]{new GridPoint2(1,0), new GridPoint2(2,0),new GridPoint2(0,1),new GridPoint2(1,1)},
			new GridPoint2[]{new GridPoint2(1,0), new GridPoint2(1,1),new GridPoint2(2,1),new GridPoint2(2,2)},
			new GridPoint2[]{new GridPoint2(1,1), new GridPoint2(2,1),new GridPoint2(0,2),new GridPoint2(1,2)},
			new GridPoint2[]{new GridPoint2(0,0), new GridPoint2(0,1),new GridPoint2(1,1),new GridPoint2(1,2)},
		});

		public Color color;
		public int width;
		public GridPoint2 startPosition;
		public GridPoint2[][] position;

		private Tetrimino(Color color, int width, GridPoint2 startPosition, GridPoint2[][] position) {
			this.color = color;
			this.width = width;
			this.startPosition = startPosition;
			this.position = position;
		}
	}

	enum GameState {
		MENU,
		LEADERBOARDS,
		START,
		PLAY,
		PAUSE,
		GAME_OVER
	}

//	public AsciiTetris(String tileset, int characterWidth, int characterHeight, int scale) throws Exception {
//
//
//
//		asciiTerminal.addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyPressed(KeyEvent e) {
//				if(!continueKeyEvents.get(e)) {
//					instantKeyEvents.set(e);
//				}
//				continueKeyEvents.set(e);
//				event = e;
//			}
//
//			@Override
//			public void keyReleased(KeyEvent e) {
//				continueKeyEvents.clear(e);
//			}
//		});
//	}

	@Override
	public void create() {
		String[] choiceTileset = { "Anikki [8x8]", "Yoshis island [9x12]", "Vidumec [15x15]", "Wanderlust [16x16]", "Curses square [24x24]" };
		JComboBox<String> comboChoiceTileset = new JComboBox<>(choiceTileset);

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
				init("Anikki_square_8x8.png", 8, 8, scale);
			}
			else if(comboChoiceTileset.getSelectedItem().equals("Yoshis island [9x12]")) {
				init("Yoshis_island_9x12.png", 9, 12, scale);
			}
			else if(comboChoiceTileset.getSelectedItem().equals("Vidumec [15x15]")) {
				init("Vidumec_15x15.png", 15, 15, scale);
			}
			else if(comboChoiceTileset.getSelectedItem().equals("Wanderlust [16x16]")) {
				init("wanderlust_16x16.png", 16, 16, scale);
			}
			else {
				init("Curses_square_24x24.png", 24, 24, scale);
			}
		}


		try {
			loadLeaderboards();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init(String tilesetFile, int characterWidth, int characterHeight, int scale) {
		asciiTerminal = new AsciiTerminal("AsciiTetris", WINDOW_WIDTH, WINDOW_HEIGHT, tilesetFile, characterWidth, characterHeight, scale);
		Gdx.graphics.setWindowedMode(AsciiUISample.WINDOW_WIDTH * characterWidth * scale, AsciiUISample.WINDOW_HEIGHT * characterHeight * scale);

		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				if(!continueKeyEvents.get(keycode)) {
					instantKeyEvents.set(keycode);
				}
				continueKeyEvents.set(keycode);
				event = keycode;
				return true;
			}

			@Override
			public boolean keyUp(int keycode) {
				continueKeyEvents.clear(keycode);
				return true;
			}
		});

		super.setScreen(asciiTerminal);
	}

	private void loadLeaderboards() throws Exception {
		File file = new File(LEADERBOARD_SAVE_FILE);
		if(file.exists() && file.canRead()) {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			leaderboards = (Leaderboards)ois.readObject();
			ois.close();
		}
		else if(file.exists() && !file.canWrite()) {
			JOptionPane.showMessageDialog(null, "Unable to load save file. You don't have persmision to load it.", "AsciiTetris Message", JOptionPane.ERROR_MESSAGE);
		}
		else {
			for(int i = 0; i < 5; i++) {
				leaderboards.scores.add(0);
				saveLeaderboards();
			}
		}
	}

	private void saveLeaderboards() {
		File file = new File(LEADERBOARD_SAVE_FILE);
		if(file.exists() && file.canWrite() || !file.exists()) {
			ObjectOutputStream out = null;
			try {
				out = new ObjectOutputStream(new FileOutputStream(file));
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(out != null) {
				try {
					out.writeObject(leaderboards);
				} catch (IOException e) {
					e.printStackTrace();
				}
				finally {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void render() {
		/*
		 * DRAW BASE
		 */
		asciiTerminal.clear();

		// PLAYFIELD BORDER
		Color color = Color.DARK_GRAY;
		asciiTerminal.write(1, 1, (char)201, color);
		asciiTerminal.write(12, 1, (char)187, color);
		asciiTerminal.write(1, 22, (char)200, color);
		asciiTerminal.write(12, 22, (char)188, color);
		for(int i = 0; i < 10; i++) {
			asciiTerminal.write(2+i, 1, (char)205, color);
			asciiTerminal.write(2+i, 22, (char)205, color);
		}
		for(int j = 0; j < 20; j++) {
			asciiTerminal.write(1, 2+j, (char)186, color);
			asciiTerminal.write(12, 2+j, (char)186, color);
		}

		// SCORE
		asciiTerminal.writeString(14, 2, "SCORE", Color.BLUE);
		asciiTerminal.writeString(14, 3, String.format("%06d", score), Color.CYAN);

		// LEVEL
		asciiTerminal.writeString(14, 5, "LEVEL", Color.BLUE);
		asciiTerminal.writeString(14, 6, String.format("%06d", level), Color.CYAN);

		// NEXT TETROMINO BORDER
		asciiTerminal.writeString(15, 8, "NEXT", Color.BLUE);
		asciiTerminal.write(14, 9, (char)218, color);
		asciiTerminal.write(19, 9, (char)191, color);
		asciiTerminal.write(14, 12, (char)192, color);
		asciiTerminal.write(19, 12, (char)217, color);
		for(int i = 0; i < 4; i++) {
			asciiTerminal.write(15+i, 9, (char)196, color);
			asciiTerminal.write(15+i, 12, (char)196, color);
		}
		for(int j = 0; j < 2; j++) {
			asciiTerminal.write(14, 10+j, (char)179, color);
			asciiTerminal.write(19, 10+j, (char)179, color);
		}

		if(gameState == GameState.MENU) {
			menuGame();
		}
		else if(gameState == GameState.LEADERBOARDS) {
			leaderboardsGame();
		}
		else if(gameState == GameState.START) {
			startGame();
		}
		else if(gameState == GameState.PLAY) {
			playGame(Gdx.graphics.getDeltaTime());
		}
		else if(gameState == GameState.PAUSE) {
			pauseGame();
		}
		else if(gameState == GameState.GAME_OVER) {
			gameOverGame();
		}

		super.render();
	}

	@Override
	public void dispose() {
		super.dispose();
		asciiTerminal.dispose();
	}

	private void menuGame() {
		if(event != 0) {
			if(event == Input.Keys.ENTER) {
				switch (menuPosition) {
					case 0:
						gameState = GameState.START;
						break;

					case 1:
						gameState = GameState.LEADERBOARDS;
						break;

					case 2:
						asciiTerminal.dispose();
						System.exit(0);
						break;

					default:
						break;
				}
			}

			else if(event == Input.Keys.UP) {
				menuPosition--;
				if(menuPosition < 0) {
					menuPosition = 2;
				}
			}
			else if(event == Input.Keys.DOWN) {
				menuPosition++;
			}

			event = 0;
		}
		menuPosition %= 3;


		asciiTerminal.writeString(5, 7, "MENU", Color.WHITE);

		asciiTerminal.writeString(5, 9, "START", Color.GRAY);
		asciiTerminal.writeString(1, 10, "LEADERBOARDS", Color.GRAY);
		asciiTerminal.writeString(5, 11, "EXIT", Color.GRAY);
		switch (menuPosition) {
			case 0:
				asciiTerminal.writeString(5, 9, "START", Color.WHITE);
				break;

			case 1:
				asciiTerminal.writeString(1, 10, "LEADERBOARDS", Color.WHITE);
				break;

			case 2:
				asciiTerminal.writeString(5, 11, "EXIT", Color.WHITE);
				break;

			default:
				break;
		}
		asciiTerminal.writeString(1, WINDOW_HEIGHT-1, "ENTER:SELECT", Color.GREEN);
	}

	private void leaderboardsGame() {
		if(event != 0) {
			if(event == Input.Keys.ESCAPE) {
				gameState = GameState.MENU;
			}

			event = 0;
		}


		asciiTerminal.writeString(1, 6, "LEADERBOARDS", Color.WHITE);
		for(int i = 0; i < 5; i++) {
			asciiTerminal.writeString(2, 8+i, (i+1)+".", Color.WHITE);
			String value = leaderboards.scores.get(i).toString();
			asciiTerminal.writeString(12-value.length(), 8+i, value, Color.WHITE);
		}
		asciiTerminal.writeString(1, WINDOW_HEIGHT-1, "ESC:MENU", Color.GREEN);
	}

	private void startGame() {
		menuPosition = 0;
		score = 0;
		level = 0;
		scoreLevel = 0;
		cells = new Color[PLAYFIELD_WIDTH][PLAYFIELD_HEIGHT];
		timer = 0d;
		currentPosition = null;
		currentDirection = 0;
		nextTetrimino = null;
		currentTetrimino = null;
		countSameTetrimino = 0;

		newTetrimino();

		gameState = GameState.PLAY;
	}

	private void playGame(double delta) {
		/*
		 * UPDATE
		 */
		boolean softDrop = false;

		// KEY EVENT
		if(event != 0) {
			if(event == Input.Keys.UP) {
				int nextDirection = (currentDirection + 1) % currentTetrimino.position.length;

				if(isFreeTetriminoPosition(currentPosition, nextDirection)) {
					currentDirection = nextDirection;
				}
				else {
					GridPoint2 nextPosition = new GridPoint2(currentPosition.x - 1, currentPosition.y);
					if(isFreeTetriminoPosition(nextPosition, nextDirection)) {
						currentPosition = nextPosition;
						currentDirection = nextDirection;
					}
					else {
						nextPosition = new GridPoint2(currentPosition.x + 1, currentPosition.y);
						if(isFreeTetriminoPosition(nextPosition, nextDirection)) {
							currentPosition = nextPosition;
							currentDirection = nextDirection;
						}
					}
				}
			}
			else if(event == Input.Keys.ESCAPE) {
				gameState = GameState.PAUSE;
				return;
			}

			event = 0;
		}



		if(continueKeyEvents.isEmpty()) {
			timerKeyEvent = 0;
			waitEvent = REPEAT_KEY_EVENT;
		}
		else {
			timerKeyEvent += delta;
			waitEvent = Math.max(waitEvent, REPEAT_KEY_EVENT);
		}

		if((timerKeyEvent >= waitEvent && !continueKeyEvents.isEmpty()) || !instantKeyEvents.isEmpty()) {
			timerKeyEvent %= waitEvent;
			waitEvent = 0;

			// If key just touch, need to wait a little more time for the next move for repeat action
			if(instantKeyEvents.get(Input.Keys.LEFT) || instantKeyEvents.get(Input.Keys.RIGHT)) {
				waitEvent = REPEAT_KEY_EVENT * 2;
			}

			if(continueKeyEvents.get(Input.Keys.LEFT)) {
				instantKeyEvents.clear(Input.Keys.LEFT);

				boolean goLeft = true;
				for(GridPoint2 p : currentTetrimino.position[currentDirection]) {
					if(p.x + currentPosition.x - 1 < 0) {
						goLeft = false;
						break;
					}
					Color color = cells[p.x + currentPosition.x - 1][p.y + currentPosition.y];
					if(color != null) {
						goLeft = false;
						break;
					}
				}

				if(goLeft) {
					currentPosition.x -= 1;
				}
			}
			if(continueKeyEvents.get(Input.Keys.RIGHT)) {
				instantKeyEvents.clear(Input.Keys.RIGHT);

				boolean goRight = true;
				for(GridPoint2 p : currentTetrimino.position[currentDirection]) {
					if(p.x + currentPosition.x + 1 >= PLAYFIELD_WIDTH) {
						goRight = false;
						break;
					}
					Color color = cells[p.x + currentPosition.x + 1][p.y + currentPosition.y];
					if(color != null) {
						goRight = false;
						break;
					}
				}

				if(goRight) {
					currentPosition.x += 1;
				}
			}
			if(continueKeyEvents.get(Input.Keys.DOWN)) {
				instantKeyEvents.clear(Input.Keys.DOWN);
				softDrop = true;
			}

			instantKeyEvents.clear();
		}




		// DROP SPEED & SOFT DROP
		double tickDuration = tickDuration()*100;
		if(softDrop) {
			tickDuration = SOFT_DROP_SPEED;
		}
		else {
			timer += delta;
		}

		// TICK
		if(timer >= tickDuration) {
			timer -= tickDuration;
			if(softDrop) {
				score += SOFT_DROP_BONUS_GridPoint2;
			}

			boolean goDown = true;
			for(GridPoint2 p : currentTetrimino.position[currentDirection]) {
				if(p.y + currentPosition.y + 1 >= PLAYFIELD_HEIGHT) {
					goDown = false;
					break;
				}
				Color color = cells[p.x + currentPosition.x][p.y + currentPosition.y + 1];
				if(color != null) {
					goDown = false;
					break;
				}
			}

			if(goDown) {
				currentPosition.y += 1;
			}
			else {
				for(GridPoint2 p : currentTetrimino.position[currentDirection]) {
					cells[p.x+currentPosition.x][p.y+currentPosition.y] = currentTetrimino.color;
				}

				int fullLineCount = 0;
				int y = PLAYFIELD_HEIGHT-1;
				while(y > 1) {
					boolean fullLine = true;
					for(int x = 0; x < PLAYFIELD_WIDTH; x++) {
						if(cells[x][y] == null) {
							fullLine = false;
							break;
						}
					}

					if(fullLine) {
						fullLineCount++;
						for(int y2 = y-1; y2 > 1; y2--) {
							for(int x2 = 0; x2 < PLAYFIELD_WIDTH; x2++) {
								cells[x2][y2+1] = cells[x2][y2];
							}
						}
					}
					else {
						y--;
					}
				}

				scoring(fullLineCount);

				// Test if game over
				for(int x = 0; x < PLAYFIELD_WIDTH; x++) {
					for(y = 0; y < PLAYFIELD_HEIGHT-DISPLAY_PLAYFIELD_HEIGHT; y++) {
						if(cells[x][y] != null) {
							gameState = GameState.GAME_OVER;

							leaderboards.scores.add(score);
							leaderboards.scores.sort(Collections.reverseOrder());
							leaderboards.scores = new LinkedList<>(leaderboards.scores.subList(0, 5));

							saveLeaderboards();
							return;
						}
					}
				}

				newTetrimino();
			}
		}

		// Find the shadow tetrimino position
		boolean isShadowPosition = false;
		GridPoint2 shadowPosition = new GridPoint2(currentPosition);
		do {
			for(GridPoint2 p : currentTetrimino.position[currentDirection]) {
				if(p.y + shadowPosition.y + 1 >= PLAYFIELD_HEIGHT) {
					isShadowPosition = true;
					break;
				}
				Color color = cells[p.x + shadowPosition.x][p.y + shadowPosition.y + 1];
				if(color != null) {
					isShadowPosition = true;
					break;
				}
			}
			if(!isShadowPosition) {
				shadowPosition = new GridPoint2(shadowPosition.x, shadowPosition.y+1);
			}
		}while(!isShadowPosition);









		/*
		 * DRAW
		 */

		// PLAYFIELD
		int xOffset = 2;
		int yOffset = 2;
		for(int i = 0; i < PLAYFIELD_WIDTH; i++) {
			for(int j = PLAYFIELD_HEIGHT - DISPLAY_PLAYFIELD_HEIGHT; j < PLAYFIELD_HEIGHT; j++) {
				if(cells[i][j] != null) {
					asciiTerminal.write(i + xOffset, j + yOffset - (PLAYFIELD_HEIGHT - DISPLAY_PLAYFIELD_HEIGHT), BLOC_TILE, Color.WHITE, cells[i][j]);
				}
			}
		}

		// SHADOW TETRIMINO
		Color shadowColor = new Color(currentTetrimino.color.r/2, currentTetrimino.color.g/2, currentTetrimino.color.b/2, 1.0f);
		for(GridPoint2 p : currentTetrimino.position[currentDirection]) {
			if(p.y + currentPosition.y + yOffset - 2 > 1) {
				asciiTerminal.write(p.x+shadowPosition.x+xOffset, p.y+shadowPosition.y+yOffset - 2, BLOC_TILE, Color.WHITE, shadowColor);
			}
		}

		// TETRIMINO
		for(GridPoint2 p : currentTetrimino.position[currentDirection]) {
			if(p.y + currentPosition.y + yOffset - 2 > 1) {
				asciiTerminal.write(p.x+currentPosition.x+xOffset, p.y+currentPosition.y+yOffset - 2, BLOC_TILE, Color.WHITE, currentTetrimino.color);
			}
		}

		// NEXT TETROMINO
		for(GridPoint2 p : nextTetrimino.position[0]) {
			asciiTerminal.write(15+p.x, 10+p.y, BLOC_TILE, Color.WHITE, nextTetrimino.color);
		}
		asciiTerminal.writeString(1, WINDOW_HEIGHT-1, "ESC:PAUSE", Color.GREEN);
	}

	private void pauseGame() {
		if(event != 0) {
			if(event == Input.Keys.ENTER) {
				switch (menuPosition) {
					case 0:
						gameState = GameState.PLAY;
						break;

					case 1:
						gameState = GameState.MENU;
						break;

					case 2:
						asciiTerminal.dispose();
						System.exit(0);
						break;

					default:
						break;
				}
			}

			else if(event == Input.Keys.UP) {
				menuPosition--;
				if(menuPosition < 0) {
					menuPosition = 2;
				}
			}
			else if(event == Input.Keys.DOWN) {
				menuPosition++;
			}

			event = 0;
		}

		menuPosition %= 3;

		asciiTerminal.writeString(4, 7, "PAUSE", Color.WHITE);

		asciiTerminal.writeString(3, 9, "CONTINUE", Color.GRAY);
		asciiTerminal.writeString(5, 10, "MENU", Color.GRAY);
		asciiTerminal.writeString(5, 11, "EXIT", Color.GRAY);
		switch (menuPosition) {
			case 0:
				asciiTerminal.writeString(3, 9, "CONTINUE", Color.WHITE);
				break;

			case 1:
				asciiTerminal.writeString(5, 10, "MENU", Color.WHITE);
				break;

			case 2:
				asciiTerminal.writeString(5, 11, "EXIT", Color.WHITE);
				break;

			default:
				break;
		}
		asciiTerminal.writeString(1, WINDOW_HEIGHT-1, "ENTER:SELECT", Color.GREEN);
	}

	private void gameOverGame() {
		if(event != 0) {
			if(event == Input.Keys.ENTER) {
				switch (menuPosition) {
					case 0:
						gameState = GameState.START;
						break;

					case 1:
						gameState = GameState.MENU;
						break;

					case 2:
						asciiTerminal.dispose();
						System.exit(0);
						break;

					default:
						break;
				}
			}

			else if(event == Input.Keys.UP) {
				menuPosition--;
				if(menuPosition < 0) {
					menuPosition = 2;
				}
			}
			else if(event == Input.Keys.DOWN) {
				menuPosition++;
			}

			event = 0;
		}

		menuPosition %= 3;

		asciiTerminal.writeString(3, 7, "GAME OVER", Color.WHITE);

		asciiTerminal.writeString(4, 9, "SCORE", Color.GRAY);
		asciiTerminal.writeString(4, 10, String.format("%06d", score), Color.CYAN);

		asciiTerminal.writeString(4, 12, "RETRY", Color.GRAY);
		asciiTerminal.writeString(5, 13, "MENU", Color.GRAY);
		asciiTerminal.writeString(5, 14, "EXIT", Color.GRAY);
		switch (menuPosition) {
			case 0:
				asciiTerminal.writeString(4, 12, "RETRY", Color.WHITE);
				break;

			case 1:
				asciiTerminal.writeString(5, 13, "MENU", Color.WHITE);
				break;

			case 2:
				asciiTerminal.writeString(5, 14, "EXIT", Color.WHITE);
				break;

			default:
				break;
		}
		asciiTerminal.writeString(1, WINDOW_HEIGHT-1, "ENTER:SELECT", Color.GREEN);
	}

	private void newTetrimino() {
		Tetrimino[] tetriminos = Tetrimino.values();
		boolean change;
		do {
			if(nextTetrimino != null) {
				currentTetrimino = nextTetrimino;
				nextTetrimino = tetriminos[rand.nextInt(tetriminos.length)];
			}
			else {
				nextTetrimino = tetriminos[rand.nextInt(tetriminos.length)];
				currentTetrimino = tetriminos[rand.nextInt(tetriminos.length)];
			}

			if(nextTetrimino == currentTetrimino && countSameTetrimino < 1) {
				countSameTetrimino++;
			}
			if(nextTetrimino == currentTetrimino && countSameTetrimino >= 1) {
				change = true;
			}
			else {
				countSameTetrimino = 0;
				change = false;
			}
		}while(change);

		currentDirection = 0;
		GridPoint2 startPosition = currentTetrimino.startPosition.cpy();
		for(int i = currentTetrimino.startPosition.y; i >= 0; i--) {
			startPosition.y = i;
			if(isFreeTetriminoPosition(startPosition, currentDirection)) {
				currentPosition = startPosition;
				return;
			}
		}
	}

	private boolean isFreeTetriminoPosition(GridPoint2 position, int direction) {
		boolean turn = true;
		for(GridPoint2 p : currentTetrimino.position[direction]) {
			if(p.x + position.x >= 0 && p.x + position.x < PLAYFIELD_WIDTH && p.y + position.y >= 0 && p.y + position.y < PLAYFIELD_HEIGHT) {
				Color color = cells[p.x + position.x][p.y + position.y];
				if(color != null) {
					turn = false;
					break;
				}
			}
			else {
				turn = false;
				break;
			}
		}

		return turn;
	}

	private double tickDuration() {
		return Math.pow((0.8-((level-1)*0.007)),(level-1));
	}

	private void scoring(int lines) {
		if(lines == 1) {
			score += 40 * (level + 1);
			scoreLevel += 1;
		}
		else if(lines == 2) {
			score += 100 * (level + 1);
			scoreLevel += 3;
		}
		else if(lines == 3) {
			score += 300 * (level + 1);
			scoreLevel += 5;
		}
		else if(lines == 4) {
			score += 1200 * (level + 1);
			scoreLevel += 8;
		}

		level = suiteScoreLevel(0, scoreLevel);
	}

	private int suiteScoreLevel(int level, int scoreLevel) {
		if(scoreLevel >= (level + 1) * 5) {
			return suiteScoreLevel(level+1, scoreLevel - ((level + 1) * 5));
		}
		else {
			return level;
		}
	}
}
