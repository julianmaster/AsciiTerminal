package ui;

	
import java.io.FileInputStream;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AsciiTerminalFX extends Application {
	
	private int width;
	private int height;
//	private
	

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("AsciiTerminal");
		
		Group root = new Group();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		
		Canvas canvas = new Canvas( 512, 512 );
	    root.getChildren().add( canvas );
	 
	    GraphicsContext gc = canvas.getGraphicsContext2D();
//	 
//	    Image earth = new Image(new FileInputStream("earth.png"));
//	    Image sun   = new Image(new FileInputStream("sun.png"));
//	    Image space = new Image(new FileInputStream("space.png"));
//	    
//	    final long startNanoTime = System.nanoTime();
//	    
//	    new AnimationTimer()
//	    {
//	        public void handle(long currentNanoTime)
//	        {
//	            double t = (currentNanoTime - startNanoTime) / 1000000000.0; 
//	 
//	            double x = 232 + 128 * Math.cos(t);
//	            double y = 232 + 128 * Math.sin(t);
//	 
//	            // background image clears canvas
//	            gc.drawImage( space, 0, 0 );
//	            gc.drawImage( earth, x, y );
//	            gc.drawImage( sun, 196, 196 );
//	        }
//	    }.start();
	    
	    
	    Image i = new Image(new FileInputStream("src/assets/wanderlust_16x16.png"));
		
		Color c = i.getPixelReader().getColor(0, 0);
		ColorInput topInput = new ColorInput(0, 0, 100, 100, c);

		Blend blush = new Blend(
                BlendMode.SCREEN,
                null,
                topInput
        );
		
		gc.setEffect(blush);
		
		gc.drawImage(i, 0, 0);
	    
	    
	    stage.show();
	}

	public static void main(String[] args) {
		AsciiTerminalFX.launch(args);
	}
}
