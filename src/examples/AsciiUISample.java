package examples;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ui.AsciiSelectableTerminalButton;
import ui.AsciiTerminal;
import ui.AsciiTerminalButton;

import javax.swing.*;
import java.util.Random;

public class AsciiUISample extends Game {
	
	public static final String TITLE = "ASCII Demo";
	public static final int WINDOW_WIDTH = 16;
	public static final int WINDOW_HEIGHT = 16;

    private Random rand = new Random();
	private AsciiTerminal asciiTerminal;

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
    }

    public void init(String tilesetFile, int characterWidth, int characterHeight, int scale) {
        asciiTerminal = new AsciiTerminal(TITLE, WINDOW_WIDTH, WINDOW_HEIGHT, tilesetFile, characterWidth, characterHeight, scale);

        for(int i = 0; i < 16; i++) {
            for(int j = 0; j < 10; j++) {
                asciiTerminal.write(i, j, (char)rand.nextInt(256), new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 1f));
            }
        }

        AsciiTerminalButton button1 = new AsciiTerminalButton(asciiTerminal, "Click on me !", 0, 12, Color.FOREST, Color.ORANGE, Color.GREEN, Color.BLACK);
        button1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                JOptionPane.showMessageDialog(null, "Thank !");
            }
        });
        asciiTerminal.addActor(button1);

        AsciiSelectableTerminalButton button2 = new AsciiSelectableTerminalButton(asciiTerminal, "Select me !", 0, 14, Color.FOREST, Color.ORANGE, Color.GREEN, Color.MAGENTA, Color.BLACK);
        asciiTerminal.addActor(button2);

        super.setScreen(asciiTerminal);
    }

    @Override
    public void render() {
        super.render();

        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            asciiTerminal.changeSettings("AsciiTetris", WINDOW_WIDTH, WINDOW_HEIGHT, "Curses_square_24x24.png", 24, 24, 1);

            for(int i = 0; i < 16; i++) {
                for(int j = 0; j < 10; j++) {
                    asciiTerminal.write(i, j, (char)rand.nextInt(256), new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 1f));
                }
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        asciiTerminal.dispose();
    }
}
