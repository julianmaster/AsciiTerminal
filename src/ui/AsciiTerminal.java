package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FloatFrameBuffer;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.nio.ByteBuffer;

/**
 * ScreenAdapter with a ASCII render system
 * 
 * @author Julien MAITRE
 *
 */
public class AsciiTerminal extends ScreenAdapter {
    private int width;
    private int height;
    private Texture texture;
    private TextureRegion[] characters;
    private Texture backgroundTexture;
    private Color defaultCharacterColor = Color.WHITE;
    private Color defaultCharacterBackgroundColor = Color.BLACK;
    private int characterWidth;
    private int characterHeight;
    private AsciiTerminalDataCell[][] terminal;
    private AsciiTerminalDataCell[][] oldTerminal;
    private int scale;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private FrameBuffer frameBuffer;
    private TextureRegion frameRegion;
    private Stage stage;

    public AsciiTerminal(String title, int width, int height, String tilesetFile, int characterWidth, int characterHeight) {
    	this(title, width, height, tilesetFile, characterWidth, characterHeight, 1);
    }
    
    public AsciiTerminal(String title, int width, int height, String tilesetFile, int characterWidth, int characterHeight, int scale) {
        this.width = width;
        this.height = height;
        this.characterWidth = characterWidth;
        this.characterHeight = characterHeight;
        this.scale = scale;

        Gdx.graphics.setTitle(title);
        Gdx.graphics.setWindowedMode(width * characterWidth * scale, height * characterHeight * scale);

        terminal = new AsciiTerminalDataCell[width][height];
        oldTerminal = new AsciiTerminalDataCell[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                terminal[i][j] = new AsciiTerminalDataCell();
                oldTerminal[i][j] = new AsciiTerminalDataCell();
            }
        }

        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(true);
        this.viewport = new FitViewport(width*characterWidth*scale, height*characterHeight*scale, camera);
        this.viewport.apply();
        this.batch = new SpriteBatch();

        this.frameBuffer = new FloatFrameBuffer(getFullWidth(), getFullHeight(), false);
        this.frameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        this.frameRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
        this.frameRegion.flip(false, true);

        camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);

        Pixmap whitePixmap = new Pixmap(characterWidth, characterHeight, Pixmap.Format.RGBA8888);
        whitePixmap.setColor(Color.WHITE);
        whitePixmap.fill();
        this.backgroundTexture = new Texture(whitePixmap);

        Pixmap pixmap = new Pixmap(Gdx.files.internal(tilesetFile));
        Pixmap resutPixmap = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Pixmap.Format.RGBA8888);

        ByteBuffer buffer = pixmap.getPixels();
        ByteBuffer resultBuffer = resutPixmap.getPixels();
        buffer.rewind();
        resultBuffer.rewind();

        boolean start = true;
        byte rBackground = 0;
        byte gBackground = 0;
        byte bBackground = 0;
        byte aBackground = -1;

        while(buffer.hasRemaining()) {
            byte r = buffer.get();
            byte g = buffer.get();
            byte b = buffer.get();
            byte a = pixmap.getFormat() == Pixmap.Format.RGBA8888 ? buffer.get() : -1;

            if(start) {
                start = false;
                rBackground = r;
                gBackground = g;
                bBackground = b;
                aBackground = a;
            }

            if(r == rBackground && g == gBackground && b == bBackground && a == aBackground) {
                resultBuffer.put((byte)0);
                resultBuffer.put((byte)0);
                resultBuffer.put((byte)0);
                resultBuffer.put((byte)0);
            }
            else {
                resultBuffer.put(r);
                resultBuffer.put(g);
                resultBuffer.put(b);
                resultBuffer.put(a);
            }
        }
        buffer.rewind();
        resultBuffer.rewind();

        pixmap.dispose();

        texture = new Texture(resutPixmap);

        characters = new TextureRegion[256];
        for(int i = 0; i < 256; i++) {
            int x = (i%16)*characterWidth;
            int y = (i/16)*characterHeight;

            characters[i] = new TextureRegion(texture, x, y, characterWidth, characterHeight);
        }

        this.stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);
    }

    public void changeSettings(String title, int width, int height, String tilesetFile, int characterWidth, int characterHeight, int scale) {
        this.width = width;
        this.height = height;
        this.characterWidth = characterWidth;
        this.characterHeight = characterHeight;
        this.scale = scale;

        Gdx.graphics.setTitle(title);
        Gdx.graphics.setWindowedMode(width * characterWidth * scale, height * characterHeight * scale);

        terminal = new AsciiTerminalDataCell[width][height];
        oldTerminal = new AsciiTerminalDataCell[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                terminal[i][j] = new AsciiTerminalDataCell();
                oldTerminal[i][j] = new AsciiTerminalDataCell();
            }
        }

        this.viewport.setWorldSize(width*characterWidth*scale, height*characterHeight*scale);
        this.viewport.apply();

        this.frameBuffer.dispose();
        this.frameBuffer = new FloatFrameBuffer(getFullWidth(), getFullHeight(), false);
        this.frameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        this.frameRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
        this.frameRegion.flip(false, true);

        camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);

        Pixmap whitePixmap = new Pixmap(characterWidth, characterHeight, Pixmap.Format.RGBA8888);
        whitePixmap.setColor(Color.WHITE);
        whitePixmap.fill();
        backgroundTexture.dispose();
        this.backgroundTexture = new Texture(whitePixmap);

        Pixmap pixmap = new Pixmap(Gdx.files.internal(tilesetFile));
        Pixmap resutPixmap = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Pixmap.Format.RGBA8888);

        ByteBuffer buffer = pixmap.getPixels();
        ByteBuffer resultBuffer = resutPixmap.getPixels();
        buffer.rewind();
        resultBuffer.rewind();

        boolean start = true;
        byte rBackground = 0;
        byte gBackground = 0;
        byte bBackground = 0;
        byte aBackground = -1;

        while(buffer.hasRemaining()) {
            byte r = buffer.get();
            byte g = buffer.get();
            byte b = buffer.get();
            byte a = pixmap.getFormat() == Pixmap.Format.RGBA8888 ? buffer.get() : -1;

            if(start) {
                start = false;
                rBackground = r;
                gBackground = g;
                bBackground = b;
                aBackground = a;
            }

            if(r == rBackground && g == gBackground && b == bBackground && a == aBackground) {
                resultBuffer.put((byte)0);
                resultBuffer.put((byte)0);
                resultBuffer.put((byte)0);
                resultBuffer.put((byte)0);
            }
            else {
                resultBuffer.put(r);
                resultBuffer.put(g);
                resultBuffer.put(b);
                resultBuffer.put(a);
            }
        }
        buffer.rewind();
        resultBuffer.rewind();

        pixmap.dispose();

        texture.dispose();
        texture = new Texture(resutPixmap);

        characters = new TextureRegion[256];
        for(int i = 0; i < 256; i++) {
            int x = (i%16)*characterWidth;
            int y = (i/16)*characterHeight;

            characters[i] = new TextureRegion(texture, x, y, characterWidth, characterHeight);
        }

        for(Actor actor  : stage.getActors()) {
            ((AsciiTerminalButton)actor).hasSizeChanged();
        }
    }

    public void write(int positionX, int positionY, char character, Color characterColor){
        this.write(positionX, positionY, character, characterColor, defaultCharacterBackgroundColor);
    }

    public void write(int positionX, int positionY, AsciiTerminalDataCell character){
        this.write(positionX, positionY, character.data, character.dataColor, character.backgroundColor);
    }

    public void write(int positionX, int positionY, char character, Color characterColor, Color characterBackgroundColor){
        if(positionX < 0 || positionX > width - 1){
            throw new IllegalArgumentException("X position between [0 and "+width+"]");
        }
        if(positionY < 0 || positionY > height - 1){
            throw new IllegalArgumentException("Y position between [0 and "+height+"]");
        }

        terminal[positionX][positionY].data = character;
        terminal[positionX][positionY].dataColor = characterColor;
        terminal[positionX][positionY].backgroundColor = characterBackgroundColor;
    }

    public void writeString(int positionX, int positionY, String string, Color characterColor){
        writeString(positionX, positionY, string, characterColor, defaultCharacterBackgroundColor);
    }

    public void writeString(int positionX, int positionY, String string, Color characterColor, Color characterBackgroundColor){
        for(char c : string.toCharArray()){
            this.write(positionX, positionY, c, characterColor, characterBackgroundColor);
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
        clear(0, 0, width, height);
    }

    public void clear(int x, int y, int width, int height){
        if(x < 0 || x > width - 1){
            throw new IllegalArgumentException("X position between [0 and "+(width-1)+"]");
        }
        if(y < 0 || y > height - 1){
            throw new IllegalArgumentException("Y position between [0 and "+(height-1)+"]");
        }
        if(width < 1){
            throw new IllegalArgumentException("Width under 1");
        }
        if(height < 1){
            throw new IllegalArgumentException("Height under 1");
        }
        if(width+x > width || height+y > height){
            throw new IllegalArgumentException("Clear over the terminal");
        }
        for(int i = y; i < y + height; i++){
            for(int j = x; j < x + width; j++) {
            	write(j, i, (char)0, defaultCharacterColor, defaultCharacterBackgroundColor);
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        stage.draw();

        frameBuffer.begin();
        batch.begin();
        for(int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if(     terminal[x][y].data == oldTerminal[x][y].data &&
                        terminal[x][y].dataColor.equals(oldTerminal[x][y].dataColor) &&
                        terminal[x][y].backgroundColor.equals(oldTerminal[x][y].backgroundColor)) {
                    continue;
                }

                batch.setColor(terminal[x][y].backgroundColor);
                batch.draw(backgroundTexture, x*characterWidth*scale, (height - y - 1)*characterHeight*scale, characterWidth*scale, characterHeight*scale);
                batch.setColor(terminal[x][y].dataColor);
                batch.draw(characters[terminal[x][y].data], x*characterWidth*scale, (height - y - 1)*characterHeight*scale, characterWidth*scale, characterHeight*scale);

                oldTerminal[x][y].data = terminal[x][y].data;
                oldTerminal[x][y].dataColor = terminal[x][y].dataColor;
                oldTerminal[x][y].backgroundColor = terminal[x][y].backgroundColor;
            }
        }
        batch.end();
        frameBuffer.end();


        batch.setColor(Color.WHITE);
        batch.begin();
        viewport.apply();
        batch.draw(frameRegion, 0, 0);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);
    }

    public void addActor(AsciiTerminalButton actor) {
        stage.addActor(actor);
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        frameBuffer.dispose();
        texture.dispose();
        backgroundTexture.dispose();
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

    public int getFullWidth() {
        return width * characterWidth * scale;
    }

    public int getFullHeight() {
        return height * characterHeight * scale;
    }

    public int getCharacterWidth() {
        return characterWidth;
    }

    public int getCharacterHeight() {
        return characterHeight;
    }

    public int getScale() {
		return scale;
	}

    public Stage getStage() {
        return stage;
    }

    public AsciiTerminalDataCell getCell(int x, int y) {
    	return terminal[y][x];
    }
}
