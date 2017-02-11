package examples;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.BitSet;
import java.util.Random;

import ui.AsciiPanel;
import ui.AsciiTerminal;

public class AsciiTetris {
	public static final int WINDOW_WIDTH = 21;
	public static final int WINDOW_HEIGHT = 24;
	public static final int SCALE = 3;
	public static final boolean CUSTOM_WINDOW = true;
	public static final String TILESET = "/assets/Yoshis_island_9x12.png";
	public static final int CHARACTER_WIDTH = 9;
	public static final int CHARACTER_HEIGHT = 12;
	public static final int TARGET_FPS = 60;
	public static final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
	
	public static final int PLAYFIELD_WIDTH = 10;
	public static final int PLAYFIELD_HEIGHT = 22;
	public static final int DISPLAY_PLAYFIELD_HEIGHT = 20;
	
	public static final float SOFT_DROP_SPEED = 0.05f;
	public static final int SOFT_DROP_BONUS_POINT = 6;
	
	public static final float REPEAT_KEY_EVENT = 5f;
	
	private AsciiTerminal asciiTerminal;
	private AsciiPanel asciiPanel;
	private KeyEvent event;
	
	private Random rand = new Random();
	
	private GameState gameState = GameState.MENU;
	
	private int score = 0;
	private int level = 0;
	private int scoreLevel = 0;
	private Color[][] cells = new Color[PLAYFIELD_WIDTH][PLAYFIELD_HEIGHT];
	
	private double timer = 0d;
	private Point currentPosition = null;
	private int currentDirection = 0;
	
	private Tetrimino nextTetrimino = null;
	private Tetrimino currentTetrimino = null;
	private int countSameTetrimino = 0;
	
	// Instant key event for 1 press event
	private BitSet instantKeyEvents = new BitSet();
	// Continue key event for long press event
	private BitSet continueKeyEvents = new BitSet();
	private double timerKeyEvent = 0d;
	private double waitEvent = REPEAT_KEY_EVENT;
	
	private int menuPosition = 0;
	
	enum Tetrimino {
		I(Color.CYAN, 4, new Point(3, 1), new Point[][]{
			new Point[]{new Point(0,1), new Point(1,1), new Point(2,1), new Point(3,1)},
			new Point[]{new Point(2,0), new Point(2,1), new Point(2,2), new Point(2,3)},
			new Point[]{new Point(0,2), new Point(1,2), new Point(2,2), new Point(3,2)},
			new Point[]{new Point(1,0), new Point(1,1), new Point(1,2), new Point(1,3)}
		}),
		O(Color.YELLOW, 4, new Point(3, 2), new Point[][]{
			new Point[]{new Point(1,0), new Point(2,0), new Point(1,1), new Point(2,1)}
		}),
		T(Color.MAGENTA, 3, new Point(3, 2), new Point[][]{
			new Point[]{new Point(1,0), new Point(0,1), new Point(1,1), new Point(2,1)},
			new Point[]{new Point(1,0), new Point(1,1), new Point(2,1), new Point(1,2)},
			new Point[]{new Point(0,1), new Point(1,1), new Point(2,1), new Point(1,2)},
			new Point[]{new Point(1,0), new Point(0,1), new Point(1,1), new Point(1,2)},
		}),
		L(Color.ORANGE, 3, new Point(3, 2), new Point[][]{
			new Point[]{new Point(2,0), new Point(0,1), new Point(1,1), new Point(2,1)},
			new Point[]{new Point(1,0), new Point(1,1), new Point(1,2), new Point(2,2)},
			new Point[]{new Point(0,1), new Point(1,1), new Point(2,1), new Point(0,2)},
			new Point[]{new Point(0,0), new Point(1,0), new Point(1,1), new Point(1,2)}
		}),
		J(Color.BLUE, 3, new Point(3, 2), new Point[][]{
			new Point[]{new Point(0,0), new Point(0,1), new Point(1,1), new Point(2,1)},
			new Point[]{new Point(1,0), new Point(2,0), new Point(1,1), new Point(1,2)},
			new Point[]{new Point(0,1), new Point(1,1), new Point(2,1), new Point(2,2)},
			new Point[]{new Point(1,0), new Point(1,1), new Point(0,2), new Point(1,2)}
		}),
		Z(Color.RED, 3, new Point(3, 2), new Point[][]{
			new Point[]{new Point(0,0), new Point(1,0), new Point(1,1), new Point(2,1)},
			new Point[]{new Point(2,0), new Point(1,1), new Point(2,1), new Point(1,2)},
			new Point[]{new Point(0,1), new Point(1,1), new Point(1,2), new Point(2,2)},
			new Point[]{new Point(1,0), new Point(0,1), new Point(1,1), new Point(0,2)}
		}),
		S(Color.GREEN, 3, new Point(3, 2), new Point[][]{
			new Point[]{new Point(1,0), new Point(2,0),new Point(0,1),new Point(1,1)},
			new Point[]{new Point(1,0), new Point(1,1),new Point(2,1),new Point(2,2)},
			new Point[]{new Point(1,1), new Point(2,1),new Point(0,2),new Point(1,2)},
			new Point[]{new Point(0,0), new Point(0,1),new Point(1,1),new Point(1,2)},
		});
		
