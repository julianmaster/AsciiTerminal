package com.asciiterminal.ui;

import com.badlogic.gdx.graphics.Color;

/**
 * Representation of data of one character in the AsciiPanel
 * 
 * @author Julien MAITRE
 * 
 */
public class AsciiTerminalDataCell {
    public char data;
    public Color dataColor;
    public Color backgroundColor;

    public AsciiTerminalDataCell() {
        this.data = 0;
        this.dataColor = Color.WHITE;
        this.backgroundColor = Color.BLACK;
    }

    public AsciiTerminalDataCell(char data, Color dataColor, Color backgroundColor) {
        this.data = data;
        this.dataColor = dataColor;
        this.backgroundColor = backgroundColor;
    }
}
