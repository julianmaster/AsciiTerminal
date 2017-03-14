package examples;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ui.AsciiPanel;
import ui.AsciiTerminalButton;

import javax.swing.*;
import java.util.Random;

public class AsciiUISample extends Game {
	
	public static final String TITLE = "ASCII Demo";
	public static final int WINDOW_WIDTH = 16;
	public static final int WINDOW_HEIGHT = 16;
    public static final int CHARACTER_WIDTH = 9;
    public static final int CHARACTER_HEIGHT = 12;
    public static final int SCALE = 4;

	private AsciiPanel asciiPanel;

    @Override
    public void create() {
//        asciiPanel = new AsciiTerminal(TITLE, new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT), tilesetFile, characterWidth, characterHeight, scale, customWindow);
        asciiPanel = new AsciiPanel(WINDOW_WIDTH, WINDOW_HEIGHT, "Yoshis_island_9x12.png", CHARACTER_WIDTH, CHARACTER_HEIGHT, SCALE);

        Random rand = new Random();

        for(int i = 0; i < 16; i++) {
            for(int j = 0; j < 10; j++) {
                asciiPanel.write(i, j, (char)rand.nextInt(256), new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 1f));
            }
        }

//        asciiPanel.writeString(0, 12, "Click on me !", Color.GREEN);

        AsciiTerminalButton button1 = new AsciiTerminalButton(asciiPanel, "Click on me !", 0, 12, Color.FOREST, Color.ORANGE, Color.GREEN, Color.BLACK);
        button1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                JOptionPane.showMessageDialog(null, "Thank !");
            }
        });
        asciiPanel.addActor(button1);

//		AsciiSelectableTerminalButton button2 = new AsciiSelectableTerminalButton(asciiPanel, "Select me !", 0, 14, Color.GREEN, Color.ORANGE, Color.MAGENTA);
//		button2.setCursor(new Cursor(Cursor.HAND_CURSOR));
//		button2.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				AsciiSelectableTerminalButton astb = (AsciiSelectableTerminalButton)e.getComponent();
//				astb.setSelect(!astb.isSelect());
//			}
//		});
//		asciiPanel.add(button2);
//
//		terminal.repaint();
        super.setScreen(asciiPanel);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    //
//	public static void main(String[] args) {
//		String[] choiceTileset = { "Anikki [8x8]", "Yoshis island [9x12]", "Vidumec [15x15]", "Wanderlust [16x16]", "Curses square [24x24]" };
//		JComboBox<String> comboChoiceTileset = new JComboBox<>(choiceTileset);
//
//		String[] choiceScale = { "Small", "Medium", "Large"};
//		JComboBox<String> comboChoiceScale = new JComboBox<>(choiceScale);
//		comboChoiceScale.setSelectedItem("Medium");
//		Object[] choices = {
//				"Tileset:", comboChoiceTileset,
//				"Scale:", comboChoiceScale
//		};
//
//		int option = JOptionPane.showConfirmDialog(null, choices, "Configurations", JOptionPane.OK_CANCEL_OPTION);
//	    if(option == JOptionPane.OK_OPTION) {
//	    	int scale = comboChoiceScale.getSelectedIndex()+1;
//	    	if(comboChoiceTileset.getSelectedItem().equals("Anikki [8x8]")) {
//				new AsciiUISample("/assets/Anikki_square_8x8.png", 8, 8, scale, true);
//			}
//			else if(comboChoiceTileset.getSelectedItem().equals("Yoshis island [9x12]")) {
//				new AsciiUISample("/assets/Yoshis_island_9x12.png", 9, 12, scale, true);
//			}
//			else if(comboChoiceTileset.getSelectedItem().equals("Vidumec [15x15]")) {
//				new AsciiUISample("/assets/Vidumec_15x15.png", 15, 15, scale, true);
//			}
//			else if(comboChoiceTileset.getSelectedItem().equals("Wanderlust [16x16]")) {
//				new AsciiUISample("/assets/wanderlust_16x16.png", 16, 16, scale, true);
//			}
//			else {
//				new AsciiUISample("/assets/Curses_square_24x24.png", 24, 24, scale, true);
//			}
//	    }
//	}
}
