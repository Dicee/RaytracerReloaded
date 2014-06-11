package guifx.generics.impl.factories;

import static guifx.MainUI.strings;
import guifx.generics.GraphicFactory;
import utils.NamedObject;
import guifx.utils.VectorBuilder;
import javafx.scene.paint.Color;
import java.util.function.Consumer;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import scene.Source;
import utils.math.Point;

public class SourceFXFactory extends GraphicFactory<Source> {
	private static final double PREFERRED_WIDTH = 450;
	private static final double PREFERRED_HEIGHT = 110;	
	
	private final VectorBuilder vectorBuilder = new VectorBuilder();
	private final ColorPicker colorPicker = new ColorPicker();
	
	public SourceFXFactory() {
		this(null);
	}
	
	public SourceFXFactory(Consumer<NamedObject<Source>> consumer) {
		super(strings.getObservableProperty("createSourceTitle"),strings.getObservableProperty("createAction"),
			consumer,PREFERRED_WIDTH,PREFERRED_HEIGHT);
		
		Label colorLabel = new Label();
		Label posLabel   = new Label();
		colorLabel.textProperty().bind(strings.getObservableProperty("sourceColorLabel"));
		posLabel  .textProperty().bind(strings.getObservableProperty("sourcePosLabel"));
		colorLabel.setFont(subtitlesFont);
		posLabel  .setFont(subtitlesFont);
		
		GridPane gridPane = new GridPane();
		gridPane.add(colorLabel,0,0,1,1);		
		gridPane.add(posLabel,0,1,1,1);
		gridPane.add(colorPicker,1,0,10,1);
		gridPane.add(vectorBuilder,1,1,10,1);
		gridPane.setVgap(5);
		gridPane.setHgap(10);
		root.getChildren().add(gridPane);
		
		AnchorPane.setTopAnchor(gridPane,20d);
		AnchorPane.setLeftAnchor(gridPane,30d);
		AnchorPane.setRightAnchor(gridPane,30d);
	}
	
	@Override
	protected NamedObject<Source> create() {
		NamedObject<Source> result = null;
		Point p = vectorBuilder.getPoint();
		if (p != VectorBuilder.errorReturn) {
			float R       = (float) colorPicker.getValue().getRed();
			float G       = (float) colorPicker.getValue().getGreen();
			float B       = (float) colorPicker.getValue().getBlue();
			Source source = new Source(new Color(R,G,B,0),p);
			result        = new NamedObject<>(strings.getObservableProperty("source"),source);
		}
		return result;
	}
}
