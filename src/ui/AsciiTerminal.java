package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 *	Create a JFrame with an AsciiPanel.
 *
 * @author Julien MAITRE
 * 
 */
public class AsciiTerminal extends JFrame {
	private AsciiPanel asciiPanel;
	private Point initialClick;

//	public AsciiTerminal(String title, Dimension dimension, String tilesetFile, int characterWidth, int characterHeight) {
//		this(title, dimension, tilesetFile, characterWidth, characterHeight, 1, false);
//	}
//
//    public AsciiTerminal(String title, Dimension dimension, String tilesetFile, int characterWidth, int characterHeight, int scale) {
//       this(title, dimension, tilesetFile, characterWidth, characterHeight, scale, false);
//    }
//
//    public AsciiTerminal(String title, Dimension dimension, String tilesetFile, int characterWidth, int characterHeight, int scale, boolean customWindow) {
//    	if(customWindow) {
//    		this.setUndecorated(true);
//    		this.setLayout(new BorderLayout());
//
//    		// Window panel
//    		AsciiPanel asciiTitleBarPanel = new AsciiPanel(new Dimension(dimension.width, 1), tilesetFile, characterWidth, characterHeight, scale);
//    		int countSpace = dimension.width-2 - title.length();
//    		String titleBar = title;
//    		for(int i = 0; i < countSpace; i++)
//            {
//    			titleBar+=" ";
//            }
//
//    		// Title bar button
//    		AsciiTerminalButton titleBarButton = new AsciiTerminalButton(asciiTitleBarPanel, titleBar, 0, 0, Color.WHITE, Color.WHITE, Color.BLUE);
//    		titleBarButton.addMouseListener(new MouseAdapter() {
//    			@Override
//    			public void mousePressed(MouseEvent e) {
//    				initialClick = e.getPoint();
//    	            getComponentAt(initialClick);
//    			}
//			});
//    		titleBarButton.addMouseMotionListener(new MouseMotionAdapter() {
//    			@Override
//    	        public void mouseDragged(MouseEvent e) {
//    	            // get location of Window
//    	            int thisX = getLocation().x;
//    	            int thisY = getLocation().y;
//
//    	            // Determine how much the mouse moved since the initial click
//    	            int xMoved = (thisX + e.getX()) - (thisX + initialClick.x);
//    	            int yMoved = (thisY + e.getY()) - (thisY + initialClick.y);
//
//    	            // Move window to this position
//    	            int X = thisX + xMoved;
//    	            int Y = thisY + yMoved;
//    	            setLocation(X, Y);
//    	        }
//			});
//    		asciiTitleBarPanel.add(titleBarButton);
//
//    		JFrame frame = this;
//
//    		// Reduce window button
//    		AsciiTerminalButton reduceWindowButton = new AsciiTerminalButton(asciiTitleBarPanel, "-", dimension.width-2, 0, Color.WHITE, Color.WHITE, Color.BLUE);
//    		reduceWindowButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
//    		reduceWindowButton.addMouseListener(new MouseAdapter() {
//    			@Override
//    			public void mouseClicked(MouseEvent e) {
//    				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_ICONIFIED));
//    				setState(JFrame.ICONIFIED);
//    			}
//    		});
//    		asciiTitleBarPanel.add(reduceWindowButton);
//
//    		// Close window button
//    		AsciiTerminalButton closeButton = new AsciiTerminalButton(asciiTitleBarPanel, "X", dimension.width-1, 0, Color.RED, Color.RED, Color.BLUE);
//    		closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
//    		closeButton.addMouseListener(new MouseAdapter() {
//    			@Override
//    			public void mouseClicked(MouseEvent e) {
//    				dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
//    			}
//    		});
//    		asciiTitleBarPanel.add(closeButton);
//
//    		// Adding the title bar panel at top of the screen
//    		this.add(asciiTitleBarPanel, BorderLayout.NORTH);
//
//    		// Panel
//    		asciiPanel = new AsciiPanel(dimension, tilesetFile, characterWidth, characterHeight, scale);
//    		this.setTitle(title);
//            this.setResizable(false);
//            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            this.add(asciiPanel, BorderLayout.CENTER);
//            this.pack();
//            this.setLocationRelativeTo(null);
//            this.setVisible(true);
//    	}
//    	else {
//    		asciiPanel = new AsciiPanel(dimension, tilesetFile, characterWidth, characterHeight, scale);
//    		this.setTitle(title);
//            this.setResizable(false);
//            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            this.getContentPane().add(asciiPanel);
//            this.pack();
//            this.setLocationRelativeTo(null);
//            this.setVisible(true);
//    	}
//    }
	
	public AsciiPanel getAsciiPanel() {
		return asciiPanel;
	}
}
