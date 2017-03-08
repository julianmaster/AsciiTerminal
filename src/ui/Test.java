package ui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Test extends Application {

    Color[] presetColors = new Color[] {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.DODGERBLUE, Color.LIGHTGREY, Color.DARKGRAY, Color.BLACK, Color.WHITE, Color.BROWN};

    Color targetColor;

    Rectangle referenceColorRectangle; // display target color as reference (rgb) 
    ColorAdjust colorAdjust;
    Slider redSlider;
    Slider greenSlider;
    Slider blueSlider;
    ComboBox<Color> colorComboBox;

    @Override
    public void start(Stage primaryStage) {

            BorderPane root = new BorderPane();

            // content
            // -------------------------

            HBox content = new HBox();
            content.setStyle("-fx-background-color:black");

            // create alpha masked image
            Image image = createImage(createAlphaMaskedBall(100));

            // create original imageview
            ImageView original = new ImageView( image);

            // create imageview with color adjustment
            ImageView modified = new ImageView( image);

            // colorAdjust effect
            colorAdjust = new ColorAdjust();
            modified.setEffect(colorAdjust);

            content.getChildren().addAll( original, modified);

            // toolbar
            // -------------------------
            GridPane toolbar = new GridPane();

            // presets: show colors as rectangles in the combobox list, as hex color in the combobox selection
            colorComboBox = new ComboBox<>();
            colorComboBox.getItems().addAll( presetColors);

            colorComboBox.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {

                    updateSliders();

                }

            });
            colorComboBox.setCellFactory(new Callback<ListView<Color>, ListCell<Color>>() {
                @Override
                public ListCell<Color> call(ListView<Color> p) {
                  return new ListCell<Color>() {
                    private final Rectangle rectangle;
                    {
                      setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                      rectangle = new Rectangle(100, 10);
                    }

                    @Override
                    protected void updateItem(Color item, boolean empty) {
                      super.updateItem(item, empty);

                      if (item == null || empty) {
                        setGraphic(null);
                      } else {
                        rectangle.setFill(item);
                        setGraphic(rectangle);
                      }
                    }
                  };
                }
              });

            // sliders, value is initialized later
            redSlider = createSlider( 0,255, 0);
            greenSlider = createSlider( 0,255, 0);
            blueSlider = createSlider( 0,255, 0);

            // reference rectangle in rgb
            referenceColorRectangle = new Rectangle( 0, 0, 100, 100);
            referenceColorRectangle.setStroke(Color.BLACK);

            // listener: get new target color from sliders and apply it 
            ChangeListener<Number> listener = new ChangeListener<Number>() {

                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                    updateColor();

                }

            }; 

            redSlider.valueProperty().addListener(listener);
            greenSlider.valueProperty().addListener(listener);
            blueSlider.valueProperty().addListener(listener);

            // add nodes to gridpane
            toolbar.addRow(0, new Label( "Preset"), colorComboBox);
            toolbar.addRow(1, new Label( "Red"), redSlider);
            toolbar.addRow(2, new Label( "Green"), greenSlider);
            toolbar.addRow(3, new Label( "Blue"), blueSlider);

            toolbar.add(referenceColorRectangle, 2, 0, 1, 4);

            // margin for all gridpane nodes
            for( Node node: toolbar.getChildren()) {
                GridPane.setMargin(node, new Insets(5,5,5,5));
            }

            // layout
            root.setTop(toolbar);
            root.setCenter(content);

            // create scene
            Scene scene = new Scene(root,800,400, Color.BLACK);

            primaryStage.setScene(scene);
            primaryStage.show();

            // set height of combobox list
            colorComboBox.lookup(".list-view").setStyle("-fx-pref-height: 200");

            // select 1st color and implicitly initialize the sliders and colors
            colorComboBox.getSelectionModel().selectFirst();

    }

    private void updateColor() {

        // create target color
        targetColor = Color.rgb( (int) redSlider.getValue(), (int) greenSlider.getValue(), (int) blueSlider.getValue());

        // update reference
        referenceColorRectangle.setFill(targetColor);

        // update colorAdjust
        // see http://stackoverflow.com/questions/31587092/how-to-use-coloradjust-to-set-a-target-color
        double hue = map( (targetColor.getHue() + 180) % 360, 0, 360, -1, 1);
        colorAdjust.setHue(hue);

        // use saturation as it is
        double saturation = targetColor.getSaturation();
        colorAdjust.setSaturation(saturation);

        // we use WHITE in the masked ball creation => inverse brightness
        double brightness = map( targetColor.getBrightness(), 0, 1, -1, 0);
        colorAdjust.setBrightness(brightness);

        // System.out.println("Target color: " + targetColor + ", hue 0..360: " + targetColor.getHue() + ", hue 0..1: " + hue);

    }

    private void updateSliders() {

        Color referenceColor = colorComboBox.getValue();

        redSlider.setValue( map( referenceColor.getRed(), 0, 1, 0, 255));
        greenSlider.setValue( map( referenceColor.getGreen(), 0, 1, 0, 255));
        blueSlider.setValue( map( referenceColor.getBlue(), 0, 1, 0, 255));

    }

    private Slider createSlider( double min, double max, double value) {

        Slider slider = new Slider( min, max, value);
        slider.setPrefWidth(600);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);

        return slider;
    }

    /**
     * Snapshot an image out of a node, consider transparency.
     * @param node
     * @return
     */
    public static Image createImage( Node node) {

        WritableImage wi;

        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT); 

        int imageWidth = (int) node.getBoundsInLocal().getWidth();
        int imageHeight = (int) node.getBoundsInLocal().getHeight();

        wi = new WritableImage( imageWidth, imageHeight);
        node.snapshot(parameters, wi);

        return wi;

    }   

    /**
     * Create an alpha masked ball with gradient colors from White to Black/Transparent. Used e. g. for particles.
     *
     * @param radius
     * @return
     */
    public static Node createAlphaMaskedBall( double radius) {

        Circle ball = new Circle(radius);

        RadialGradient gradient1 = new RadialGradient(0,
            0,
            0,
            0,
            radius,
            false,
            CycleMethod.NO_CYCLE,
            new Stop(0, Color.WHITE.deriveColor(1,1,1,1)),
            new Stop(1, Color.WHITE.deriveColor(1,1,1,0)));

        ball.setFill(gradient1);

        return ball;
    }

   public static double map(double value, double start, double stop, double targetStart, double targetStop) {
        return targetStart + (targetStop - targetStart) * ((value - start) / (stop - start));
   }

    public static void main(String[] args) {
        launch(args);
    }
}
