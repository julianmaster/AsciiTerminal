package ui;

import java.awt.Color;
import java.awt.image.RGBImageFilter;

/**
 * Change the background color of all character of the tileset to black.
 * 
 * @author Julien MAITRE
 * 
 */
public class AsciiBackgroundFilter extends RGBImageFilter {
    private Color m_BackgroundColor;

    public AsciiBackgroundFilter(Color m_BackgroundColor) {
    	// The transformation of colors don't depend of the location of points in the image.
        canFilterIndexColorModel = true;
        this.m_BackgroundColor = m_BackgroundColor;
    }

    @Override
    public int filterRGB(int x, int y, int rgb) {
        int red = rgb & 0x00FF0000;
        int green = rgb & 0x0000FF00;
        int blue = rgb & 0x000000FF;
        int alpha = rgb & 0xFF000000;

        if(     (red >> 4*4) == m_BackgroundColor.getRed() &&
                (green >> 2*4) == m_BackgroundColor.getGreen()&&
                (blue >> 0*4) == m_BackgroundColor.getBlue()){
            return alpha << 6*4;
        }
        else{
            return rgb;
        }
    }
}
