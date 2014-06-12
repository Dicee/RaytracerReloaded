package guifx.utils;

import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import scene.Painter;
import scene.Raytracer;
import static guifx.MainUI.strings;

public class FXPainter extends Stage implements Painter {
	private Raytracer		raytracer;
	private WritableImage	wim;

	public FXPainter(Raytracer raytracer) {
		super(StageStyle.DECORATED);
		
		this.raytracer = raytracer;
		this.wim       = new WritableImage(raytracer.columns,raytracer.lines);
		this.raytracer.addPainter(this);
		
		BorderPane borderPane = new BorderPane();
		ImageView imageView   = new ImageView(wim);
		borderPane.setCenter(imageView);
		
		Scene scene = new Scene(borderPane,20d + raytracer.columns,20d + raytracer.lines);
		titleProperty().bind(strings.getObservableProperty("renderingFrameTitle"));
		setScene(scene);
		show();
	}

	@Override
	public void paint(Color color, int i, int j) {
		wim.getPixelWriter().setColor(i,j,color);
	}

	@Override
	public void render() {
		raytracer.render(3);
	}
}
