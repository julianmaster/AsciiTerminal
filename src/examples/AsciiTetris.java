package examples;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import ui.AsciiPanel;
import ui.AsciiTerminal;

public class AsciiTetris {
	public static final int WINDOW_WIDTH = 22;
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
	
	private AsciiTerminal asciiTerminal;
	private AsciiPanel asciiPanel;
	private KeyEvent event;
	
	private Random rand = new Random();
	private int score = 0;
	
	private Color[][] cells = new Color[PLAYFIELD_WIDTH][PLAYFIELD_HEIGHT];
	
	private double FPS = 1f;
	private double SOFT_DROP_FPS = 30f;
	
	private double timer = 0d;
	private Point currentPosition = null;
	private int currentDirection = 0;
	private Tetrimino currentTetrimino = null;
	
	enum Tetrimino {
		I(Color.CYAN, 4, new Point[][]{
			new Point[]{new Point(0,1), new Point(1,1), new Point(2,1), new Point(3,1)},
			new Point[]{new Point(2,0), new Point(2,1), new Point(2,2), new Point(2,3)},
			new Point[]{new Point(0,2), new Point(1,2), new Point(2,2), new Point(3,2)},
			new Point[]{new Point(1,0), new Point(1,1), new Point(1,2), new Point(1,3)}
		}),
		O(Color.YELLOW, 4, new Point[][]{
			new Point[]{new Point(1,0), new Point(2,0), new Point(1,1), new Point(2,1)}
		}),
		T(Color.MAGENTA, 3, new Point[][]{
			new Point[]{new Point(1,0), new Point(0,1), new Point(1,1), new Point(2,1)},
			new Point[]{new Point(1,0), new Point(1,1), new Point(2,1), new Point(1,2)},
			new Point[]{new Point(0,1), new Point(1,1), new Point(2,1), new Point(1,2)},
			new Point[]{new Point(1,0), new Point(0,1), new Point(1,1), new Point(1,2)},
		}),
		L(Color.ORANGE, 3, new Point[][]{
			new Point[]{new Point(2,0), new Point(0,1), new Point(1,1), new Point(2,1)},
			new Point[]{new Point(1,0), new Point(1,1), new Point(1,2), new Point(2,2)},
			new Point[]{new Point(0,1), new Point(1,1), new Point(2,1), new Point(0,2)},
			new Point[]{new Point(0,0), new Point(1,0), new Point(1,1), new Point(1,2)}
		}),
		J(Color.BLUE, 3, new Point[][]{
			new Point[]{new Point(0,0), new Point(0,1), new Point(1,1), new Point(2,1)},
			new Point[]{new Point(1,0), new Point(2,0), new Point(1,1), new Point(1,2)},
			new Point[]{new Point(0,1), new Point(1,1), new Point(2,1), new Point(2,2)},
			new Point[]{new Point(1,0), new Point(1,1), new Point(0,2), new Point(1,2)}
		}),
		Z(Color.RED, 3, new Point[][]{
			new Point[]{new Point(0,0), new Point(1,0), new Point(1,1), new Point(2,1)},
			new Point[]{new Point(2,0), new Point(1,1), new Point(2,1), new Point(1,2)},
			new Point[]{new Point(0,1), new Point(1,1), new Point(1,2), new Point(2,2)},
			new Point[]{new Point(1,0), new Point(0,1), new Point(1,1), new Point(0,2)}
		}),
		S(Color.GREEN, 3, new Point[][]{
			new Point[]{new Point(1,0), new Point(2,0),new Point(0,1),new Point(1,1)},
			new Point[]{new Point(1,0), new Point(1,1),new Point(2,1),new Point(2,2)},
			new Point[]{new Point(1,1), new Point(2,1),new Point(0,2),new Point(1,2)},
			new Point[]{new Point(0,0), new Point(0,1),new Point(1,1),new Point(1,2)},
		});
		
		public Color color;
		public int width;
		public Point[][] position;

		private Tetrimino(Color color, int width, Point[][] position) {
			this.color = color;
			this.width = width;
			this.position = position;
		}
	}
	
	
	
	
	public AsciiTetris() {
		asciiTerminal = new AsciiTerminal("AsciiTetris", new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT), TILESET, CHARACTER_WIDTH, CHARACTER_HEIGHT, SCALE, CUSTOM_WINDOW);
		asciiPanel = asciiTerminal.getAsciiPanel();
		
		asciiTerminal.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				event = e;
			}
		});
		
		newTetrimino();
	}
	
	public void run() {
		long lastLoopTime = System.nanoTime();
		
		while(true) {
			long now = System.nanoTime();
			double updateLength = now - lastLoopTime;
			lastLoopTime = now;
			double delta = updateLength / OPTIMAL_TIME;
			
			/**
			 * UPDATE
			 */
			
			timer += delta;
			if(timer >= TARGET_FPS / FPS) {
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
					timer = 0;
					currentPosition.y += 1;
				}
				else {
					for(Point p : currentTetrimino.position[currentDirection]) {
						cells[p.x+currentPosition.x][p.y+currentPosition.y] = currentTetrimino.color;
					}
					newTetrimino();
				}
			}
			
			// KEY EVENT
			if(event != null) {
				if(event.getKeyCode() == KeyEvent.VK_LEFT) {
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
				if(event.getKeyCode() == KeyEvent.VK_RIGHT) {
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
				if(event.getKeyCode() == KeyEvent.VK_UP) {
					boolean turn = true;
					int nextDirection = (currentDirection + 1) % currentTetrimino.position.length;
					for(Point p : currentTetrimino.position[nextDirection]) {
						Color color = cells[p.x + currentPosition.x][p.y + currentPosition.y];
						if(color != null) {
							turn = false;
							break;
						}
					}
					
					if(turn) {
						currentDirection = nextDirection;
					}
				}
				
				event = null;
			}

			
			
			
			
			
			/**
			 * DRAW
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
			
			// SCORE
			asciiPanel.writeString(15, 2, "SCORE", Color.BLUE);
			asciiPanel.writeString(15, 3, String.format("%05d",score), Color.CYAN);
			
			// NEXT TETROMINOS
			asciiPanel.writeString(15, 5, "NEXT", Color.BLUE);
			asciiPanel.write(14, 6, (char)218, color);
			asciiPanel.write(20, 6, (char)191, color);
			asciiPanel.write(14, 12, (char)192, color);
			asciiPanel.write(20, 12, (char)217, color);
			for(int i = 0; i < 5; i++) {
				asciiPanel.write(15+i, 6, (char)196, color);
				asciiPanel.write(15+i, 12, (char)196, color);
			}
			for(int j = 0; j < 5; j++) {
				asciiPanel.write(14,7+j, (char)179, color);
				asciiPanel.write(20, 7+j, (char)179, color);
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
	
	public void newTetrimino() {
		Tetrimino[] tetriminos = Tetrimino.values();
		currentTetrimino = tetriminos[rand.nextInt(tetriminos.length)];
		currentDirection = 0;
		currentPosition = new Point(PLAYFIELD_WIDTH/2 - currentTetrimino.width/2, 0);
	}
	
	public static void main(String[] args) {
		AsciiTetris asciiTetris = new AsciiTetris();
		asciiTetris.run();
	}
}
