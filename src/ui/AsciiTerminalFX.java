package ui;

	
import java.io.FileInputStream;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AsciiTerminalFX extends Application {
	
	private int width;
	private int height;
	private int characterWidth;
	private int characterheight;
	
	public AsciiTerminalFX(int width, int height, int characterWidth, int characterheight) {
		this.width = width;
		this.height = height;
		this.characterWidth = characterWidth;
		this.characterheight = characterheight;
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("AsciiTerminal");
		
		Group root = new Group();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		
	    Image image = new Image(new FileInputStream("src/assets/wanderlust_16x16.png"));
	    
	    ImageView modified = new ImageView(createImage(image));
	    
	    
	    System.out.println(width);
	    
	    
	    
	    GridPane gridPane = new GridPane();
	    
//	    gridPane.add(modified, columnIndex, rowIndex);
	    
	    
	    
	    
	    
	    
	    root.getChildren().add(modified);
	    
//	    Color color = Color.BLUE;
//	    double hue = map((color.getHue() + 180) % 360, 0, 360, -1, 1);
//	    colorAdjust.setHue(hue);
//	    double saturation = color.getSaturation();
//	    colorAdjust.setSaturation(saturation);
//	    double brightness = map(color.getBrightness(), 0, 1, -1, 0);
//	    colorAdjust.setBrightness(brightness);
	    
	    stage.show();
	}
	
	public static Image createImage(Image image) {
		PixelReader pixelReader = image.getPixelReader();
		
		Color characterBackgroundColor = pixelReader.getColor(0, 0);
		
		WritableImage writableImage = new WritableImage((int)image.getWidth(), (int)image.getHeight());
		PixelWriter pixelWriter = writableImage.getPixelWriter();
		
		for(int x = 0; x < image.getWidth(); x++) {
			for(int y = 0; y < image.getHeight(); y++) {
				if(pixelReader.getColor(x, y).equals(characterBackgroundColor)) {
					pixelWriter.setColor(x, y, new Color(0, 0, 0, characterBackgroundColor.getOpacity()));
				}
				else {
					pixelWriter.setColor(x, y, pixelReader.getColor(x, y));
				}
			}
		}
		
		
		
		
		return writableImage;
	}
	
	public static double map(double value, double start, double stop, double targetStart, double targetStop) {
        return targetStart + (targetStop - targetStart) * ((value - start) / (stop - start));
   }

	public static void main(String[] args) {
//		AsciiTerminalFX.launch(args);
		new AsciiTerminalFX(5, 5, 16, 16).launch(args);
	}
}