		public Color color;
		public int width;
		public Point startPosition;
		public Point[][] position;

		private Tetrimino(Color color, int width, Point startPosition, Point[][] position) {
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
		GAME_OVER;
	}
	
	
	
	public AsciiTetris() {
		asciiTerminal = new AsciiTerminal("AsciiTetris", new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT), TILESET, CHARACTER_WIDTH, CHARACTER_HEIGHT, SCALE, CUSTOM_WINDOW);
		asciiPanel = asciiTerminal.getAsciiPanel();
		
		asciiTerminal.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(!continueKeyEvents.get(e.getKeyCode())) {
					instantKeyEvents.set(e.getKeyCode());
				}
				continueKeyEvents.set(e.getKeyCode());
				event = e;
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				continueKeyEvents.clear(e.getKeyCode());
			}
		});
	}
	
	public void run() {
		long lastLoopTime = System.nanoTime();
		
		while(true) {
			long now = System.nanoTime();
			double updateLength = now - lastLoopTime;
			lastLoopTime = now;
			double delta = updateLength / OPTIMAL_TIME;
			
			/**
			 * DRAW BASE
			 */
			
			asciiPanel.clear();
			
			// PLAYFIELD BORDER
			Color color = Color.DARK_GRAY;
			asciiPanel.write(1, 1, (char)201, color);
			asciiPanel.write(12, 1, (char)187, color);
			asciiPanel.write(1, 22, (char)200, color);
			asciiPanel.write(12, 22, (char)188, color);
			for(int i = 0; i < 10; i++) {
				asciiPanel.write(2+i, 1, (char)205, color);
				asciiPanel.write(2+i, 22, (char)205, color);
			}
			for(int j = 0; j < 20; j++) {
				asciiPanel.write(1, 2+j, (char)186, color);
				asciiPanel.write(12, 2+j, (char)186, color);
			}
			
			// SCORE
			asciiPanel.writeString(14, 2, "SCORE", Color.BLUE);
			asciiPanel.writeString(14, 3, String.format("%06d", score), Color.CYAN);
			
			// LEVEL
			asciiPanel.writeString(14, 5, "LEVEL", Color.BLUE);
			asciiPanel.writeString(14, 6, String.format("%06d", level), Color.CYAN);
			
			// NEXT TETROMINO BORDER
			asciiPanel.writeString(15, 8, "NEXT", Color.BLUE);
			asciiPanel.write(14, 9, (char)218, color);
			asciiPanel.write(19, 9, (char)191, color);
			asciiPanel.write(14, 12, (char)192, color);
			asciiPanel.write(19, 12, (char)217, color);
			for(int i = 0; i < 4; i++) {
				asciiPanel.write(15+i, 9, (char)196, color);
				asciiPanel.write(15+i, 12, (char)196, color);
			}
			for(int j = 0; j < 2; j++) {
				asciiPanel.write(14, 10+j, (char)179, color);
				asciiPanel.write(19, 10+j, (char)179, color);
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
				playGame(delta);
			}
			else if(gameState == GameState.PAUSE) {
				pauseGame();
			}
			else if(gameState == GameState.GAME_OVER) {
				gameOverGame();
			}
			
			
			asciiTerminal.repaint();
			
			try {
				long value = (lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000;
				if(value > 0) {
					Thread.sleep(value);					
				}
				else {
					Thread.sleep(0, 1);
				}
			} catch (InterruptedException e) {
			}
		}
	}
	
	public void menuGame() {
		if(event != null) {
			if(event.getKeyCode() == KeyEvent.VK_ENTER) {
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
			
			else if(event.getKeyCode() == KeyEvent.VK_UP) {
				menuPosition--;
				if(menuPosition < 0) {
					menuPosition = 2;
				}
			}
			else if(event.getKeyCode() == KeyEvent.VK_DOWN) {
				menuPosition++;
			}
			
			event = null;
		}
		
		menuPosition %= 3;
		
		asciiPanel.writeString(5, 7, "MENU", Color.WHITE);
		
		asciiPanel.writeString(5, 9, "START", Color.GRAY);
		asciiPanel.writeString(1, 10, "LEADERBOARDS", Color.GRAY);
		asciiPanel.writeString(5, 11, "EXIT", Color.GRAY);
		switch (menuPosition) {
			case 0:
				asciiPanel.writeString(5, 9, "START", Color.WHITE);
				break;
				
			case 1:
				asciiPanel.writeString(1, 10, "LEADERBOARDS", Color.WHITE);
				break;
				
			case 2:
				asciiPanel.writeString(5, 11, "EXIT", Color.WHITE);
				break;
	
			default:
				break;
		}
	}
	
	public void leaderboardsGame() {
		
	}
	
	public void startGame() {
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
	
	public void playGame(double delta) {
		/**
		 * UPDATE
		 */
		boolean softDrop = false;
		
		// KEY EVENT
		if(event != null) {
			if(event.getKeyCode() == KeyEvent.VK_UP) {
				boolean turn = turnTetrimino(currentPosition);
				if(!turn) {
					turn = turnTetrimino(new Point(currentPosition.x - 1, currentPosition.y));
					if(!turn) {
						turnTetrimino(new Point(currentPosition.x + 1, currentPosition.y));
					}
				}
			}
			else if(event.getKeyCode() == KeyEvent.VK_ESCAPE) {
				gameState = GameState.PAUSE;
				return;
			}
			
			event = null;
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
			
			if(instantKeyEvents.get(KeyEvent.VK_LEFT) || instantKeyEvents.get(KeyEvent.VK_RIGHT)) {
				waitEvent = REPEAT_KEY_EVENT * 2;
			}
		
			if(continueKeyEvents.get(KeyEvent.VK_LEFT) || instantKeyEvents.get(KeyEvent.VK_LEFT)) {
				instantKeyEvents.clear(KeyEvent.VK_LEFT);
				
				boolean goLeft = true;
				for(Point p : currentTetrimino.position[currentDirection]) {
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
			if(continueKeyEvents.get(KeyEvent.VK_RIGHT) || instantKeyEvents.get(KeyEvent.VK_RIGHT)) {
				instantKeyEvents.clear(KeyEvent.VK_RIGHT);
				
				boolean goRight = true;
				for(Point p : currentTetrimino.position[currentDirection]) {
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
			if(continueKeyEvents.get(KeyEvent.VK_DOWN) || instantKeyEvents.get(KeyEvent.VK_DOWN)) {
				instantKeyEvents.clear(KeyEvent.VK_DOWN);
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
				score += SOFT_DROP_BONUS_POINT;
			}
			
			boolean goDown = true;
			for(Point p : currentTetrimino.position[currentDirection]) {
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
				for(Point p : currentTetrimino.position[currentDirection]) {
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
				
				newTetrimino();
			}
		}
		
		
		
		
		
		
		
		
		
		/**
		 * DRAW
		 */
		
		// PLAYFIELD
		int xOffset = 2;
		int yOffset = 2;
		for(int i = 0; i < PLAYFIELD_WIDTH; i++) {
			for(int j = PLAYFIELD_HEIGHT - DISPLAY_PLAYFIELD_HEIGHT; j < PLAYFIELD_HEIGHT; j++) {
				if(cells[i][j] != null) {
					asciiPanel.write(i + xOffset, j + yOffset - (PLAYFIELD_HEIGHT - DISPLAY_PLAYFIELD_HEIGHT), ' ', Color.WHITE, cells[i][j]);
				}
			}
		}
		
		for(Point p : currentTetrimino.position[currentDirection]) {
			if(p.y + currentPosition.y + yOffset - 2 > 1) {
				asciiPanel.write(p.x+currentPosition.x+xOffset, p.y+currentPosition.y+yOffset - 2, ' ', Color.WHITE, currentTetrimino.color);
			}
		}
		
		
		// NEXT TETROMINO
		for(Point p : nextTetrimino.position[0]) {
			asciiPanel.write(15+p.x, 10+p.y, ' ', Color.WHITE, nextTetrimino.color);
		}
	}
	
	public void pauseGame() {
		if(event != null) {
			if(event.getKeyCode() == KeyEvent.VK_ENTER) {
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
			
			else if(event.getKeyCode() == KeyEvent.VK_UP) {
				menuPosition--;
				if(menuPosition < 0) {
					menuPosition = 2;
				}
			}
			else if(event.getKeyCode() == KeyEvent.VK_DOWN) {
				menuPosition++;
			}
			
			event = null;
		}
		
		menuPosition %= 3;
		
		asciiPanel.writeString(4, 7, "PAUSE", Color.WHITE);
		
		asciiPanel.writeString(3, 9, "CONTINUE", Color.GRAY);
		asciiPanel.writeString(5, 10, "MENU", Color.GRAY);
		asciiPanel.writeString(5, 11, "EXIT", Color.GRAY);
		switch (menuPosition) {
			case 0:
				asciiPanel.writeString(3, 9, "CONTINUE", Color.WHITE);
				break;
				
			case 1:
				asciiPanel.writeString(5, 10, "MENU", Color.WHITE);
				break;
				
			case 2:
				asciiPanel.writeString(5, 11, "EXIT", Color.WHITE);
				break;
	
			default:
				break;
		}
	}
	
	public void gameOverGame() {
		
	}
	
	
	public void newTetrimino() {
		Tetrimino[] tetriminos = Tetrimino.values();
		boolean change = false;
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
		currentPosition = (Point)currentTetrimino.startPosition.clone();
	}
	
	private boolean turnTetrimino(Point position) {
		boolean turn = true;
		int nextDirection = (currentDirection + 1) % currentTetrimino.position.length;
		for(Point p : currentTetrimino.position[nextDirection]) {
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
		
		if(turn) {
			currentDirection = nextDirection;
			currentPosition.x = position.x;
		}
		
		return turn;
	}
	
	private double tickDuration() {
		return Math.pow((0.8-((level-1)*0.007)),(level-1));
	}
	
	private void scoring(int lines) {
		System.out.println("Level: "+level+" - scoreLevel: "+scoreLevel+" - score: "+score);
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
		System.out.println("Level: "+level+" - scoreLevel: "+scoreLevel+" - score: "+score);
		
		level = suiteScoreLevel(0, scoreLevel);
	}
	
	public int suiteScoreLevel(int level, int scoreLevel) {
		if(scoreLevel >= (level + 1) * 5) {
			return suiteScoreLevel(level+1, scoreLevel - ((level + 1) * 5));
		}
		else {
			return level;
		}
	}
	
	public static void main(String[] args) {
		AsciiTetris asciiTetris = new AsciiTetris();
		asciiTetris.run();
	}
}
