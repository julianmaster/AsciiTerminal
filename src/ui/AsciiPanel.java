package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.ShortLookupTable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * JPanel with a ASCII render system
 * 
 * @author julien MAITRE
 *
 */
public class AsciiPanel extends JPanel {
    private Dimension size;
    private BufferedImage[] character;
    private Color defaultCharacterColor;
    private Color defaultCharacterBackgroundColor;
    private Dimension characterSize;
    private AsciiTerminalDataCell[][] terminal;
    private AsciiTerminalDataCell[][] oldTerminal;
    private Image image;
    private Graphics2D graphics;
    private int scale;

    public AsciiPanel(Dimension dimension, String tilesetFile, int characterWidth, int characterHeight) {
    	this(dimension, tilesetFile, characterWidth, characterHeight, 1);
    }
    
    public AsciiPanel(Dimension dimension, String tilesetFile, int characterWidth, int characterHeight, int scale) {
        this.size = dimension;
        this.characterSize = new Dimension(characterWidth, characterHeight);
        this.scale = scale;
        this.defaultCharacterColor = Color.WHITE;
        this.defaultCharacterBackgroundColor = Color.BLACK;

        terminal = new AsciiTerminalDataCell[size.height][size.width];
        oldTerminal = new AsciiTerminalDataCell[size.height][size.width];
        for(int i = 0; i < size.height; i++){
            for(int j = 0; j < size.width; j++){
                AsciiTerminalDataCell tdc = new AsciiTerminalDataCell();
                terminal[i][j] = tdc;
                oldTerminal[i][j] = tdc;
            }
        }

        this.setPreferredSize(new Dimension(size.width*characterSize.width*scale, size.height*characterSize.height*scale));

        try {
            character = new  BufferedImage[256];
            BufferedImage tilesets = ImageIO.read(getClass().getResource(tilesetFile));

            // Recuperation of the background color
            BufferedImage imageBackgroundColor = tilesets.getSubimage(0, 0, 1, 1);
            int color = imageBackgroundColor.getRGB(0, 0);
            Color m_characterBackgroundColor = Color.getColor(null, color);

            // Modification of characters background
            Image characterBackgroundColorModified = createImage(new FilteredImageSource(tilesets.getSource(), new AsciiBackgroundFilter(m_characterBackgroundColor)));

            // Creation of tileset with a modification of the background color
            BufferedImage tilesetsModified = new BufferedImage(tilesets.getWidth(), tilesets.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics graphicsTilesetsModified = tilesetsModified.getGraphics();
            graphicsTilesetsModified.setColor(Color.BLACK);
            graphicsTilesetsModified.fillRect(0, 0, tilesetsModified.getWidth(), tilesetsModified.getHeight());
            // Draw in a BufferedImage for characters recuperation
            graphicsTilesetsModified.drawImage(characterBackgroundColorModified, 0, 0, this);

            for(int i = 0; i < 256; i++){
                int x = (i%16)*characterSize.width;
                int y = (i/16)*characterSize.height;
                character[i] = new BufferedImage(characterSize.width, characterSize.height, BufferedImage.TYPE_INT_ARGB);
                character[i].getGraphics().drawImage(tilesetsModified, 0, 0, characterSize.width, characterSize.height, x, y, x+characterSize.width, y+characterSize.height, this);
            }
        }
        catch (IOException ex) {
        	Logger.getLogger(AsciiTerminal.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.setLayout(null);
    }

    public void write(int positionX, int positionY, char character, Color characterColor){
        AsciiTerminalDataCell tdc = new AsciiTerminalDataCell();
        tdc.data = character;
        tdc.dataColor = characterColor;
        tdc.backgroundColor = defaultCharacterBackgroundColor;
        this.write(positionX, positionY, tdc);
    }

    public void write(int positionX, int positionY, char character, Color characterColor, Color characterBackgroundColor){
        AsciiTerminalDataCell tdc = new AsciiTerminalDataCell();
        tdc.data = character;
        tdc.dataColor =characterColor;
        tdc.backgroundColor = characterBackgroundColor;
        this.write(positionX, positionY, tdc);
    }

    public void write(int positionX, int positionY, AsciiTerminalDataCell character){
        if(positionX < 0 || positionX > size.width - 1){
            throw new IllegalArgumentException("X position between [0 and "+size.width+"]");
        }
        if(positionY < 0 || positionY > size.height - 1){
            throw new IllegalArgumentException("Y position between [0 and "+size.height+"]");
        }

        terminal[positionY][positionX] = character;
    }

    public void writeString(int positionX, int positionY, String string, Color characterColor){
        writeString(positionX, positionY, string, characterColor, defaultCharacterBackgroundColor);
    }

    public void writeString(int positionX, int positionY, String string, Color characterColor, Color characterBackgroundColor){
        for(char c : string.toCharArray()){
            if(positionX < 0 || positionX > size.width - 1){
                throw new IllegalArgumentException("X position between [0 and "+size.width+"]");
            }
            if(positionY < 0 || positionY > size.height - 1){
                throw new IllegalArgumentException("Y position between [0 and "+size.height+"]");
            }

            AsciiTerminalDataCell tdc = new AsciiTerminalDataCell();
            tdc.data = c;
            tdc.dataColor = characterColor;
            tdc.backgroundColor = characterBackgroundColor;
            write(positionX, positionY, tdc);
            positionX++;
        }
    }

    public AsciiTerminalDataCell readCurrent(int x, int y){
        return this.oldTerminal[y][x];
    }
    
    public AsciiTerminalDataCell readNext(int x, int y){
        return this.terminal[y][x];
    }

    public void clear(){
        clear(0, 0, size.width, size.height);
    }

    public void clear(int x, int y, int width, int height){
        if(x < 0 || x > size.width - 1){
            throw new IllegalArgumentException("X position between [0 and "+(size.width-1)+"]");
        }
        if(y < 0 || y > size.height - 1){
            throw new IllegalArgumentException("Y position between [0 and "+(size.height-1)+"]");
        }
        if(width < 1){
            throw new IllegalArgumentException("Width under 1");
        }
        if(height < 1){
            throw new IllegalArgumentException("Height under 1");
        }
        if(width+x > size.width || height+y > size.height){
            throw new IllegalArgumentException("Clear over the terminal");
        }
        for(int i = y; i < y + height; i++){
            for(int j = x; j < x + width; j++){
                AsciiTerminalDataCell tdc = new AsciiTerminalDataCell();
                terminal[i][j] = tdc;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	Graphics2D g2d = (Graphics2D)g.create();
    	
		if(image == null) {
			image = this.createImage(this.getPreferredSize().width, this.getPreferredSize().height);
            graphics = (Graphics2D)image.getGraphics();
            graphics.setColor(defaultCharacterBackgroundColor);
            graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
    	}
    	
        for(Component component : getComponents()) {
        	component.paint(graphics);
        }

        for(int i = 0; i < size.height; i++){
            for(int j = 0; j < size.width; j++){
                if(     terminal[i][j].data == oldTerminal[i][j].data &&
                        terminal[i][j].dataColor.equals(oldTerminal[i][j].dataColor) &&
                        terminal[i][j].backgroundColor.equals(oldTerminal[i][j].backgroundColor)){
                    continue;
                }

                LookupOp lookupOp = setColorCharacter(terminal[i][j].backgroundColor, terminal[i][j].dataColor);
                graphics.drawImage(lookupOp.filter(character[terminal[i][j].data], null), j*characterSize.width*scale, i*characterSize.height*scale, characterSize.width*scale, characterSize.height*scale, this);

                oldTerminal[i][j].data = terminal[i][j].data;
                oldTerminal[i][j].dataColor = terminal[i][j].dataColor;
                oldTerminal[i][j].backgroundColor = terminal[i][j].backgroundColor;
            }
        }

        g2d.drawImage(image, 0, 0, this);
        g2d.dispose();
    }

    private LookupOp setColorCharacter(Color bgColor, Color fgColor){
        short[] red = new short[256];
        short[] green = new short[256];
        short[] blue = new short[256];
        short[] alpha = new short[256];

        // Recuperation of compound colors of foreground character color
        short dcr = (short) fgColor.getRed();
        short dcg = (short) fgColor.getGreen();
        short dcb = (short) fgColor.getBlue();

        // Recuperation of compound colors of background character color
        short bgr = (short) bgColor.getRed();
        short bgg = (short) bgColor.getGreen();
        short bgb = (short) bgColor.getBlue();

        for(short j = 0; j < 256; j++){
        	// if is foreground color
            if(j != 0){

                /**
                 * Calculation of j*255/dcr .
                 * Cross product
                 * dcr = 180     255
                 *   j =  ?       X
                 * Distribute the requested color [0 to 255] on the character color [0 to X]
                 */
                // Red
                if(dcr != 0){
                    red[j] = (short)(j*dcr/255);
                }
                else{
                    red[j] = 0;
                }

                // green
                if(dcg != 0){
                    green[j] = (short)(j*dcg/255);
                }
                else{
                    green[j] = 0;
                }

                // Blue
                if( dcb != 0){
                    blue[j] = (short)(j*dcb/255);
                }
                else{
                    blue[j] = 0;
                }

                // Alpha
                alpha[j] = 255;
            }
            // else is background color
            else {
                red[j] = bgr;
                green[j] = bgg;
                blue[j] = bgb;
                alpha[j] = 255;
            }
        }

        short[][] data = new short[][]{red, green, blue, alpha};
        LookupTable lookupTable = new ShortLookupTable(0, data);
        LookupOp lookupOp = new LookupOp(lookupTable, null);
        return lookupOp;
    }
    
    public Color getDefaultCharacterColor(){
        return this.defaultCharacterColor;
    }

    public void setDefaultCharacterColor(Color color){
        this.defaultCharacterColor = color;
    }

    public Color getDefaultCharacterBackgroundColor() {
        return defaultCharacterBackgroundColor;
    }

    public void setDefaultCharacterBackgroundColor(Color defaultCharacterBackgroundColor) {
        this.defaultCharacterBackgroundColor = defaultCharacterBackgroundColor;
    }
    
    public Dimension getCharacterSize() {
		return characterSize;
	}
    public int getScale() {
		return scale;
	}
}
