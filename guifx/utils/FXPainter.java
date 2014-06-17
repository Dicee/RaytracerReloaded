package guifx.utils;

import static guifx.MainUI.strings;
import static scene.Raytracer.*;
import guifx.generics.GraphicFactory;

import java.util.Arrays;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import scene.Painter;
import scene.Raytracer;

public class FXPainter extends Stage implements Painter {
	private Raytracer				raytracer;
	private WritableImage			wim;
	private final CheckBox[]		checkBoxes	= new CheckBox[5];
	private final ComboBox<Integer>	depthBox	= new ComboBox<>(FXCollections.observableList(Arrays.asList(0,1,2,3,4,
														5,6,7,8,9,10)));

	public FXPainter(Raytracer raytracer) {
		super(StageStyle.DECORATED);
		
		this.raytracer = raytracer;
		this.wim       = new WritableImage(raytracer.columns,raytracer.lines);
		this.raytracer.addPainter(this);
		
		BorderPane borderPane = new BorderPane();
		ImageView  imageView  = new ImageView(wim);
		GridPane   bottom     = setBottom();
		
		borderPane.setPadding(new Insets(5));
		borderPane.setCenter(imageView);
		borderPane.setBottom(bottom);
		
		Scene scene = new Scene(borderPane,70d + raytracer.columns,130d + raytracer.lines);
		titleProperty().bind(strings.getObservableProperty("renderingFrameTitle"));
		setScene(scene);
		setResizable(false);
		show();
	}
	
	private GridPane setBottom() {
		Button compute = new Button();
		compute.textProperty().bind(strings.getObservableProperty("computeScene"));
		compute.setOnAction(ev -> render());
		BorderPane.setAlignment(compute,Pos.CENTER);

		for (int i=0 ; i<checkBoxes.length ; i++) {
			checkBoxes[i] = new CheckBox();
			checkBoxes[i].setSelected(true);
		}
		checkBoxes[AMBIENT  ].textProperty().bind(strings.getObservableProperty("ambientComponent"));
		checkBoxes[SPECULAR ].textProperty().bind(strings.getObservableProperty("specularComponent"));
		checkBoxes[DIFFUSE  ].textProperty().bind(strings.getObservableProperty("diffuseComponent"));
		checkBoxes[REFLECTED].textProperty().bind(strings.getObservableProperty("reflectedComponent"));
		checkBoxes[REFRACTED].textProperty().bind(strings.getObservableProperty("refractedComponent"));
		
		Label label = new Label();
		label.textProperty().bind(strings.getObservableProperty("depth"));
		label.setFont(GraphicFactory.subtitlesFont);
		
		GridPane gridPane = new GridPane();
		gridPane.add(label,0,0);
		gridPane.add(depthBox,1,0);
		gridPane.addRow(1,checkBoxes[AMBIENT],checkBoxes[SPECULAR],checkBoxes[DIFFUSE]);
		gridPane.addRow(2,checkBoxes[REFLECTED],checkBoxes[REFRACTED]);
		gridPane.add(compute,1,3);
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		GridPane.setFillWidth(compute,true);
		BorderPane.setAlignment(gridPane,Pos.CENTER);
		BorderPane.setMargin(gridPane,new Insets(10));
		
		return gridPane;
	}

	@Override
	public void paint(Color color, int i, int j) {
		wim.getPixelWriter().setColor(i,j,color);
	}

	@Override
	public void render() {
		final Integer depth = depthBox.getValue() == null ? 0 : depthBox.getValue();
		boolean[] display   = new boolean[checkBoxes.length];
		for (int i=0 ; i<display.length ; i++)
			display[i] = checkBoxes[i].isSelected();
		raytracer.render(depth,display);
		
//		new Thread(new Task<Void>() {
//			@Override
//			protected Void call() throws Exception {
//				raytracer.render(depth,display);
//				return null;
//			}
//		}).start();
	}
}
